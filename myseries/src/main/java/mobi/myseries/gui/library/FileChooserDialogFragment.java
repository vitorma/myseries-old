package mobi.myseries.gui.library;

import java.io.File;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.shared.FileChooserDialogBuilder;
import mobi.myseries.gui.shared.FileChooserDialogBuilder.OnChooseListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class FileChooserDialogFragment extends DialogFragment {

    private int selected;
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("selectedPosition", selected);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            selected = 0;
        } else {
           selected = savedInstanceState.getInt("selectedPosition");
        }
        
        return new FileChooserDialogBuilder(getActivity())
            .setDefaultPath(SdcardBackup.getDefaultFolder())
            .setOnChooseListener(onChooseListener())
            .setSelectedOption(selected)
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

            @Override
            public void onSelectPosition(int selectedPosition) {
                selected = selectedPosition;
            }
        };
    }

    public static FileChooserDialogFragment newInstance() {
        return new FileChooserDialogFragment();
    }
}