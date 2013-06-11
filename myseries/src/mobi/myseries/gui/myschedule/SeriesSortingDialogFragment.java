package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.shared.Extra;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class SeriesSortingDialogFragment extends DialogFragment {
    private int scheduleMode;
    private int sortMode;

    public static SeriesSortingDialogFragment newInstance(int scheduleMode) {
        Bundle args = new Bundle();
        args.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        SeriesSortingDialogFragment instance = new SeriesSortingDialogFragment();
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
        String format = this.getActivity().getString(R.string.sort_by_format);
        String args = this.getActivity().getString(R.string.episodes);
        String title = String.format(format, args);

        this.sortMode = App.preferences().forMySchedule(this.scheduleMode).sortMode();

        return new AlertDialog.Builder(this.getActivity())
            .setTitle(title)
            .setSingleChoiceItems(R.array.action_sort_episodes_array, this.sortMode, this.onItemClickListener())
            .setNegativeButton(R.string.cancel, this.onCancelListener())
            .setPositiveButton(R.string.ok, this.onConfirmListener())
            .create();
    }

    private OnClickListener onItemClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SeriesSortingDialogFragment.this.sortMode = which;
            }
        };
    }

    private OnClickListener onCancelListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }

    private OnClickListener onConfirmListener() {
        return new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                App.preferences()
                    .forMySchedule(SeriesSortingDialogFragment.this.scheduleMode)
                    .putSortMode(SeriesSortingDialogFragment.this.sortMode);

                dialog.dismiss();
            }
        };
    }
}
