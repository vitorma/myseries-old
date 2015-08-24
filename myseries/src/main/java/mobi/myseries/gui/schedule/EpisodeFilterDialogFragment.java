package mobi.myseries.gui.schedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.SchedulePreferences;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.FilterDialogBuilder;
import mobi.myseries.gui.shared.FilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.FilterDialogBuilder.OnToggleOptionListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class EpisodeFilterDialogFragment extends DialogFragment {
    private static final int SPECIAL_EPISODES_ITEM = 0;
    private static final int WATCHED_EPISODES_ITEM = 1;
    private static final String EPISODE_IDS = "EPISODE_IDS";

    private int mScheduleMode;
    private boolean[] mEpisodesToShow;

    public static EpisodeFilterDialogFragment newInstance(int scheduleMode) {
        Bundle args = new Bundle();
        args.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        EpisodeFilterDialogFragment instance = new EpisodeFilterDialogFragment();
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBooleanArray(EPISODE_IDS, mEpisodesToShow);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int episodesToShowArrayResource = episodesToShowArrayResource();

        if (savedInstanceState == null) {
            mEpisodesToShow = episodesToShow();
        } else {
            mEpisodesToShow = savedInstanceState.getBooleanArray(EPISODE_IDS);
        }

        return new FilterDialogBuilder(this.getActivity())
            .setCheckableTitle(R.string.episodesToShow)
            .setDefaultFilterOptions(episodesToShowArrayResource, mEpisodesToShow, onItemClickListener(mEpisodesToShow))
            .setOnFilterListener(onConfirmListener(mEpisodesToShow))
            .build();
    }

    private int episodesToShowArrayResource() {
        return mScheduleMode == ScheduleMode.TO_WATCH ?
                R.array.action_episodes_to_show_array_for_mode_next :
                R.array.action_episodes_to_show_array;
    }

    private boolean[] episodesToShow() {
        SchedulePreferences preferences = App.preferences().forSchedule();

        return mScheduleMode == ScheduleMode.TO_WATCH ?
                new boolean[] { preferences.showSpecialEpisodes() } :
                new boolean[] { preferences.showSpecialEpisodes(), preferences.showWatchedEpisodes() };
    }

    private OnToggleOptionListener onItemClickListener(final boolean[] episodesToShow) {
        return new OnToggleOptionListener() {
            @Override
            public void onToggleOption(DialogInterface dialog, int which, boolean isChecked) {
                episodesToShow[which] = isChecked;
            }

            @Override
            public void onToggleAllOptions(DialogInterface dialog, boolean isChecked) {
                for (int i=0; i<episodesToShow.length; i++) {
                    episodesToShow[i] = isChecked;
                }
            }
        };
    }

    private OnFilterListener onConfirmListener(final boolean[] episodesToShow) {
        return new OnFilterListener() {
            @Override
            public void onFilter() {
                SchedulePreferences prefs = App.preferences().forSchedule();

                prefs.putIfShowSpecialEpisodes(episodesToShow[SPECIAL_EPISODES_ITEM]);

                if (mScheduleMode != ScheduleMode.TO_WATCH) {
                    prefs.putIfShowWatchedEpisodes(episodesToShow[WATCHED_EPISODES_ITEM]);
                }
            }
        };
    }
}
