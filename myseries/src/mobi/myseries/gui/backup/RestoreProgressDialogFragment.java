package mobi.myseries.gui.backup;

import mobi.myseries.application.App;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RestoreProgressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new RestoreProgressDialogBuilder(getActivity())
            .build();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        App.backupService().cancelCurrentRestore();
    }
}