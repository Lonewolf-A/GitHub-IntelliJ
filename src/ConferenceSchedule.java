import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class ConferenceSchedule {
    public static void main(String[] args) {
        LocalDateTime cursor = LocalDateTime.of(2026, 6, 8, 9, 0);
        LocalTime dayEnd = LocalTime.of(17, 0);
        int breakMinutes = 15;

        String[] titles = {
                "Keynote: Modern Java", "Streams Deep Dive", "Records & Patterns",
                "Virtual Threads", "AI-Assisted Coding", "Closing Panel"
        };
        int[] durations = { 60, 90, 45, 75, 90, 60 };

        DateTimeFormatter t = DateTimeFormatter.ofPattern("EEE HH:mm");
        LocalDateTime firstStart = cursor;

        System.out.println("=== CONFERENCE SCHEDULE ===");
        for (int i = 0; i < titles.length; i++) {
            LocalDateTime start = cursor;
            LocalDateTime end = start.plusMinutes(durations[i]);
            String flag = end.toLocalTime().isAfter(dayEnd) ? "  [RUNS LATE]" : "";
            System.out.printf("%s - %s  %-24s (%dm)%s%n",
                    start.format(t), end.format(t), titles[i], durations[i], flag);
            cursor = end.plusMinutes(breakMinutes);
        }

        LocalDateTime lastEnd = cursor.minusMinutes(breakMinutes);
        long totalMinutes = ChronoUnit.MINUTES.between(firstStart, lastEnd);
        System.out.printf("%nFirst session : %s%n", firstStart.format(t));
        System.out.printf("Last ends     : %s%n", lastEnd.format(t));
        System.out.printf("Total span    : %dh %dm (including breaks)%n",
                totalMinutes / 60, totalMinutes % 60);
    }

}
