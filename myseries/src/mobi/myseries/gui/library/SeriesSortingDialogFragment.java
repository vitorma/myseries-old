package mobi.myseries.gui.library;

import mobi.myseries.R;
import mobi.myseries.application.App;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class SeriesSortingDialogFragment extends DialogFragment {
    private int sortMode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String format = this.getActivity().getString(R.string.sort_by_format);
        String args = this.getActivity().getString(R.string.series);
        String title = String.format(format, args);

        this.sortMode = App.preferences().forMySeries().sortMode();

        return new AlertDialog.Builder(this.getActivity())
            .setTitle(title)
            .setSingleChoiceItems(R.array.action_sort_series_array, this.sortMode, this.onItemClickListener())
            .setNegativeButton(R.string.cancel, this.onCancelListener())
            .setPositiveButton(R.string.ok, this.onConfirmListener())
            .create();
    }

    private OnClickListener onItemClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SeriesSortingDialogFragment.this.sortMode = which;
            }
        };
    }

    private OnClickListener onCancelListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }

    private OnClickListener onConfirmListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.preferences().forMySeries().putSortMode(SeriesSortingDialogFragment.this.sortMode);

                dialog.dismiss();
            }
        };
    }
}
