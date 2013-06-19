package mobi.myseries.application.notification;

public class IndeterminateProgressNotification extends Notification {

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyIndeterminateProgressNotification(this);
    }
}
