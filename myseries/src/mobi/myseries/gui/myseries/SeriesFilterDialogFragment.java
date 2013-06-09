package mobi.myseries.gui.myseries;

import java.util.Map;
import java.util.TreeMap;

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
        final Map<Series, Boolean> filterOptions = new TreeMap<Series, Boolean>(new SeriesComparator());

        filterOptions.putAll(App.preferences().forMySeries().seriesToShow());

        return new SeriesFilterDialogBuilder(this.getActivity())
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    App.preferences().forMySeries().putShowSeries(filterOptions);
                }
            })
            .build();
    }
}
