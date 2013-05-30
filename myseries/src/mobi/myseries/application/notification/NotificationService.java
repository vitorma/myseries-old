package mobi.myseries.application.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import mobi.myseries.R;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateProgressListener;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private NotificationManager notificationManager;
    private Notification.Builder updateNotificationBuilder;

    public NotificationService(Context context, UpdateService updateService) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.updateNotificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.actionbar_update)  // XXX R.blablabla
                .setContentTitle("MySeries Update")  // XXX R.blablabla
                .setAutoCancel(true);

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
        this.notifyUpdateWithText("Update finished successfully.");  // XXX R.blablabla
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
