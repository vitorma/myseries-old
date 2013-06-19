package mobi.myseries.application.notification;

public class TextOnlyNotification extends Notification {

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyTextOnlyNotification(this);
    }
}
