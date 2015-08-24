package mobi.myseries.gui.library;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.shared.Extra;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MySeriesContextMenuDialogFragment extends DialogFragment {
    private static final String TAG = "MySeriesContextMenuDialogFragment";

    public static void showDialog(int seriesId, FragmentManager fm) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment previousFragment = fm.findFragmentByTag(TAG);

        if (previousFragment != null) {
            ft.remove(previousFragment);
        }

        ft.addToBackStack(null);

        DialogFragment newFragment = MySeriesContextMenuDialogFragment.newInstance(seriesId);

        newFragment.show(ft, TAG);
    }

    private static DialogFragment newInstance(int seriesId) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);

        DialogFragment newInstance = new MySeriesContextMenuDialogFragment();
        newInstance.setArguments(arguments);

        return newInstance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.myseries_item_context_menu, null);

        TextView hide = (TextView) layout.findViewById(R.id.hide);
        TextView remove = (TextView) layout.findViewById(R.id.remove);

        final int seriesId = this.getArguments().getInt(Extra.SERIES_ID);

        hide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MySeriesContextMenuDialogFragment.this.onHideClick(seriesId);
            }
        });

        remove.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MySeriesContextMenuDialogFragment.this.onRemoveClick(seriesId);
            }
        });

        return layout;
    }

    private void onHideClick(int seriesId) {
        App.preferences().forLibrary().putSeriesToHide(seriesId, true);

        this.dismiss();
    }

    private void onRemoveClick(int seriesId) {
        SeriesRemovalConfirmationDialogFragment.newInstance(new int[] {seriesId})
            .show(this.getFragmentManager(), "removalConfirmationDialog");

        this.dismiss();
    }
}
