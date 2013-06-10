package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySeriesPreferences;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;

public class EpisodeFilterDialogFragment extends DialogFragment {
    private static final int SPECIAL_EPISODES_ITEM = 0;
    private static final int UNAIRED_EPISODES_ITEM = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MySeriesPreferences preferences = App.preferences().forMySeries();

        boolean[] episodesToCount = new boolean[]{
            preferences.countSpecialEpisodes(),
            preferences.countUnairedEpisodes()
        };

        return new AlertDialog.Builder(this.getActivity())
            .setTitle(R.string.episodesToCount)
            .setMultiChoiceItems(R.array.action_episodes_to_count_array, episodesToCount, this.onItemClickListener(episodesToCount))
            .setNegativeButton(R.string.cancel, this.onCancelListener())
            .setPositiveButton(R.string.ok, this.onConfirmListener(episodesToCount))
            .create();
    }

    private OnMultiChoiceClickListener onItemClickListener(final boolean[] episodesToCount) {
        return new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                episodesToCount[which] = isChecked;
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

    private OnClickListener onConfirmListener(final boolean[] episodesToCount) {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.preferences().forMySeries().putCountSpecialEpisodes(episodesToCount[SPECIAL_EPISODES_ITEM]);
                App.preferences().forMySeries().putCountUnairedEpisodes(episodesToCount[UNAIRED_EPISODES_ITEM]);

                dialog.dismiss();
            }
        };
    }
}
