package mobi.myseries.gui.shared;

import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BaseBackupListener;
import mobi.myseries.application.backup.exception.GoogleDriveException;
import mobi.myseries.application.notification.DeterminateProgressNotification;
import mobi.myseries.application.notification.IndeterminateProgressNotification;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.TextOnlyNotification;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RestoreProgressDialogBuilder {
    private Context context;
    private ProgressBar progressBar;
    private TextView messageTextView;
    private BaseBackupListener restoreListerner;
    private Button cancelButton;

    public RestoreProgressDialogBuilder(Context context) {
        this.context = context;
    }

    public Context context() {
        return this.context;
    }


    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_backup_restore_progress);

        this.setupProgressBar(dialog);
        this.setupStatusMessage(dialog);
        this.setupCancelButton(dialog);
        this.setupRestoreListener(dialog);

        App.notificationService().setRestoreNotificationDispatcher(restoreNotificationDispatcher);
        return dialog;
    }

    private void setupRestoreListener(final Dialog dialog) {
        this.restoreListerner = new BaseBackupListener() {
            @Override
            public void onRestoreRunning (BackupMode mode) {
                  super.onRestoreRunning(mode);
                  progressBar.setIndeterminate(true);
                  messageTextView.setText(context.getString(R.string.restore_progress_message, mode.name()));
            }
            @Override
            public void onRestoreCompleted(BackupMode mode) {
                super.onRestoreCompleted(mode);
                App.notificationService().removeRestoreNotificationDispatcher(restoreNotificationDispatcher);
                dialog.dismiss();
                
            }

            @Override
            public void onRestoreProgress(int current, int total) {
                super.onRestoreProgress(current, total);
                messageTextView.setText(R.string.restore_downloading_series_message);
                progressBar.setIndeterminate(false);
                progressBar.setMax(total);
                progressBar.setProgress(current);
            }

            @Override
            public void onRestorePosterDownloadProgress(int current, int total) {
                super.onRestorePosterDownloadProgress(current, total);
                messageTextView.setText(R.string.restore_downloading_posters_message);
                progressBar.setIndeterminate(false);
                progressBar.setMax(total);
                progressBar.setProgress(current);
            }
            @Override
            public void onRestoreFailure(BackupMode mode, Exception e) {
                if ((e instanceof GoogleDriveException  && (e.getCause() instanceof UserRecoverableAuthIOException)) 
                        || (e instanceof DropboxUnlinkedException)) {
                        dialog.dismiss();
                }
                progressBar.setProgress(0);
                cancelButton.setText(R.string.ok);
            }
        };
        App.backupService().register(restoreListerner);
    }

    private void setupCancelButton(final Dialog dialog) {
        cancelButton = (Button) dialog.findViewById(R.id.button);
        cancelButton.setText(R.string.cancel);

        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                App.notificationService().removeRestoreNotificationDispatcher(restoreNotificationDispatcher);
                App.backupService().cancelCurrentRestore();
            }
        });
    }


    private void setupStatusMessage(Dialog dialog) {
        this.messageTextView = (TextView) dialog.findViewById(R.id.RestoreStatusMessage);

    }

    private void setupProgressBar(Dialog dialog) {
        this.progressBar = (ProgressBar) dialog.findViewById(R.id.RestoreProgressBar);
    }

    private final NotificationDispatcher restoreNotificationDispatcher = new NotificationDispatcher() {

        @Override
        public void notifyTextOnlyNotification(TextOnlyNotification notification) {
            messageTextView.setText(notification.message());
        }

        @Override
        public void notifyIndeterminateProgressNotification(
                IndeterminateProgressNotification notification) {
            messageTextView.setText(notification.message());
        }


        @Override
        public void cancel(Notification notification) {
            messageTextView.setText(notification.message());
        }

        @Override
        public void notifyDeterminateProgressNotification(
                DeterminateProgressNotification notification) {
            messageTextView.setText(notification.message());
        }
    };
}
