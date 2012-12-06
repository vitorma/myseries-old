package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.message.MessageServiceListener;
import mobi.myseries.application.update.NetworkUnavailableException;
import mobi.myseries.application.update.UpdateTimeoutException;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
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

        App.messageService().registerListener(this);
    }

    @Override
    public void onFollowingStart(Series series) {
        String toastMessage = String.format(activity
                .getString(R.string.follow_toast_message_format), series.name());

        showToastWith(toastMessage);

    }

    @Override
    public void onFollowingSuccess(Series series) {
        String toastMessage = String.format(this.activity.getString(R.string.add_success),
                series.name());
        showToastWith(toastMessage);

    }

    @Override
    public void onFollowingError(Series series, Exception e) {
        this.dialogBuilder.setTitle(R.string.add_failed_title);

        if (e instanceof ConnectionFailedException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.add_connection_failed_message),
                    series.name()));

        } else if (e instanceof SeriesNotFoundException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.add_series_not_found),
                    series.name()));

        } else if (e instanceof ParsingFailedException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.parsing_failed_message),
                    series.name()));
        } else if (e instanceof ConnectionTimeoutException) {
            this.dialogBuilder.setMessage(String.format(
                    this.activity.getString(R.string.connection_timeout_message),
                    series.name()));
        } else {
            this.dialogBuilder.setMessage(e.getMessage());
        }
        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;
    }

    @Override
    public void onUpdateStart() {
        showToastWith(R.string.update_started_message);

    }

    @Override
    public void onUpdateSuccess() {
        showToastWith(R.string.update_success_message);

    }

    @Override
    public void onUpdateError(Exception e) {
        this.dialogBuilder.setTitle(R.string.update_failed_title);

        if (e instanceof ConnectionFailedException) {
            dialogBuilder.setMessage(R.string.update_connection_failed);

        } else if (e instanceof ConnectionTimeoutException) {
            dialogBuilder.setMessage(R.string.update_connection_timeout);

        } else if (e instanceof ParsingFailedException) {
            dialogBuilder.setMessage(R.string.update_parsing_failed);

        } else if (e instanceof SeriesNotFoundException) {
            dialogBuilder.setMessage(String.format(
                    App.context().getString(R.string.update_series_not_found),
                    ((SeriesNotFoundException) e).seriesName()));

        } else if (e instanceof UpdateMetadataUnavailableException) {
            dialogBuilder.setMessage(R.string.update_metadata_unavailable);

        } else if (e instanceof NetworkUnavailableException) {
            dialogBuilder.setMessage(R.string.update_network_unavailable);

        } else if (e instanceof UpdateTimeoutException) {
            dialogBuilder.setMessage(R.string.update_timeout);

        } else {
            dialogBuilder.setMessage(e.getMessage());

        }

        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;

    }

    public void onStop() {
        App.messageService().deregisterListener(this);
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
        App.messageService().registerListener(this);
        if ((currentDialog != null) && isShowingDialog()) {
            currentDialog.show();
        }
    }

    private void showToastWith(String message) {
        this.toastBuilder.setMessage(message)
                .build().show();
    }

    private void showToastWith(int messageId) {
        this.toastBuilder.setMessage(messageId)
                .build().show();
    }

    @Override
    public void onBackupSucess() {
        // TODO(vitor) turn this a Internacionalizable string
        this.showToastWith("Backup completed!");

    }

    @Override
    public void onBackupFailure(Exception e) {
        // TODO(vitor) handle this exception properly
        this.dialogBuilder.setTitle("The backup operation has failed");
        this.dialogBuilder.setMessage("Something goes wrong!");
        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;
    }

    @Override
    public void onRestoreSucess() {
        this.showToastWith("Restore completed!");

    }

    @Override
    public void onRestoreFailure(Exception e) {
        // TODO(vitor) handle this exception properly
        this.dialogBuilder.setTitle("The backup operation has failed");
        this.dialogBuilder.setMessage("Something goes wrong!");
        Dialog dialog = this.dialogBuilder.build();
        dialog.show();
        this.currentDialog = dialog;

    }

}
