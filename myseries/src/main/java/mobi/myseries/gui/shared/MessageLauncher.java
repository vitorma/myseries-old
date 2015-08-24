package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.message.MessageServiceListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.InvalidBackupVersionException;
import mobi.myseries.domain.repository.series.InvalidDBSourceFileException;
import mobi.myseries.domain.repository.series.NoSeriesToRestoreException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.shared.Validate;
import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.widget.Toast;

import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class MessageLauncher implements MessageServiceListener {

    private Dialog currentDialog;
    private boolean isShowingDialog;
    private Activity activity;

    private FailureDialogBuilder dialogBuilder;

    public MessageLauncher(Activity activity) {
        Validate.isNonNull(activity, "activity");

        this.activity = activity;
        this.dialogBuilder = new FailureDialogBuilder(activity);

        App.messageService().register(this);
    }

    @Override
    public void onFollowingStart(Series series) { /* Do nothing */ }

    @Override
    public void onFollowingSuccess(Series series) {
        String toastMessage = String.format(
                this.activity.getString(R.string.add_success), series.name());

        this.showToastWith(toastMessage);
    }

    @Override
    public void onFollowingError(Series series, Exception e) {
        this.dialogBuilder.setTitle(R.string.add_failed_title);

        if (e instanceof ConnectionFailedException) {
            this.dialogBuilder.setMessage(String.format(this.activity
                    .getString(R.string.add_connection_failed_message), series
                    .name()));
        } else if (e instanceof ParsingFailedException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.parsing_failed_message),
                    series.name()));
        } else {
            this.dialogBuilder.setMessage(e.getMessage());
        }

        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;
    }

    @Override
    public void onUpdateSuccess() {
        this.showToastWith(R.string.update_success_message);
    }

    public void onStop() {
        App.messageService().deregister(this);

        if (this.currentDialog != null) {
            this.isShowingDialog = this.currentDialog.isShowing();
            this.currentDialog.dismiss();
        }
    }

    public Dialog dialog() {
        return this.currentDialog;
    }

    public boolean isShowingDialog() {
        return this.isShowingDialog;
    }

    public void loadState() {
        App.messageService().register(this);

        if ((this.currentDialog != null) && this.isShowingDialog()) {
            this.currentDialog.show();
        }
    }

    private void showToastWith(String message) {
        Toast t = Toast.makeText(this.activity, message, Toast.LENGTH_SHORT);


        t.show();
    }

    private void showToastWith(int messageId) {
        Toast t = Toast.makeText(this.activity, messageId, Toast.LENGTH_SHORT);

        t.show();
    }

    @Override
    public void onBackupSuccess() {
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
    public void onRestoreSuccess() {
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
