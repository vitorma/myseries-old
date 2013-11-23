package mobi.myseries.gui.addseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.DialogButtonOnClickListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeriesRemovalConfirmationDialogFragment extends DialogFragment {
    private static final String ARGUMENT_SERIES = "Series";

    public static SeriesRemovalConfirmationDialogFragment newInstance(SearchResult series) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_SERIES, series);

        SeriesRemovalConfirmationDialogFragment instance = new SeriesRemovalConfirmationDialogFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Series series = ((SearchResult) this.getArguments().getParcelable(ARGUMENT_SERIES)).toSeries();

        String message = App.resources().getString(R.string.confirmation_removal_single_series, series.name());

        return new ConfirmationDialogBuilder(this.getActivity())
            .setMessage(message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, new DialogButtonOnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    App.seriesFollowingService().unfollow(series);
                    App.preferences().removeEntriesRelatedToSeries(series.id());

                    dialog.dismiss();
                }
            }).build();
    }
}
