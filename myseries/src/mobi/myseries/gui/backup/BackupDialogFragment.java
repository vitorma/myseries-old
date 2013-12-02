package mobi.myseries.gui.backup;

import com.google.android.gms.common.AccountPicker;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.shared.BackupDialogBuilder;
import mobi.myseries.gui.shared.BackupDialogBuilder.OnBackupOperationRequestListener;
import mobi.myseries.gui.shared.FailureDialogBuilder;

public class BackupDialogFragment extends DialogFragment {
    private static final int DRIVE_BACKUP = 1;
    private static final int DRIVE_RESTORE = 2;
    private static final int DROPBOX_BACKUP = 3;
    private static final int DROPBOX_RESTORE = 4;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BackupDialogBuilder(getActivity())
            .setOnBackupOperationRequestListener(onBackupOperationRequestListener())
            .build();
    }

    private OnBackupOperationRequestListener onBackupOperationRequestListener() {
        return new OnBackupOperationRequestListener() {

            @Override
            public void onRestoreRequest(int mode) {
                switch (mode) {
                case R.id.SDCardRadioButton:
                    if(SdcardBackup.hasBackupFiles()) {
                        new FileChooserDialogFragment().show(BackupDialogFragment.this.getFragmentManager(), "FileChooseDialog");
                    } else {
                        dismiss();
                        new FailureDialogBuilder(getActivity())
                        .setTitle(R.string.restore_failed_title)
                        .setMessage(R.string.restore_there_are_no_backups_to_restore)
                        .build()
                        .show();
                    }
                    break;
                case R.id.GoogleDriveRadioButton:
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                            true, null, null, null, null);
                    getActivity().startActivityForResult(intent, DRIVE_RESTORE);
                    break;
                case R.id.DropboxRadioButton:
                    new RestoreProgressDialogFragment().show(getFragmentManager(), "RestoreProgressDialog");
                    App.backupService().restoreBackup(new DropboxBackup());

                default:
                    break;
                }
            }

            @Override
            public void onBackupRequest(int mode) {
                switch (mode) {
                case R.id.SDCardRadioButton:
                    App.backupService().doBackup(new SdcardBackup());
                    break;

                case R.id.GoogleDriveRadioButton:
                    Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"},
                            true, null, null, null, null);
                    getActivity().startActivityForResult(intent, DRIVE_BACKUP);
                    break;

                case R.id.DropboxRadioButton:
                    App.backupService().doBackup(new DropboxBackup());

                default:
                    break;
                }

            }
        };
    }
}