package mobi.myseries.gui.schedule;

import java.util.Map;
import java.util.TreeMap;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeriesFilterDialogFragment extends DialogFragment {
    private int scheduleMode;

    public static SeriesFilterDialogFragment newInstance(int scheduleMode) {
        Bundle args = new Bundle();
        args.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        SeriesFilterDialogFragment instance = new SeriesFilterDialogFragment();
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
        final Map<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(SeriesComparator.byAscendingAlphabeticalOrder());

        filterOptions.putAll(App.preferences().forMySchedule(this.scheduleMode).seriesToShow());

        return new SeriesFilterDialogBuilder(this.getActivity())
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    App.preferences().forMySchedule(SeriesFilterDialogFragment.this.scheduleMode).putIfShowSeries(filterOptions);
                }
            })
            .build();
    }
}
