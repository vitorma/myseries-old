package mobi.myseries.gui.statistics;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeriesFilterDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Map<Series, Boolean> filterOptions = newFilterOptions();

        return new SeriesFilterDialogBuilder(getActivity())
            .setTitle(R.string.seriesToCount)
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    App.preferences().forStatistics().putSeriesToDontCount(seriesToHideIdsFrom(filterOptions));
                }
            })
            .build();
    }

    private TreeMap<Series, Boolean> newFilterOptions() {
        TreeMap<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(SeriesComparator.byAscendingAlphabeticalOrder());
        Collection<Series> seriesToHide = seriesToHideFromPreferences();
        Collection<Series> seriesToShow = seriesToShowFrom(seriesToHide);

        for(Series s : seriesToHide) {
            filterOptions.put(s, false);
        }

        for(Series s : seriesToShow) {
            filterOptions.put(s, true);
        }

        return filterOptions;
    }

    private Collection<Series> seriesToHideFromPreferences() {
        return App.seriesFollowingService().getAllFollowedSeries(
                App.preferences().forStatistics().seriesToDontCount());
    }

    private Collection<Series> seriesToShowFrom(Collection<Series> seriesToHide) {
        Collection<Series> seriesToShow = App.seriesFollowingService().getAllFollowedSeries();

        seriesToShow.removeAll(seriesToHide);

        return seriesToShow;
    }

    private int[] seriesToHideIdsFrom(Map<Series, Boolean> filterOptions) {
        int[] seriesToHideIds = new int[filterOptions.size()];

        int length = 0;

        for (Entry<Series, Boolean> entry : filterOptions.entrySet()) {
            if (!entry.getValue()) {
                seriesToHideIds[length] = entry.getKey().id();
                length++;
            }
        }

        return Arrays.copyOf(seriesToHideIds, length);
    }
}
