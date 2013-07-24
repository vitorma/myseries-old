package mobi.myseries.application.notification;

import mobi.myseries.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class AndroidNotificationDispatcher extends NotificationDispatcher {

    private final NotificationManager notificationManager;
    private final NotificationCompat.Builder updateNotificationBuilder;

    public AndroidNotificationDispatcher(Context context) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.updateNotificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getText(R.string.app))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                // For some reason, setAutoCancel is not working properly with NotificationCompat.Builder,
                // this contentIntent is an workaround for that.
                // http://stackoverflow.com/questions/15033316/notification-setautocanceltrue-doesnt-work
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));
    }

    @Override
    public void notifyTextOnlyNotification(TextOnlyNotification notification) {
        Notification androidNotification = this.updateNotificationBuilder
                .setContentText(notification.message())
                .setProgress(0, 0, false)
                .build();

        this.notificationManager.notify(notification.id(), androidNotification);
    }

    @Override
    public void notifyIndeterminateProgressNotification(
            IndeterminateProgressNotification notification) {
        Notification androidNotification = this.updateNotificationBuilder
                .setContentText(notification.message())
                .setProgress(0, 0, true)
                .build();

        this.notificationManager.notify(notification.id(), androidNotification);
    }

    @Override
    public void notifyDeterminateProgressNotification(
            DeterminateProgressNotification notification) {
        Notification androidNotification = this.updateNotificationBuilder
                .setContentText(notification.message())
                .setProgress(notification.totalProgress(), notification.currentProgress(), false)
                .build();

        this.notificationManager.notify(notification.id(), androidNotification);
    }

    @Override
    public void cancel(mobi.myseries.application.notification.Notification notification) {
        this.notificationManager.cancel(notification.id());
    }
}
