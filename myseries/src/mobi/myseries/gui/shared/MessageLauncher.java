package mobi.myseries.gui.shared;

import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.message.MessageServiceListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.InvalidBackupVersionException;
import mobi.myseries.domain.repository.series.InvalidDBSourceFileException;
import mobi.myseries.domain.repository.series.NoSeriesToRestoreException;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.shared.Validate;
import android.app.Activity;
import android.app.Dialog;

public class MessageLauncher implements MessageServiceListener {

    private Dialog currentDialog;
    private boolean isShowingDialog;
    private Activity activity;

    private FailureDialogBuilder dialogBuilder;
    private ToastBuilder toastBuilder;

    public MessageLauncher(Activity activity) {
        Validate.isNonNull(activity, "activity");

        this.activity = activity;
        this.dialogBuilder = new FailureDialogBuilder(activity);
        this.toastBuilder = new ToastBuilder(activity);

        App.messageService().register(this);
    }

    @Override
    public void onFollowingStart(Series series) {
        String toastMessage = String.format(
                activity.getString(R.string.follow_toast_message_format),
                series.name());

        showToastWith(toastMessage);
    }

    @Override
    public void onFollowingSuccess(Series series) {
        String toastMessage = String.format(
                this.activity.getString(R.string.add_success), series.name());

        showToastWith(toastMessage);
    }

    @Override
    public void onFollowingError(Series series, Exception e) {
        this.dialogBuilder.setTitle(R.string.add_failed_title);

        if (e instanceof ConnectionFailedException) {
            this.dialogBuilder.setMessage(String.format(this.activity
                    .getString(R.string.add_connection_failed_message), series
                    .name()));

        } else if (e instanceof SeriesNotFoundException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.add_series_not_found),
                    series.name()));

        } else if (e instanceof ParsingFailedException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.parsing_failed_message),
                    series.name()));

        } else if (e instanceof ConnectionTimeoutException) {
            this.dialogBuilder.setMessage(String.format(this.activity
                    .getString(R.string.connection_timeout_message), series
                    .name()));

        } else {
            this.dialogBuilder.setMessage(e.getMessage());

        }

        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;
    }

    @Override
    public void onUpdateSuccess() {
        showToastWith(R.string.update_success_message);
    }

    public void onStop() {
        App.messageService().deregister(this);

        if (this.currentDialog != null) {
            this.isShowingDialog = this.currentDialog.isShowing();
            this.currentDialog.dismiss();
        }
    }

    public Dialog dialog() {
        return currentDialog;
    }

    public boolean isShowingDialog() {
        return isShowingDialog;
    }

    public void loadState() {
        App.messageService().register(this);

        if ((currentDialog != null) && isShowingDialog()) {
            currentDialog.show();
        }
    }

    private void showToastWith(String message) {
        this.toastBuilder.setMessage(message).build().show();
    }

    private void showToastWith(int messageId) {
        this.toastBuilder.setMessage(messageId).build().show();
    }

    @Override
    public void onBackupSucess() {
        // TODO(vitor) turn this a Internationalizable string
        this.showToastWith(R.string.backup_completed);

    }

    @Override
    public void onBackupFailure(Exception e) {
        if (e instanceof UserRecoverableAuthIOException)
            return;
        if (e instanceof DropboxUnlinkedException)
            return;
        this.dialogBuilder.setTitle(R.string.backup_failed_title);
        this.dialogBuilder.setMessage(R.string.backup_failed_message);
        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;

    }

    @Override
    public void onRestoreSucess() {
        this.showToastWith(R.string.restore_completed);
    }

    @Override
    public void onRestoreFailure(Exception e) {
        if (!((e instanceof UserRecoverableAuthIOException) || e instanceof DropboxUnlinkedException)) {
            this.dialogBuilder.setTitle(R.string.restore_failed_title);
            this.dialogBuilder.setMessage(R.string.restore_failed_message);

            if (e instanceof NoSeriesToRestoreException) {
                this.dialogBuilder.setMessage(R.string.no_series_to_restore);

            } else if (e instanceof InvalidBackupVersionException) {
                this.dialogBuilder
                        .setMessage(R.string.restore_invalid_db_version);

            } else if (e instanceof InvalidDBSourceFileException) {
                this.dialogBuilder.setMessage(R.string.restore_invalid_db_file);

            }

            Dialog dialog = this.dialogBuilder.build();
            dialog.show();
            this.currentDialog = dialog;
        }
    }
}
