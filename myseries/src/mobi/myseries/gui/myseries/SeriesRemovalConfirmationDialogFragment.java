package mobi.myseries.gui.myseries;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.DialogButtonOnClickListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class SeriesRemovalConfirmationDialogFragment extends DialogFragment {

    public static SeriesRemovalConfirmationDialogFragment newInstance(int[] seriesToRemoveIds) {
        Bundle arguments = new Bundle();
        arguments.putIntArray("seriesToRemoveIds", seriesToRemoveIds);

        SeriesRemovalConfirmationDialogFragment instance = new SeriesRemovalConfirmationDialogFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new ConfirmationDialogBuilder(this.getActivity())
            .setTitle(R.string.are_you_sure)
            .setMessage(R.string.cannot_be_undone)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes, new DialogButtonOnClickListener() {
                @Override
                public void onClick(Dialog dialog) {
                    int[] seriesToRemoveIds = SeriesRemovalConfirmationDialogFragment.this.getArguments().getIntArray("seriesToRemoveIds");
                    Collection<Series> seriesToRemove  = App.seriesProvider().getAllSeries(seriesToRemoveIds);

                    //TODO (Cleber) Overload methods below so they can receive int[]

                    App.seriesFollowingService().unfollowAll(seriesToRemove);
                    App.preferences().removeEntriesRelatedToAllSeries(seriesToRemove);

                    dialog.dismiss();
                }
            }).build();
    }
}
