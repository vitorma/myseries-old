package mobi.myseries.application.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import mobi.myseries.R;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateListener;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private NotificationManager notificationManager;

    private Notification.Builder updateNotificationBuilder;

    public NotificationService(Context context, UpdateService updateService) {
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        this.updateNotificationBuilder = new Notification.Builder(context)
                .setSmallIcon(R.drawable.actionbar_update)
                .setContentTitle("MySeries Update")  // XXX R.blablabla
                .setAutoCancel(true);

        updateService.register(updateListener);
        Log.d(NotificationService.class.getName(), "Notification service instatiated");  // XXX
    }

    private void notifyUpdateStart() {
        this.notifyUpdateWithText("Update started.");  // XXX R.blablabla
    }

    private void notifyUpdateNotNecessary() {
        this.notifyUpdateWithText("None of your followed series have updates.");  // XXX R.blablabla
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
                .build();

        this.notificationManager.notify(UPDATE_NOTIFICATION_ID, notification);
    }

    private UpdateListener updateListener = new UpdateListener() {  // FIXME(Gabriel): use UpdateProgressListener
        
        @Override
        public void onUpdateStart() {
            notifyUpdateStart();
        }
        
        @Override
        public void onUpdateNotNecessary() {
            notifyUpdateNotNecessary();
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
