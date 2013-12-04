package mobi.myseries.gui.statistics;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.StatisticsPreferences;
import mobi.myseries.gui.shared.FilterDialogBuilder;
import mobi.myseries.gui.shared.FilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.FilterDialogBuilder.OnToggleOptionListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class EpisodeFilterDialogFragment extends DialogFragment {
    private static final int SPECIAL_EPISODES_ITEM = 0;
    private static final int UNAIRED_EPISODES_ITEM = 1;
    private static final String EPISODE_IDS = "EPISODE_IDS";

    private boolean[] mEpisodesToCount;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(EPISODE_IDS, mEpisodesToCount);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mEpisodesToCount = newEpisodesToCount();
        } else {
            mEpisodesToCount = savedInstanceState.getBooleanArray(EPISODE_IDS);
        }

        return new FilterDialogBuilder(this.getActivity())
            .setCheckableTitle(R.string.episodesToCount)
            .setDefaultFilterOptions(R.array.action_episodes_to_count_array, mEpisodesToCount, onItemClickListener(mEpisodesToCount))
            .setOnFilterListener(onConfirmListener(mEpisodesToCount))
            .build();
    }

    private boolean[] newEpisodesToCount() {
        StatisticsPreferences preferences = App.preferences().forStatistics();

        return new boolean[] {
            preferences.countSpecialEpisodes(),
            preferences.countUnairedEpisodes()
        };
    }

    private OnToggleOptionListener onItemClickListener(final boolean[] episodesToCount) {
        return new OnToggleOptionListener() {
            @Override
            public void onToggleOption(DialogInterface dialog, int which, boolean isChecked) {
                episodesToCount[which] = isChecked;
            }

            @Override
            public void onToggleAllOptions(DialogInterface dialog, boolean isChecked) {
                for (int i=0; i<episodesToCount.length; i++) {
                    episodesToCount[i] = isChecked;
                }
            }
        };
    }

    private OnFilterListener onConfirmListener(final boolean[] episodesToCount) {
        return new OnFilterListener() {
            @Override
            public void onFilter() {
                StatisticsPreferences preferences = App.preferences().forStatistics();

                preferences.putIfCountSpecialEpisodes(episodesToCount[SPECIAL_EPISODES_ITEM]);
                preferences.putIfCountUnairedEpisodes(episodesToCount[UNAIRED_EPISODES_ITEM]);
            }
        };
    }
}
