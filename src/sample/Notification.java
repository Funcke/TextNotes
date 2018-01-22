package sample;

public class Notification {
    private String message;
    private long notificationTime;

    public Notification(String inf, long time) {
        this.message = inf;
        this.notificationTime = time;
    }

    public String getInformation() {
        return this.message;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }
}
