package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

public class BackupDialogBuilder {
    private Context context;
    private OnBackupOperationRequestListener backupOperationListener;
    private RadioGroup radioGroup;

    
    public BackupDialogBuilder(Context context) {
        this.context = context;
    }
    
    public Context context() {
        return this.context;
    }

    public BackupDialogBuilder setOnBackupOperationRequestListener(OnBackupOperationRequestListener listener) {
        this.backupOperationListener = listener;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_restore);

        this.setupTitleFor(dialog);
        this.setupRadioGroup(dialog);
        this.setupBackupButtonFor(dialog);
        this.setupRestoreButtonFor(dialog);

        return dialog;
    }

    private void setupRestoreButtonFor(Dialog dialog) {
        Button restoreButton = (Button) dialog.findViewById(R.id.restoreButton);
        restoreButton.setText(R.string.restore_button);

        restoreButton.setOnClickListener(
            this.restoreButtonListenerFor(dialog));
    }

    private void setupBackupButtonFor(Dialog dialog) {
        Button backupButton = (Button) dialog.findViewById(R.id.backupButton);
        backupButton.setText(R.string.backup_button);

        backupButton.setOnClickListener(
            this.backupButtonListenerFor(dialog));
    }

private OnClickListener backupButtonListenerFor(final Dialog dialog) {
    return new OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode  = radioGroup.getCheckedRadioButtonId();
            dialog.dismiss();
            backupOperationListener.onBackupRequest(mode);
        }
    };
}

private OnClickListener restoreButtonListenerFor(final Dialog dialog) {
    return new OnClickListener() {
        @Override
        public void onClick(View v) {
            int mode  = radioGroup.getCheckedRadioButtonId();
            dialog.dismiss();
            backupOperationListener.onRestoreRequest(mode);
        }
    };
}

    private void setupRadioGroup(Dialog dialog) {
        this.radioGroup = (RadioGroup) dialog.findViewById(R.id.RestoreModeRadioGroup);
    }

    private void setupTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(R.string.backup_restore);

        View titleDivider = dialog.findViewById(R.id.topDivider);
        titleDivider.setVisibility(View.VISIBLE);
        
    }
    public interface OnBackupOperationRequestListener {

        void onBackupRequest(int mode);

        void onRestoreRequest(int mode);

    }

}
