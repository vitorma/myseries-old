package mobi.myseries.application.notification;

public class TextOnlyNotification extends Notification {

    public TextOnlyNotification(int id, CharSequence message) {
        super(id, false, message);
    }

    @Override
    public void notifyVisit(NotificationDispatcher dispatcher) {
        dispatcher.notifyTextOnlyNotification(this);
    }
}
