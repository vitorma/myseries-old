package mobi.myseries.application.notification;

public class DeterminateProgressNotification extends Notification {

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyDeterminateProgressNotification(this);
    }
}
