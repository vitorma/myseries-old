package mobi.myseries.application.notification;

import android.content.Context;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateProgressListener;
import mobi.myseries.domain.model.Series;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private final NotificationLauncher updateNotificationLauncher;

    public NotificationService(Context context, UpdateService updateService) {
        NotificationDispatcher defaultDispatcher = new AndroidNotificationDispatcher(context);
        this.updateNotificationLauncher = new NotificationLauncher(defaultDispatcher);

        updateService.register(updateListener);
    }

    public void setUpdateNotificationDispatcher(NotificationDispatcher newUpdateNotificationDispatcher) {
        this.updateNotificationLauncher.setDispatcherTo(newUpdateNotificationDispatcher);
    }

    public void removeUpdateNotificationDispatcher(NotificationDispatcher updateNotificationDispatcher) {
        this.updateNotificationLauncher.removeDispatcher(updateNotificationDispatcher);
    }

    private void notifyCheckingForUpdates() {
        this.updateNotificationLauncher.launch(
                new IndeterminateProgressNotification(
                        UPDATE_NOTIFICATION_ID,
                        "Checking for updates..."));  // XXX R.blablabla
    }

    private void notifyUpdateNotNecessary() {
        this.notifyUpdateWithText("None of your followed series have updates.");  // XXX R.blablabla
    }

    private void notifyUpdateProgress(int current, int total, Series currentSeries) {
        this.updateNotificationLauncher.launch(
                new DeterminateProgressNotification(
                        UPDATE_NOTIFICATION_ID,
                        "Updating \"" + currentSeries.name() + "\"",
                        current - 1,  // it is current - 1 because, when updating the first series, it should show an empty progress bar
                        total));
    }

    private void notifyUpdateSuccess() {
        this.updateNotificationLauncher.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateFailed() {
        this.notifyUpdateWithText("Update failed.");  // XXX R.blablabla
    }

    private void notifyUpdateWithText(CharSequence text) {
        this.updateNotificationLauncher.launch(
                new TextOnlyNotification(
                        UPDATE_NOTIFICATION_ID,
                        "None of your followed series have updates."));  // XXX R.blablabla
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
        public void onUpdateProgress(int current, int total, Series currentSeries) {
            notifyUpdateProgress(current, total, currentSeries);
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
