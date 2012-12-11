package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BackupDialogBuilder {
    private Context context;
    private ButtonOnClickListener backupButtonListener;
    private ButtonOnClickListener restoreButtonListener;
    private String backupFolderPath;

    
    public BackupDialogBuilder(Context context) {
        this.context = context;
    }
    
    public Context context() {
        return this.context;
    }

    public BackupDialogBuilder setBackupButtonListener(ButtonOnClickListener buttonOnClickListener) {
        this.backupButtonListener = buttonOnClickListener;
        return this;
    }
    
    public BackupDialogBuilder setRestoreButtonListener(ButtonOnClickListener listener) {
        this.restoreButtonListener = listener;
        return this;
    }

    public void setBackupFolder(String backupFolder) {
        this.backupFolderPath = backupFolder;
        
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_backup);

        this.setupTitleFor(dialog);
        this.setupMessage(dialog);
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
            backupButtonListener.onClick(dialog);
            dialog.dismiss();
        }
    };
}

private OnClickListener restoreButtonListenerFor(final Dialog dialog) {
    return new OnClickListener() {
        @Override
        public void onClick(View v) {
            restoreButtonListener.onClick(dialog);
            dialog.dismiss();
        }
    };
}

    private void setupMessage(Dialog dialog) {
        TextView descriptionView = (TextView) dialog.findViewById(R.id.message);
        StringBuilder builder = new StringBuilder();
        builder.append(context.getString(R.string.backup_description));
        builder.append("\n");
        builder.append("\n");
        builder.append(String.format(context.getString(R.string.backup_folder_file_path), this.backupFolderPath));
        descriptionView.setText(builder.toString());
    }

    private void setupTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(R.string.backup_restore);

        View titleDivider = dialog.findViewById(R.id.titleDivider);
        titleDivider.setVisibility(View.VISIBLE);
        
    }

}
