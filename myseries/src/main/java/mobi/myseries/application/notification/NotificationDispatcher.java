package mobi.myseries.application.notification;

public abstract class NotificationDispatcher {

    public abstract void notifyTextOnlyNotification(TextOnlyNotification notification);
    public abstract void notifyIndeterminateProgressNotification(IndeterminateProgressNotification notification);
    public abstract void notifyDeterminateProgressNotification(DeterminateProgressNotification notification);

    public void notify(Notification notification) {
        notification.notifyVisit(this);
    }

    public abstract void cancel(Notification notification);
}
