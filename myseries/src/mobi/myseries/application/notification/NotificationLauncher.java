package mobi.myseries.application.notification;

import mobi.myseries.shared.Validate;

public class NotificationLauncher {

    private final NotificationDispatcher defaultDispatcher;
    private volatile NotificationDispatcher currentDispatcher;

    private volatile Notification currentNotification;

    public NotificationLauncher(NotificationDispatcher defaultDispatcher) {
        Validate.isNonNull(defaultDispatcher, "defaultDispatcher");

        this.defaultDispatcher = defaultDispatcher;
        this.currentDispatcher = defaultDispatcher;
    }

    public synchronized void setDispatcherTo(NotificationDispatcher newDispatcher) {
        Validate.isNonNull(newDispatcher, "newDispatcher");

        NotificationDispatcher oldDispatcher = this.currentDispatcher;
        this.currentDispatcher = newDispatcher;

        if (this.currentNotification != null) {
            oldDispatcher.cancel(this.currentNotification);
            newDispatcher.notify(this.currentNotification);
        }
    }

    public synchronized void removeDispatcher(NotificationDispatcher dispatcher) {
        if (dispatcher == this.currentDispatcher && dispatcher != this.defaultDispatcher) {
            this.setDispatcherTo(this.defaultDispatcher);
        }
    }

    public synchronized void launch(Notification notification) {
        Validate.isNonNull(notification, "notification");

        this.currentNotification = notification;
        this.currentDispatcher.notify(this.currentNotification);
    }

    public synchronized void cancel(int notificationId) {
        if (this.currentNotification != null && notificationId == this.currentNotification.id()) {
            Notification lastNotification = this.currentNotification;

            this.currentNotification = null;
            this.currentDispatcher.cancel(lastNotification);
        }
    }
}
