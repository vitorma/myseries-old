package mobi.myseries.gui.myseries;

import java.util.ArrayList;
import java.util.List;
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
import android.content.Context;
import android.os.Bundle;

public class SeriesRemovalDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = this.getActivity();
        final SortedMap<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(SeriesComparator.byAscendingAlphabeticalOrder());

        for (Series s : App.seriesFollowingService().getAllFollowedSeries()) {
            filterOptions.put(s, false);
        }

        return new SeriesFilterDialogBuilder(context)
            .setTitle(R.string.series_to_remove)
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    final List<Series> allSeriesToRemove = new ArrayList<Series>();

                    for (Series s : filterOptions.keySet()) {
                        if (filterOptions.get(s)) {
                            allSeriesToRemove.add(s);
                        }
                    }

                    if (allSeriesToRemove.isEmpty()) {
                        new ToastBuilder(context).setMessage(R.string.no_series_selected_to_remove)
                            .build().show();
                        return;
                    }

                    int[] seriesToRemoveIds = new int[allSeriesToRemove.size()];
                    for (int i = 0; i < seriesToRemoveIds.length; i++) {
                        seriesToRemoveIds[i] = allSeriesToRemove.get(i).id();
                    }

                    SeriesRemovalConfirmationDialogFragment.newInstance(seriesToRemoveIds).show(
                            SeriesRemovalDialogFragment.this.getFragmentManager(), "removalConfirmationDialog");
                }
            }).build();
    }
}
