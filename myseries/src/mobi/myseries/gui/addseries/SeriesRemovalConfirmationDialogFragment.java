package mobi.myseries.gui.addseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.model.Series;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SeriesRemovalConfirmationDialogFragment extends DialogFragment {
    private static final String ARGUMENT_SERIES = "Series";

    public static SeriesRemovalConfirmationDialogFragment newInstance(ParcelableSeries series) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGUMENT_SERIES, series);

        SeriesRemovalConfirmationDialogFragment instance = new SeriesRemovalConfirmationDialogFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Series series = ((ParcelableSeries) this.getArguments().getParcelable(ARGUMENT_SERIES)).toSeries();

        String message = App.resources().getString(R.string.confirmation_removal_single_series, series.name());

        return new AlertDialog.Builder(this.getActivity())
            .setMessage(message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    App.seriesFollowingService().unfollow(series);
                    App.preferences().removeEntriesRelatedToSeries(series);

                    dialog.dismiss();
                }
            }).create();
    }
}
