package mobi.myseries.gui.season;

import mobi.myseries.R;
import mobi.myseries.application.App;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class EpisodeSortingDialogFragment extends DialogFragment {
    private int sortMode;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String format = this.getActivity().getString(R.string.sort_by_format);
        String args = this.getActivity().getString(R.string.episodes);
        String title = String.format(format, args);

        this.sortMode = App.preferences().forSeason().sortMode();

        return new AlertDialog.Builder(this.getActivity())
            .setTitle(title)
            .setSingleChoiceItems(R.array.action_sort_episodes_array, this.sortMode, this.onItemClickListener())
            .setNegativeButton(R.string.cancel, this.onCancelListener())
            .setPositiveButton(R.string.ok, this.onConfirmListener())
            .create();
    }

    private OnClickListener onItemClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EpisodeSortingDialogFragment.this.sortMode = which;
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
                App.preferences()
                    .forSeason()
                    .putSortMode(EpisodeSortingDialogFragment.this.sortMode);

                dialog.dismiss();
            }
        };
    }
}
