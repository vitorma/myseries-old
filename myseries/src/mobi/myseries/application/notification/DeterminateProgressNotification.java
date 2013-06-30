package mobi.myseries.application.notification;

public class DeterminateProgressNotification extends Notification {

    private final int currentProgress;
    private final int totalProgress;

    public DeterminateProgressNotification(int id, CharSequence message, int currentProgress, int totalProgress) {
        super(id, true, message);

        this.currentProgress = currentProgress;
        this.totalProgress = totalProgress;
    }

    public int currentProgress() {
        return this.currentProgress;
    }

    public int totalProgress() {
        return this.totalProgress;
    }

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyDeterminateProgressNotification(this);
    }
}
