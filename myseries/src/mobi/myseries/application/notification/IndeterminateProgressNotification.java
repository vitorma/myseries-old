package mobi.myseries.application.notification;

public class IndeterminateProgressNotification extends Notification {

    public IndeterminateProgressNotification(int id, CharSequence message) {
        super(id, true, message);
    }

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyIndeterminateProgressNotification(this);
    }
}
