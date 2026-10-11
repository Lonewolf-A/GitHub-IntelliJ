 class RecordFormatException extends Exception {
     RecordFormatException(String msg) { super(msg); }
 }

 class Person {
     final String name; final int age; final int score;
     Person(String name, int age, int score) { this.name = name; this.age = age; this.score = score; }
     @Override public String toString() {
         return String.format("%-8s age %d score %d", name, age, score);
     }
 }

 public class RecordProcessor {

     static Person parse(String line) throws RecordFormatException {
         String[] parts = line.split(",");
         if (parts.length != 3) throw new RecordFormatException("expected 3 fields, got " + parts.length);
         String name = parts[0].trim();
         if (name.isEmpty()) throw new RecordFormatException("name is blank");
         try {
             int age = Integer.parseInt(parts[1].trim());
             int score = Integer.parseInt(parts[2].trim());
             if (age < 0 || score < 0 || score > 100)
                 throw new RecordFormatException("age/score out of range");
             return new Person(name, age, score);
         } catch (NumberFormatException e) {
             throw new RecordFormatException("age/score not a number: " + e.getMessage());
         }
     }

     public static void main(String[] args) {
         String[] lines = {
                 "Ada, 30, 88", "Bode, 25, 72", "Chi, twenty, 60",
                 "Dee, 40", ", 22, 91", "Eze, 19, 130"
         };

         int valid = 0, invalid = 0, scoreTotal = 0;
         System.out.println("=== PROCESSING ===");
         for (String line : lines) {
             try {
                 Person p = parse(line);
                 valid++; scoreTotal += p.score;
                 System.out.println("OK   : " + p);
             } catch (RecordFormatException e) {
                 invalid++;
                 System.out.println("SKIP : \"" + line + "\"  (" + e.getMessage() + ")");
             }
         }

         System.out.println("\n=== SUMMARY ===");
         System.out.println("Valid records   : " + valid);
         System.out.println("Invalid records : " + invalid);
         if (valid > 0)
             System.out.printf("Average score    : %.2f%n", (double) scoreTotal / valid);
     }
}
