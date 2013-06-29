package mobi.myseries.application.notification;

public abstract class Notification {

    private final int id;
    private final boolean isContinuous;
    private final CharSequence message;

    protected Notification(int id, boolean isContinuous, CharSequence message) {
        this.id = id;
        this.isContinuous = isContinuous;
        this.message = message;
    }

    public int id() {
        return this.id;
    }

    public boolean isContinuous() {
        return this.isContinuous;
    }

    public CharSequence message() {
        return this.message;
    }

    public abstract void notifyVisit(NotificationDispatcher dispatcher);
}
