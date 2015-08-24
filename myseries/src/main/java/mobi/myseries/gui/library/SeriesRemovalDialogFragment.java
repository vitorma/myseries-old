package mobi.myseries.gui.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeriesRemovalDialogFragment extends DialogFragment {
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
            .setTitle(R.string.series_to_remove)
            .setDefaultFilterOptions(mFilterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    final List<Series> allSeriesToRemove = new ArrayList<Series>();

                    for (Series s : mFilterOptions.keySet()) {
                        if (mFilterOptions.get(s)) {
                            allSeriesToRemove.add(s);
                        }
                    }

                    if (allSeriesToRemove.isEmpty()) {
                        new ToastBuilder(getActivity())
                            .setMessage(R.string.no_series_selected_to_remove)
                            .build()
                            .show();
                        return;
                    }

                    int[] seriesToRemoveIds = new int[allSeriesToRemove.size()];
                    for (int i = 0; i < seriesToRemoveIds.length; i++) {
                        seriesToRemoveIds[i] = allSeriesToRemove.get(i).id();
                    }

                    SeriesRemovalConfirmationDialogFragment
                        .newInstance(seriesToRemoveIds)
                        .show(getFragmentManager(), "removalConfirmationDialog");
                }
            }).build();
    }

    private Map<Series, Boolean> newFilterOptions() {
        final SortedMap<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(SeriesComparator.byAscendingAlphabeticalOrder());

        for (Series s : App.seriesFollowingService().getAllFollowedSeries()) {
            filterOptions.put(s, false);
        }

        return filterOptions;
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
