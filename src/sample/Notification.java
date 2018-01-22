package sample;

public class Notification {
    private Note information;
    private long notificationTime;

    public Notification(Note inf, long time) {
        this.information = inf;
        this.notificationTime = time;
    }

    public Note getInformation() {
        return this.information;
    }

    public long getNotificationTime() {
        return this.notificationTime;
    }
}
