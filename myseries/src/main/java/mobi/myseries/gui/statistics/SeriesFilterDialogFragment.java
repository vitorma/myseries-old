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
    private static final String SERIES_IDS = "SERIES_IDS";

    private Map<Series, Boolean> mFilterOptions;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putIntArray(SERIES_IDS, seriesToHideIdsFrom(mFilterOptions));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mFilterOptions = newFilterOptions();
        } else {
            mFilterOptions = filterOptionsFrom(savedInstanceState.getIntArray(SERIES_IDS));
        }

        return new SeriesFilterDialogBuilder(getActivity())
            .setTitle(R.string.seriesToCount)
            .setDefaultFilterOptions(mFilterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    App.preferences().forStatistics().putSeriesToDontCount(seriesToHideIdsFrom(mFilterOptions));
                }
            })
            .build();
    }

    private TreeMap<Series, Boolean> newFilterOptions() {
        return filterOptionsFrom(App.preferences().forStatistics().seriesToDontCount());
    }

    private TreeMap<Series, Boolean> filterOptionsFrom(int[] seriesToHideIds) {
        TreeMap<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(SeriesComparator.byAscendingAlphabeticalOrder());

        Collection<Series> seriesToHide = seriesToHideFrom(seriesToHideIds);
        Collection<Series> seriesToShow = seriesToShowFrom(seriesToHide);

        for(Series s : seriesToHide) {
            filterOptions.put(s, false);
        }

        for(Series s : seriesToShow) {
            filterOptions.put(s, true);
        }

        return filterOptions;
    }

    private Collection<Series> seriesToHideFrom(int[] seriesIds) {
        return App.seriesFollowingService().getAllFollowedSeries(seriesIds);
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