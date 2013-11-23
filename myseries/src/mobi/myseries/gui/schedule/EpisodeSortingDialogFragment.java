package mobi.myseries.gui.schedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OnSelectOptionListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class EpisodeSortingDialogFragment extends DialogFragment {
    private int mScheduleMode;

    public static EpisodeSortingDialogFragment newInstance(int scheduleMode) {
        Bundle args = new Bundle();
        args.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        EpisodeSortingDialogFragment instance = new EpisodeSortingDialogFragment();
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new SortingDialogBuilder(getActivity())
            .setTitleArgument(R.string.episodes)
            .setSortingOptions(R.array.action_sort_episodes_array, App.preferences().forSchedule().sortMode(), onItemClickListener())
            .build();
    }

    private OnSelectOptionListener onItemClickListener() {
        return new OnSelectOptionListener() {
            @Override
            public void onSelect(int index) {
                App.preferences().forSchedule().putSortMode(index);
            }
        };
    }
}
