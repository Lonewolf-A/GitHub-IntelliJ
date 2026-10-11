import java.util.ArrayList;

interface Channel {
    String send(String message);
    default boolean isUrgentCapable() { return false; }
}

class EmailChannel implements Channel {
    private final String address;
    EmailChannel(String address) { this.address = address; }
    @Override public String send(String message) { return "EMAIL -> " + address + " : " + message; }
}

class SmsChannel implements Channel {
    private final String number;
    SmsChannel(String number) { this.number = number; }
    @Override public String send(String message) { return "SMS   -> " + number + " : " + message; }
    @Override public boolean isUrgentCapable() { return true; }
}

class PushChannel implements Channel {
    private final String deviceId;
    PushChannel(String deviceId) { this.deviceId = deviceId; }
    @Override public String send(String message) { return "PUSH  -> " + deviceId + " : " + message; }
    @Override public boolean isUrgentCapable() { return true; }
}

class Dispatcher {
    private final ArrayList<Channel> channels = new ArrayList<>();
    void register(Channel c) { channels.add(c); }
    void broadcast(String message) {
        for (Channel c : channels) System.out.println(c.send(message));
    }
    void broadcastUrgent(String message) {
        for (Channel c : channels)
            if (c.isUrgentCapable()) System.out.println("[URGENT] " + c.send(message));
    }
}

public class NotificationCenter {
    public static void main(String[] args) {
        Dispatcher d = new Dispatcher();
        d.register(new EmailChannel("ada@example.com"));
        d.register(new SmsChannel("+234-800-0000"));
        d.register(new PushChannel("device-42"));
        d.register(msg -> "LOG   -> console : " + msg);   // lambda channel

        System.out.println("--- broadcast ---");
        d.broadcast("Server maintenance at 22:00");

        System.out.println("--- urgent only ---");
        d.broadcastUrgent("Database down!");
    }
}
