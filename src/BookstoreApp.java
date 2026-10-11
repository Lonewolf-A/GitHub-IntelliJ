import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.*;

public class BookstoreApp {

    record Book(String isbn, String title, String genre, double price) {}

    static final class OutOfStockException extends Exception {
        OutOfStockException(String m) { super(m); }
    }

    interface Discount {
        double apply(double subtotal);
        String label();
    }
    static final class NoDiscount implements Discount {
        public double apply(double s) { return s; }
        public String label() { return "none"; }
    }
    static final class PercentOff implements Discount {
        private final double pct;
        PercentOff(double pct) { this.pct = pct; }
        public double apply(double s) { return s * (1 - pct / 100); }
        public String label() { return (int) pct + "% off"; }
    }
    static final class BulkDeal implements Discount {
        public double apply(double s) { return s - 5 * Math.floor(s / 50); }
        public String label() { return "$5 per $50"; }
    }

    record LineItem(Book book, int qty) {
        double total() { return book.price() * qty; }
    }

    static final class Order {
        private static int nextId = 1000;
        private final int id;
        private final LocalDateTime placedAt;
        private final List<LineItem> items;
        private final Discount discount;

        Order(List<LineItem> items, Discount discount, LocalDateTime placedAt) {
            this.id = nextId++;
            this.items = List.copyOf(items);
            this.discount = discount;
            this.placedAt = placedAt;
        }
        int id() { return id; }
        LocalDateTime placedAt() { return placedAt; }
        List<LineItem> items() { return items; }
        double subtotal() { return items.stream().mapToDouble(LineItem::total).sum(); }
        double total() { return discount.apply(subtotal()); }
        String discountLabel() { return discount.label(); }
    }

    static final class Bookstore {
        private final Map<String, Book> catalogue = new LinkedHashMap<>();
        private final Map<String, Integer> stock = new HashMap<>();
        private final List<Order> orders = new ArrayList<>();

        void addBook(Book b, int qty) {
            catalogue.put(b.isbn(), b);
            stock.merge(b.isbn(), qty, Integer::sum);
        }

        Order checkout(Map<String, Integer> cart, Discount discount, LocalDateTime when)
                throws OutOfStockException {
            List<LineItem> lines = new ArrayList<>();
            for (var entry : cart.entrySet()) {
                Book b = catalogue.get(entry.getKey());
                if (b == null) throw new OutOfStockException("unknown ISBN " + entry.getKey());
                int want = entry.getValue();
                int have = stock.getOrDefault(b.isbn(), 0);
                if (want > have) {
                    throw new OutOfStockException(
                            "\"" + b.title() + "\": wanted " + want + ", only " + have + " in stock");
                }
                lines.add(new LineItem(b, want));
            }
            for (LineItem li : lines) stock.merge(li.book().isbn(), -li.qty(), Integer::sum);
            Order o = new Order(lines, discount, when);
            orders.add(o);
            return o;
        }

        double totalRevenue() {
            return orders.stream().mapToDouble(Order::total).sum();
        }
        Map<String, Double> revenueByGenre() {
            return orders.stream()
                    .flatMap(o -> o.items().stream())
                    .collect(Collectors.groupingBy(li -> li.book().genre(),
                            Collectors.summingDouble(LineItem::total)));
        }
        List<String> bestSellers(int topN) {
            return orders.stream()
                    .flatMap(o -> o.items().stream())
                    .collect(Collectors.groupingBy(li -> li.book().title(),
                            Collectors.summingInt(LineItem::qty)))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(topN)
                    .map(e -> e.getKey() + " (" + e.getValue() + ")")
                    .toList();
        }
        int lowStockCount(int threshold) {
            return (int) stock.values().stream().filter(q -> q < threshold).count();
        }
    }

    public static void main(String[] args) throws OutOfStockException {
        Bookstore store = new Bookstore();
        store.addBook(new Book("978-1", "Effective Java", "tech", 45.00), 10);
        store.addBook(new Book("978-2", "Clean Code", "tech", 38.00), 4);
        store.addBook(new Book("978-3", "Dune", "sci-fi", 18.50), 6);
        store.addBook(new Book("978-4", "The Hobbit", "fantasy", 14.00), 8);

        var fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime t0 = LocalDateTime.of(2026, 6, 8, 10, 0);

        System.out.println("=== ORDERS ===");
        Order o1 = store.checkout(cart("978-1", 2, "978-3", 1), new PercentOff(10), t0);
        printOrder(o1, fmt);

        Order o2 = store.checkout(cart("978-4", 3), new BulkDeal(), t0.plusHours(2));
        printOrder(o2, fmt);

        try {
            store.checkout(cart("978-2", 5), new NoDiscount(), t0.plusHours(3));
        } catch (OutOfStockException e) {
            System.out.println("REJECTED order: " + e.getMessage());
        }

        Order o3 = store.checkout(cart("978-1", 1, "978-4", 2), new NoDiscount(), t0.plusHours(4));
        printOrder(o3, fmt);

        System.out.println("\n=== REPORTS ===");
        System.out.printf("Total revenue      : $%.2f%n", store.totalRevenue());
        System.out.println("Revenue by genre   :");
        new TreeMap<>(store.revenueByGenre())
                .forEach((g, v) -> System.out.printf("  %-9s $%.2f%n", g, v));
        System.out.println("Best sellers       : " + store.bestSellers(3));
        System.out.println("Low-stock titles (<5): " + store.lowStockCount(5));
    }

    static Map<String, Integer> cart(Object... pairs) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) m.put((String) pairs[i], (Integer) pairs[i + 1]);
        return m;
    }

    static void printOrder(Order o, DateTimeFormatter fmt) {
        System.out.printf("Order #%d  %s  [%s]%n", o.id(), o.placedAt().format(fmt), o.discountLabel());
        for (LineItem li : o.items()) {
            System.out.printf("  %-16s x%d  $%.2f%n", li.book().title(), li.qty(), li.total());
        }
        System.out.printf("  subtotal $%.2f -> total $%.2f%n", o.subtotal(), o.total());
    }
}
