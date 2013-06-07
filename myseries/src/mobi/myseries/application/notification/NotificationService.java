package mobi.myseries.application.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import mobi.myseries.R;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateProgressListener;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private NotificationManager notificationManager;
    private NotificationCompat.Builder updateNotificationBuilder;

    public NotificationService(Context context, UpdateService updateService) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.updateNotificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.actionbar_update)  // XXX R.blablabla
                .setContentTitle("MySeries Update")  // XXX R.blablabla
                .setPriority(NotificationCompat.PRIORITY_LOW)
                // For some reason, setAutoCancel is not working with NotificationCompat.Builder,
                // this contentIntent is an workaround for that.
                // http://stackoverflow.com/questions/15033316/notification-setautocanceltrue-doesnt-work
                //.setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));

        updateService.register(updateListener);
    }

    private void notifyCheckingForUpdates() {
        Notification notification = this.updateNotificationBuilder
                .setContentText("Checking for updates...")  // XXX R.blablabla
                .setProgress(0, 0, true)
                .build();

        this.notificationManager.notify(UPDATE_NOTIFICATION_ID, notification);
    }

    private void notifyUpdateNotNecessary() {
        this.notifyUpdateWithText("None of your followed series have updates.");  // XXX R.blablabla
    }

    private void notifyUpdateProgress(int current, int total) {
        Notification notification = this.updateNotificationBuilder
                .setContentText("Updating " + current + " of " + total)  // XXX R.blablabla
                // current - 1 because, when updating the first series, it should show an empty progress bar
                .setProgress(total, current - 1, false)
                .build();

        this.notificationManager.notify(UPDATE_NOTIFICATION_ID, notification);
    }

    private void notifyUpdateSuccess() {
        this.notificationManager.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateFailed() {
        this.notifyUpdateWithText("Update failed.");  // XXX R.blablabla
    }

    private void notifyUpdateWithText(CharSequence text) {
        Notification notification = this.updateNotificationBuilder
                .setContentText(text)
                .setProgress(0, 0, false)
                .build();

        this.notificationManager.notify(UPDATE_NOTIFICATION_ID, notification);
    }

    private UpdateProgressListener updateListener = new UpdateProgressListener() {

        @Override
        public void onCheckingForUpdates() {
            notifyCheckingForUpdates();
        }

        @Override
        public void onUpdateNotNecessary() {
            notifyUpdateNotNecessary();
        }

        @Override
        public void onUpdateProgress(int current, int total) {
            notifyUpdateProgress(current, total);
        }

        @Override
        public void onUpdateSuccess() {
            notifyUpdateSuccess();
        }

        @Override
        public void onUpdateFailure(Exception e) {
            notifyUpdateFailed();
        }
    };
}
