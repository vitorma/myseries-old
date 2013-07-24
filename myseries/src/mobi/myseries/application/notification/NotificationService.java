package mobi.myseries.application.notification;

import java.util.Map;

import android.content.Context;
import mobi.myseries.R;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.exception.NetworkUnavailableException;
import mobi.myseries.application.update.exception.UpdateTimeoutException;
import mobi.myseries.application.update.listener.UpdateProgressListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private final Context context;
    private final NotificationLauncher updateNotificationLauncher;

    public NotificationService(Context context, UpdateService updateService) {
        this.context = context;

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
                        context.getString(R.string.checking_for_updates_message)));
    }

    private void notifyUpdateNotNecessary() {
        this.updateNotificationLauncher.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateProgress(int current, int total, Series currentSeries) {
        this.updateNotificationLauncher.launch(
                new DeterminateProgressNotification(
                        UPDATE_NOTIFICATION_ID,
                        context.getString(R.string.update_progress_message, currentSeries.name()),
                        current - 1,  // it is current - 1 because, when updating the first series, it should show an empty progress bar
                        total));
    }

    private void notifyUpdateSuccess() {
        this.updateNotificationLauncher.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateFailed(Exception cause) {
        String causeMessage = this.updateFailedMessageFor(cause);

        String notificationMessage;
        if (causeMessage != null) {
            notificationMessage = context.getString(R.string.update_failed_with_cause, causeMessage);
        } else {
            notificationMessage = context.getString(R.string.update_failed_without_cause);
        }

        this.notifyUpdateWithText(notificationMessage);
    }

    private void notifyUpdateSeriesFailed(Map<Series, Exception> causes) {
        if (causes.size() == 1) {
            Series failedSeries = causes.keySet().iterator().next();
            String errorMessage = this.updateFailedMessageFor(causes.values().iterator().next());

            String notificationMessage;
            if (errorMessage != null) {
                notificationMessage = context.getString(R.string.update_single_series_failed_with_cause,
                                                        failedSeries.name(), errorMessage);
            } else {
                notificationMessage = context.getString(R.string.update_single_series_failed_without_cause,
                                                        failedSeries.name());
            }

            this.notifyUpdateWithText(notificationMessage);
        } else {
            String errorMessage = this.updateFailedMessageFor(causes.values().iterator().next());

            CharSequence notificationMessage;
            if (errorMessage != null) {
                notificationMessage = context.getString(R.string.update_many_series_failed_with_cause, causes.size(), errorMessage);
            } else {
                notificationMessage = context.getString(R.string.update_many_series_failed_without_cause, causes.size());
            }

            this.notifyUpdateWithText(notificationMessage);
        }
    }

    private void notifyUpdateWithText(CharSequence text) {
        this.updateNotificationLauncher.launch(new TextOnlyNotification(UPDATE_NOTIFICATION_ID, text));
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
        public void onUpdateFailure(Exception cause) {
            notifyUpdateFailed(cause);
        }

        @Override
        public void onUpdateSeriesFailure(Map<Series, Exception> causes) {
            notifyUpdateSeriesFailed(causes);
        }
    };

    private String updateFailedMessageFor(Exception e) {
        if (e instanceof ConnectionFailedException) {
            return context.getString(R.string.update_connection_failed);

        } else if (e instanceof ConnectionTimeoutException) {
            return context.getString(R.string.update_connection_timeout);

        } else if (e instanceof ParsingFailedException) {
            return context.getString(R.string.update_parsing_failed);

        } else if (e instanceof SeriesNotFoundException) {
            return context.getString(R.string.update_series_not_found);

        } else if (e instanceof UpdateMetadataUnavailableException) {
            return context.getString(R.string.update_metadata_unavailable);

        } else if (e instanceof NetworkUnavailableException) {
            return context.getString(R.string.update_network_unavailable);

        } else if (e instanceof UpdateTimeoutException) {
            return context.getString(R.string.update_timeout);

        } else {
            //return e.getMessage();
            return null;

        }
    }
}
