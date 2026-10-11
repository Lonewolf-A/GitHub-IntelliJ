abstract class MediaItem {
    private final String title;
    private boolean checkedOut = false;

    MediaItem(String title) { this.title = title; }

    String getTitle() { return title; }
    boolean isCheckedOut() { return checkedOut; }

    void checkOut() {
        if (checkedOut) System.out.println("\"" + title + "\" is already out.");
        else { checkedOut = true; System.out.println("Checked out: " + title); }
    }

    void checkIn(int daysLate) {
        if (!checkedOut) { System.out.println("\"" + title + "\" was not out."); return; }
        checkedOut = false;
        double fee = Math.max(daysLate, 0) * lateFeePerDay();
        System.out.printf("Returned: %-22s %d days late  fee $%.2f%n", title, Math.max(daysLate, 0), fee);
    }

    abstract double lateFeePerDay();

    @Override public String toString() {
        return String.format("%-22s [%s]  ($%.2f/day late)",
                title, checkedOut ? "OUT" : "IN", lateFeePerDay());
    }
}

class Book extends MediaItem {
    Book(String title) { super(title); }
    @Override double lateFeePerDay() { return 0.25; }
}
class DVD extends MediaItem {
    DVD(String title) { super(title); }
    @Override double lateFeePerDay() { return 1.00; }
}
class Magazine extends MediaItem {
    Magazine(String title) { super(title); }
    @Override double lateFeePerDay() { return 0.10; }
}

public class MediaLibrary {
    public static void main(String[] args) {
        MediaItem[] shelf = {
                new Book("Effective Java"),
                new DVD("The Matrix"),
                new Magazine("Nature - Jan")
        };

        System.out.println("=== CATALOGUE ===");
        for (MediaItem m : shelf) System.out.println(m);

        System.out.println("\n=== ACTIVITY ===");
        shelf[0].checkOut();
        shelf[1].checkOut();
        shelf[1].checkOut();
        shelf[0].checkIn(3);
        shelf[1].checkIn(5);
        shelf[2].checkIn(2);

        double totalFees = 0;
        int[] lateDays = {3, 5, 0};
        for (int i = 0; i < shelf.length; i++) {
            totalFees += lateDays[i] * shelf[i].lateFeePerDay();
        }
        System.out.printf("%nTotal late fees this session: $%.2f%n", totalFees);
    }
}
