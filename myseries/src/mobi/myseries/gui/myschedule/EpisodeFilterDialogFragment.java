package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferences;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.shared.Extra;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
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

        if (this.scheduleMode == ScheduleMode.NEXT) {
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

        return new AlertDialog.Builder(this.getActivity())
            .setTitle(R.string.episodesToShow)
            .setMultiChoiceItems(episodesToShowArrayResource, episodesToShow, this.onItemClickListener(episodesToShow))
            .setNegativeButton(R.string.cancel, this.onCancelListener())
            .setPositiveButton(R.string.ok, this.onConfirmListener(episodesToShow))
            .create();
    }

    private OnMultiChoiceClickListener onItemClickListener(final boolean[] episodesToShow) {
        return new OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                episodesToShow[which] = isChecked;
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

    private OnClickListener onConfirmListener(final boolean[] episodesToShow) {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MySchedulePreferences prefs = App.preferences().forMySchedule(EpisodeFilterDialogFragment.this.scheduleMode);

                prefs.putIfShowSpecialEpisodes(episodesToShow[SPECIAL_EPISODES_ITEM]);

                if (EpisodeFilterDialogFragment.this.scheduleMode != ScheduleMode.NEXT) {
                    prefs.putIfShowSeenEpisodes(episodesToShow[WATCHED_EPISODES_ITEM]);
                }

                dialog.dismiss();
            }
        };
    }
}
