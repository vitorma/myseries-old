package mobi.myseries.application.notification;

import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.backup.exception.BackupTimeoutException;
import mobi.myseries.application.backup.exception.ExternalStorageNotAvailableException;
import mobi.myseries.application.backup.exception.GoogleDriveCannotCreateFileException;
import mobi.myseries.application.backup.exception.GoogleDriveDownloadException;
import mobi.myseries.application.backup.exception.GoogleDriveException;
import mobi.myseries.application.backup.exception.GoogleDriveFileNotFoundException;
import mobi.myseries.application.backup.exception.GoogleDriveUploadException;
import mobi.myseries.application.backup.exception.RestoreTimeoutException;
import mobi.myseries.application.backup.exception.SDcardException;
import mobi.myseries.application.update.BaseUpdateListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.exception.UpdateTimeoutException;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ParsingFailedException;
import android.content.Context;

import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxLocalStorageFullException;
import com.dropbox.client2.exception.DropboxServerException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class NotificationService {

    private static int UPDATE_NOTIFICATION_ID = 0;

    private final Context context;
    private final NotificationLauncher updateNotificationLauncher;
    private final NotificationLauncher backupNotificationLauncher;
    private final NotificationLauncher restoreNotificationLauncher;

    public NotificationService(Context context, UpdateService updateService,
            BackupService backupService) {
        this.context = context;

        NotificationDispatcher defaultDispatcher = new AndroidNotificationDispatcher(
                context);
        this.updateNotificationLauncher = new NotificationLauncher(
                defaultDispatcher);
        this.backupNotificationLauncher = new NotificationLauncher(
                defaultDispatcher);
        this.restoreNotificationLauncher = new NotificationLauncher(
                defaultDispatcher);

        updateService.register(updateListener);
        backupService.register(backupListener);
    }

    public void setUpdateNotificationDispatcher(
            NotificationDispatcher newUpdateNotificationDispatcher) {
        this.updateNotificationLauncher
                .setDispatcherTo(newUpdateNotificationDispatcher);
    }

    public void removeUpdateNotificationDispatcher(
            NotificationDispatcher updateNotificationDispatcher) {
        this.updateNotificationLauncher
                .removeDispatcher(updateNotificationDispatcher);
    }

    private void notifyCheckingForUpdates() {
        this.updateNotificationLauncher
                .launch(new IndeterminateProgressNotification(
                        UPDATE_NOTIFICATION_ID,
                        context.getString(R.string.checking_for_updates_message)));
    }

    private void notifyUpdateNotNecessary() {
        this.updateNotificationLauncher.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateProgress(int current, int total,
            Series currentSeries) {
        this.updateNotificationLauncher
                .launch(new DeterminateProgressNotification(
                        UPDATE_NOTIFICATION_ID, context.getString(
                                R.string.update_progress_message,
                                currentSeries.name()), current - 1, // it is current - 1 because, when
                                                                    // updating the first series,
                                                                    // it should show an empty progress bar
                        total));
    }

    private void notifyUpdateSuccess() {
        this.updateNotificationLauncher.cancel(UPDATE_NOTIFICATION_ID);
    }

    private void notifyUpdateFailed(Exception cause) {
        String causeMessage = this.updateFailedMessageFor(cause);

        String notificationMessage;
        if (causeMessage != null) {
            notificationMessage = context.getString(
                    R.string.update_failed_with_cause, causeMessage);
        } else {
            notificationMessage = context
                    .getString(R.string.update_failed_without_cause);
        }

        this.notifyUpdateWithText(notificationMessage);
    }

    private void notifyUpdateSeriesFailed(Map<Series, Exception> causes) {
        if (causes.size() == 1) {
            Series failedSeries = causes.keySet().iterator().next();
            String errorMessage = this.updateFailedMessageFor(causes.values()
                    .iterator().next());

            String notificationMessage;
            if (errorMessage != null) {
                notificationMessage = context.getString(
                        R.string.update_single_series_failed_with_cause,
                        failedSeries.name(), errorMessage);
            } else {
                notificationMessage = context.getString(
                        R.string.update_single_series_failed_without_cause,
                        failedSeries.name());
            }

            this.notifyUpdateWithText(notificationMessage);
        } else {
            String errorMessage = this.updateFailedMessageFor(causes.values()
                    .iterator().next());

            CharSequence notificationMessage;
            if (errorMessage != null) {
                notificationMessage = context.getString(
                        R.string.update_many_series_failed_with_cause,
                        causes.size(), errorMessage);
            } else {
                notificationMessage = context.getString(
                        R.string.update_many_series_failed_without_cause,
                        causes.size());
            }

            this.notifyUpdateWithText(notificationMessage);
        }
    }

    private void notifyUpdateWithText(CharSequence text) {
        this.updateNotificationLauncher.launch(new TextOnlyNotification(
                UPDATE_NOTIFICATION_ID, text));
    }

    private UpdateListener updateListener = new BaseUpdateListener() {

        @Override
        public void onCheckingForUpdates() {
            notifyCheckingForUpdates();
        }

        @Override
        public void onUpdateNotNecessary() {
            notifyUpdateNotNecessary();
        }

        @Override
        public void onUpdateProgress(int current, int total,
                Series currentSeries) {
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
            return context.getString(R.string.connection_failed_title);

        } else if (e instanceof ParsingFailedException) {
            return context.getString(R.string.parsing_failed_title);

        } else if (e instanceof NetworkUnavailableException) {
            return context.getString(R.string.network_unavailable_title);

        } else if (e instanceof UpdateTimeoutException) {
            //return context.getString(R.string.update_timeout);
            return context.getString(R.string.connection_failed_title);

        } else {
            // return e.getMessage();
            return null;

        }
    }

// ---------------------BACKUP-------------------------------------------------

    private static int getBackupModeNotificationID(BackupMode mode) {
        return mode.hashCode();

    }

    public void setBackupNotificationDispatcher(
            NotificationDispatcher newBackupNotificationDispatcher) {
        this.backupNotificationLauncher
                .setDispatcherTo(newBackupNotificationDispatcher);
    }

    public void setRestoreNotificationDispatcher(
            NotificationDispatcher newBackupNotificationDispatcher) {
        this.restoreNotificationLauncher
                .setDispatcherTo(newBackupNotificationDispatcher);
    }

    public void removeBackupNotificationDispatcher(
            NotificationDispatcher backupNotificationDispatcher) {
        this.backupNotificationLauncher
                .removeDispatcher(backupNotificationDispatcher);
    }
    public void removeRestoreNotificationDispatcher(
            NotificationDispatcher backupNotificationDispatcher) {
        this.restoreNotificationLauncher
                .removeDispatcher(backupNotificationDispatcher);
    }

    private void notifyRunningBackup(BackupMode mode) {
        this.backupNotificationLauncher
                .launch(new IndeterminateProgressNotification(
                        getBackupModeNotificationID(mode), context.getString(
                                R.string.backup_progress_message, mode.name())));
    }

    private void notifyRunningRestore(BackupMode mode) {
        this.restoreNotificationLauncher
                .launch(new IndeterminateProgressNotification(
                        getBackupModeNotificationID(mode), context.getString(
                                R.string.restore_progress_message, mode.name())));
    }

    private void notifyBackupSuccess(BackupMode mode) {
        this.backupNotificationLauncher
                .cancel(getBackupModeNotificationID(mode));
        String notificationMessage = context.getString(
                R.string.backup_success_message, mode.name());
        this.backupNotificationLauncher.launch(new TextOnlyNotification(
                getBackupModeNotificationID(mode), notificationMessage));
    }

    private void notifyRestoreSuccess(BackupMode mode) {
        this.restoreNotificationLauncher
                .cancel(getBackupModeNotificationID(mode));
        String notificationMessage = context.getString(
                R.string.restore_success_message, mode.name());
        this.restoreNotificationLauncher.launch(new TextOnlyNotification(
                getBackupModeNotificationID(mode), notificationMessage));
    }


    private void notifyBackupFailed(BackupMode mode, Exception cause) {
        String causeMessage = this.backupFailedMessageFor(cause);

        String notificationMessage;
        if (causeMessage != null) {
            notificationMessage = context.getString(
                    R.string.backup_failed_with_cause, causeMessage);
        } else {
            notificationMessage = context
                    .getString(R.string.backup_failed_without_cause);
        }

        this.backupNotificationLauncher.launch(new TextOnlyNotification(
                getBackupModeNotificationID(mode), notificationMessage));
    }

    private void notifyRestoreFailed(BackupMode mode, Exception cause) {
        String causeMessage = this.restoreFailedMessageFor(cause);

        String notificationMessage;
        if (causeMessage != null) {
            notificationMessage = context.getString(
                    R.string.restore_failed_with_cause, causeMessage);
        } else {
            notificationMessage = context
                    .getString(R.string.restore_failed_without_cause);
        }

        this.restoreNotificationLauncher.launch(new TextOnlyNotification(
                getBackupModeNotificationID(mode), notificationMessage));
    }

    private BackupListener backupListener = new BackupListener() {

        @Override
        public void onStart() {

        }

        @Override
        public void onRestoreSucess() {

        }

        @Override
        public void onBackupSucess() {

        }

        @Override
        public void onBackupFailure(BackupMode mode, Exception e) {
            if (e.getCause() instanceof UserRecoverableAuthIOException) {
                backupNotificationLauncher
                        .cancel(getBackupModeNotificationID(mode));
                return;
            } else if (e instanceof DropboxUnlinkedException) {
                backupNotificationLauncher
                        .cancel(getBackupModeNotificationID(mode));
                return;
            }
            notifyBackupFailed(mode, e);
        }

        @Override
        public void onBackupCompleted(BackupMode mode) {
            notifyBackupSuccess(mode);

        }

        @Override
        public void onBackupRunning(BackupMode mode) {
            notifyRunningBackup(mode);

        }

        @Override
        public void onRestoreFailure(BackupMode mode, Exception e) {
            if (e.getCause() instanceof UserRecoverableAuthIOException) {
                restoreNotificationLauncher
                        .cancel(getBackupModeNotificationID(mode));
                return;
            } else if (e instanceof DropboxUnlinkedException) {
                restoreNotificationLauncher
                        .cancel(getBackupModeNotificationID(mode));
                return;
            }
            notifyRestoreFailed(mode, e);
        }

        @Override
        public void onRestoreRunning(BackupMode mode) {
            notifyRunningRestore(mode);

        }

        @Override
        public void onRestoreCompleted(BackupMode mode) {
            notifyRestoreSuccess(mode);

        }
    };

    private String backupFailedMessageFor(Exception e) {
        if (e instanceof ConnectionFailedException) {
            return context.getString(R.string.backup_connection_failed);

        } else if (e instanceof NetworkUnavailableException) {
            return context.getString(R.string.backup_network_unavailable);

        } else if (e instanceof BackupTimeoutException) {
            return context.getString(R.string.backup_timeout);

        } else if (e instanceof ExternalStorageNotAvailableException) {
            return context.getString(R.string.backup_sdcard_not_available);

        } else if (e instanceof DropboxLocalStorageFullException) {
            return context.getString(R.string.backup_dropbox_full);

        } else if (e instanceof DropboxException) {
            return context.getString(R.string.backup_dropbox_error);

        } else if (e instanceof GoogleDriveCannotCreateFileException) {
            return context
                    .getString(R.string.backup_google_drive_cannot_create_file);

        } else if (e instanceof GoogleDriveUploadException) {
            return context.getString(R.string.backup_google_drive_upload_error);

        } else if (e instanceof GoogleDriveException) {
            return context.getString(R.string.backup_google_drive_error);

        } else {
             e.printStackTrace();
            return null;

        }
    }

    private String restoreFailedMessageFor(Exception e) {
        if (e instanceof ConnectionFailedException) {
            return context.getString(R.string.restore_connection_failed);


        } else if (e instanceof NetworkUnavailableException) {
            return context.getString(R.string.restore_network_unavailable);

        } else if (e instanceof RestoreTimeoutException) {
            return context.getString(R.string.restore_timeout);

        } else if (e instanceof ExternalStorageNotAvailableException) {
            return context.getString(R.string.restore_sdcard_not_available);

        } else if (e instanceof SDcardException) {
            return context.getString(R.string.restore_sdcard_file_not_found);

        } else if (e instanceof DropboxServerException
                   && ((DropboxServerException) e).error == DropboxServerException._404_NOT_FOUND) {
            return context.getString(R.string.restore_dropbox_file_not_found);

        } else if (e instanceof DropboxException) {
            return context.getString(R.string.restore_dropbox_error);

        } else if (e instanceof GoogleDriveFileNotFoundException) {
            return context
                    .getString(R.string.restore_google_drive_file_not_found);

        } else if (e instanceof GoogleDriveDownloadException) {
            return context
                    .getString(R.string.restore_google_drive_download_error);

        } else if (e instanceof GoogleDriveException) {
            e.getCause().printStackTrace();
            return context.getString(R.string.restore_google_drive_error);

        } else {
            // return e.getMessage();
            return null;

        }
    }
}
