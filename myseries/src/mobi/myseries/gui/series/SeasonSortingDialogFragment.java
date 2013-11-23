package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OnSelectOptionListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeasonSortingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new SortingDialogBuilder(getActivity())
            .setTitleArgument(R.string.seasons)
            .setSortingOptions(R.array.action_sort_seasons_array, App.preferences().forSeries().sortMode(), onItemClickListener())
            .build();
    }

    private OnSelectOptionListener onItemClickListener() {
        return new OnSelectOptionListener() {
            @Override
            public void onSelect(int index) {
                App.preferences().forSeries().putSortMode(index);
            }
        };
    }
}
