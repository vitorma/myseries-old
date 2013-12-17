package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.features.Feature;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
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

        dialog.setContentView(R.layout.dialog_backup_restore);

        this.setupTitleFor(dialog);
        this.setupRadioGroup(dialog);
        this.setupCancelButtonFor(dialog);
        this.setupBackupButtonFor(dialog);
        this.setupRestoreButtonFor(dialog);

        return dialog;
    }

    private void setupCancelButtonFor(final Dialog dialog) {
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);
        cancelButton.setText(R.string.cancel);

        cancelButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        
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
        int count = this.radioGroup.getChildCount();
        for (int i=0;i<count;i++) {
            View button = this.radioGroup.getChildAt(i);
            if (button instanceof RadioButton) {
                int id = ((RadioButton) button).getId();
                if(id == R.id.GoogleDriveRadioButton || id == R.id.DropboxRadioButton)
                    if(!App.features().isEnabled(Feature.CLOUD_BACKUP)) {
                        button.setEnabled(false);
                        String label = (String) ((RadioButton) button).getText() 
                                                + " - " 
                                                + System.getProperty("line.separator") 
                                                + context.getString(R.string.purchase_feature, context.getString(R.string.cloud_backup));
                        ((RadioButton) button).setText(label);
                    }
                    
            }
        }
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
