package mobi.myseries.application.notification;

public class IndeterminateProgressNotification extends Notification {

    public IndeterminateProgressNotification(int id, CharSequence message) {
        super(id, message);
    }

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyIndeterminateProgressNotification(this);
    }
}
