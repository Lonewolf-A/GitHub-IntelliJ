import java.util.*;
import java.util.stream.*;

public class SalesAnalytics {

    record Order(String customer, String region, String product, int qty, double unitPrice) {
        double total() { return qty * unitPrice; }
    }

    public static void main(String[] args) {
        List<Order> orders = List.of(
                new Order("Ada",  "WEST", "Keyboard", 2, 45.00),
                new Order("Bode", "EAST", "Monitor",  1, 189.00),
                new Order("Chi",  "WEST", "Mouse",    5, 19.99),
                new Order("Ada",  "WEST", "Hub",      3, 34.50),
                new Order("Dee",  "EAST", "Keyboard", 1, 45.00),
                new Order("Eze",  "NORTH","Monitor",  2, 189.00),
                new Order("Bode", "EAST", "Mouse",    4, 19.99)
        );

        double grand = orders.stream().mapToDouble(Order::total).sum();
        System.out.printf("Grand total revenue : $%.2f%n", grand);

        Map<String, Double> byRegion = orders.stream()
                .collect(Collectors.groupingBy(Order::region,
                        Collectors.summingDouble(Order::total)));
        System.out.println("Revenue by region   :");
        new TreeMap<>(byRegion).forEach((r, v) -> System.out.printf("  %-6s $%.2f%n", r, v));

        Map<String, Integer> unitsByProduct = orders.stream()
                .collect(Collectors.groupingBy(Order::product,
                        Collectors.summingInt(Order::qty)));
        System.out.println("Units per product   :");
        new TreeMap<>(unitsByProduct).forEach((p, u) -> System.out.printf("  %-9s %d%n", p, u));

        Map<String, Double> spendByCustomer = orders.stream()
                .collect(Collectors.groupingBy(Order::customer,
                        Collectors.summingDouble(Order::total)));
        String top = spendByCustomer.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("<none>");
        System.out.printf("Top customer        : %s ($%.2f)%n", top, spendByCustomer.get(top));

        String bigOrders = orders.stream()
                .filter(o -> o.total() > 100)
                .map(o -> o.customer() + ":" + o.product())
                .collect(Collectors.joining(", "));
        System.out.println("Orders over $100    : " + bigOrders);
    }
}
