package mobi.myseries.gui.shared;

import android.widget.ProgressBar;
import android.widget.TextView;
import mobi.myseries.application.notification.DeterminateProgressNotification;
import mobi.myseries.application.notification.IndeterminateProgressNotification;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.TextOnlyNotification;
import mobi.myseries.shared.Validate;

public class NotificationDispatcherForOrdinaryViews extends NotificationDispatcher {
    
    private final ProgressBar progressBar;
    private final TextView textView;
    private final CharSequence emptyNotificationMessage;

    public NotificationDispatcherForOrdinaryViews(
            TextView notificationTextView,
            ProgressBar notificationProgressBar,
            CharSequence messageForWhenThereAreNotNotifications) {
        Validate.isNonNull(notificationTextView, "notificationTextView");
        Validate.isNonNull(notificationProgressBar, "notificationProgressBar");
        Validate.isNonNull(messageForWhenThereAreNotNotifications, "messageForWhenThereAreNotNotifications");

        this.textView = notificationTextView;
        this.progressBar = notificationProgressBar;
        this.emptyNotificationMessage = messageForWhenThereAreNotNotifications;
    }

    @Override
    public void notifyTextOnlyNotification(TextOnlyNotification notification) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(0);
        progressBar.setProgress(0);

        textView.setText(notification.message());
    }

    @Override
    public void notifyIndeterminateProgressNotification(
            IndeterminateProgressNotification notification) {
        progressBar.setIndeterminate(true);

        textView.setText(notification.message());
    }

    @Override
    public void notifyDeterminateProgressNotification(
            DeterminateProgressNotification notification) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(notification.totalProgress());
        progressBar.setProgress(notification.currentProgress());

        textView.setText(notification.message());
    }

    @Override
    public void cancel(Notification notification) {
        progressBar.setIndeterminate(false);
        progressBar.setMax(0);
        progressBar.setProgress(0);

        textView.setText(emptyNotificationMessage);
    }
}
