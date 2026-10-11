class FeedingException extends Exception {
    FeedingException(String msg) { super(msg); }
}

interface Feedable {
    void feed(int grams) throws FeedingException;
}

abstract class Animal implements Feedable {
    protected final String name;
    protected int fedGrams = 0;
    Animal(String name) { this.name = name; }
    abstract int dailyNeed();
    @Override public void feed(int grams) throws FeedingException {
        if (grams <= 0) throw new FeedingException(name + ": portion must be positive");
        fedGrams += grams;
        System.out.printf("%-8s fed %d g (%d/%d)%n", name, grams, fedGrams, dailyNeed());
    }
    boolean isHungry() { return fedGrams < dailyNeed(); }
}

class Rabbit extends Animal {
    Rabbit(String name) { super(name); }
    @Override int dailyNeed() { return 150; }
}
class Lion extends Animal {
    Lion(String name) { super(name); }
    @Override int dailyNeed() { return 6000; }
}

public class Zoo {
    public static void main(String[] args) {
        Animal[] animals = { new Rabbit("Thumper"), new Lion("Leo") };
        int[][] portions = { {100, 60}, {4000, -1, 2500} };

        for (int a = 0; a < animals.length; a++) {
            for (int g : portions[a]) {
                try {
                    animals[a].feed(g);
                } catch (FeedingException e) {
                    System.out.println("  skipped: " + e.getMessage());
                }
            }
        }
        for (Animal an : animals) {
            System.out.println(an.name + " still hungry? " + an.isHungry());
        }
    }
}
