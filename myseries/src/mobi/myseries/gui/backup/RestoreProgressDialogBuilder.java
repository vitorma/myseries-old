package mobi.myseries.gui.backup;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BaseBackupListener;
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

        dialog.setContentView(R.layout.dialog_restore_progress);

        this.setupTitleFor(dialog);
        this.setupProgressBar(dialog);
        this.setupStatusMessage(dialog);
        this.setupCancelButton(dialog);
        this.setupRestoreListener(dialog);
        
        App.notificationService().removeRestoreNotificationDispatcher(restoreNotificationDispatcher);

        return dialog;
    }

    private void setupRestoreListener(Dialog dialog) {
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
                cancelButton.setText(R.string.ok);
                messageTextView.setText(context.getString(R.string.restore_success_message, mode.name()));
                progressBar.setIndeterminate(false);
                progressBar.setProgress(0);
            }

            @Override
            public void onRestoreProgress(int current, int total) {
                super.onRestoreProgress(current, total);
                messageTextView.setText(R.string.restore_downloading_series_message);
                progressBar.setIndeterminate(false);
                progressBar.setMax(total);
                progressBar.setProgress(current - 1);
            }
            
            @Override
            public void onRestorePosterDownloadProgress(int current, int total) {
                super.onRestorePosterDownloadProgress(current, total);
                messageTextView.setText(R.string.restore_downloading_posters_message);
                progressBar.setIndeterminate(false);
                progressBar.setMax(total);
                progressBar.setProgress(current - 1);
            }
            @Override
            public void onRestoreFailure(BackupMode mode, Exception e) {
                super.onRestoreFailure(mode, e);
                progressBar.setProgress(0);
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

    private void setupTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(R.string.restore);

        View titleDivider = dialog.findViewById(R.id.titleDivider);
        titleDivider.setVisibility(View.VISIBLE);
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
            // TODO Auto-generated method stub
            
        }
    };

}
