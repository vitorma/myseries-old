package mobi.myseries.application.notification;

public abstract class Notification {

    private final int id;
    private final CharSequence message;

    protected Notification(int id, CharSequence message) {
        this.id = id;
        this.message = message;
    }

    public int id() {
        return this.id;
    }

    public CharSequence message() {
        return this.message;
    }

    public abstract void notifyVisit(NotificationDispatcher dispatcher);
}
