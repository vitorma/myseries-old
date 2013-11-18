package mobi.myseries.gui.schedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferences;
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

    private int scheduleMode;

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

        this.scheduleMode = this.getArguments().getInt(Extra.SCHEDULE_MODE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MySchedulePreferences preferences = App.preferences().forMySchedule(this.scheduleMode);

        int episodesToShowArrayResource;
        boolean[] episodesToShow;

        if (this.scheduleMode == ScheduleMode.TO_WATCH) {
            episodesToShowArrayResource = R.array.action_episodes_to_show_array_for_mode_next;
            episodesToShow = new boolean[] {
                preferences.showSpecialEpisodes()
            };
        } else {
            episodesToShowArrayResource = R.array.action_episodes_to_show_array;
            episodesToShow = new boolean[] {
                preferences.showSpecialEpisodes(),
                preferences.showSeenEpisodes()
            };
        }

        return new FilterDialogBuilder(this.getActivity())
        .setCheckableTitle(R.string.episodesToShow)
        .setDefaultFilterOptions(episodesToShowArrayResource, episodesToShow, this.onItemClickListener(episodesToShow))
        .setOnFilterListener(onConfirmListener(episodesToShow))
        .build();
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
                MySchedulePreferences prefs = App.preferences().forMySchedule(scheduleMode);

                prefs.putIfShowSpecialEpisodes(episodesToShow[SPECIAL_EPISODES_ITEM]);

                if (scheduleMode != ScheduleMode.TO_WATCH) {
                    prefs.putIfShowWatchedEpisodes(episodesToShow[WATCHED_EPISODES_ITEM]);
                }
            }
        };
    }
}
