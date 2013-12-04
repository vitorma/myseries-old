package mobi.myseries.gui.library;

import mobi.myseries.application.App;
import mobi.myseries.gui.shared.RestoreProgressDialogBuilder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RestoreProgressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new RestoreProgressDialogBuilder(getActivity()).build();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        App.backupService().cancelCurrentRestore();
    }
}