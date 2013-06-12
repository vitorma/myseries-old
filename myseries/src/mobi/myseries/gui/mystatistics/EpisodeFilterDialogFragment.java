package mobi.myseries.gui.mystatistics;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MyStatisticsPreferences;
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
        final MyStatisticsPreferences preferences = App.preferences().forMyStatistics();

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
                App.preferences().forMyStatistics().putIfCountSpecialEpisodes(episodesToCount[EpisodeFilterDialogFragment.SPECIAL_EPISODES_ITEM]);
                App.preferences().forMyStatistics().putIfCountUnairedEpisodes(episodesToCount[EpisodeFilterDialogFragment.UNAIRED_EPISODES_ITEM]);

                dialog.dismiss();
            }
        };
    }
}
