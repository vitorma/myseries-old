package mobi.myseries.gui.backup;

import java.io.File;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.shared.FileChooserDialogBuilder;
import mobi.myseries.gui.shared.FileChooserDialogBuilder.OnChooseListener;
import mobi.myseries.gui.shared.SortingDialogBuilder.OnSelectOptionListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class FileChooserDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new FileChooserDialogBuilder(getActivity())
            .setDefaultPath(SdcardBackup.getDefaultFolder())
            .setOnChooseListener(onChooseListener())
            .setTitle(R.string.restore_choose_backup_to_restore)
            .build();
    }

    private OnChooseListener onChooseListener() {
        return new OnChooseListener() {
            @Override
            public void onChoose(File chosenFile) {
                if(chosenFile != null)
                    new RestoreProgressDialogFragment().show(getFragmentManager(), "RestoreProgressDialog");
                    App.backupService().restoreBackup(new SdcardBackup(chosenFile));
            }
        };
    }

    public static FileChooserDialogFragment newInstance() {
        return new FileChooserDialogFragment();
    }
}