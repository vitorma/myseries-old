package mobi.myseries.gui.shared;

import java.util.HashMap;
import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.domain.model.Series;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RemovingSeriesDialogBuilder {
    private Context context;
    private Map<Series, Boolean> options;
    private Map<Series, CheckedTextView> views;
    private OnRequestRemovalListener onRequestRemovalListener;

    public RemovingSeriesDialogBuilder(Context context) {
        this.context = context;
        this.views = new HashMap<Series, CheckedTextView>();
    }

    public RemovingSeriesDialogBuilder setDefaultRemovalOptions(Map<Series, Boolean> options) {
        this.options = options;
        return this;
    }

    public RemovingSeriesDialogBuilder setOnRequestRemovalListener(OnRequestRemovalListener listener) {
        this.onRequestRemovalListener = listener;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_filter);

        this.setUpTitleFor(dialog);
        this.setUpOptionsFor(dialog);
        this.setUpRemoveAllButtonFor(dialog);
        this.setUpRemoveSelectedButtonFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);

        titleView.setText(this.context.getText(R.string.series_to_remove));
    }

    private void setUpOptionsFor(Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        LinearLayout optionPanel = (LinearLayout) dialog.findViewById(R.id.optionPanel);

        for (final Series s : this.options.keySet()) {
            View v = inflater.inflate(R.layout.dialog_filter_option, null);
            final CheckedTextView seriesCheck = (CheckedTextView) v.findViewById(R.id.seriesCheck);

            seriesCheck.setText(s.name());
            seriesCheck.setChecked(this.options.get(s));
            seriesCheck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    seriesCheck.toggle();
                }
            });

            optionPanel.addView(v);

            this.views.put(s, seriesCheck);
        }
    }

    private void setUpRemoveAllButtonFor(final Dialog dialog) {
        Button removeAllButton = (Button) dialog.findViewById(R.id.showAllButton);

        removeAllButton.setText(R.string.remove_all);
        removeAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Series s : options.keySet()) {
                    options.put(s, true);
                }
                dialog.dismiss();
                onRequestRemovalListener.onRequestRemoval();
            }
        });
    }

    private void setUpRemoveSelectedButtonFor(final Dialog dialog) {
        Button removeSelectedButton = (Button) dialog.findViewById(R.id.showSelectedButton);
        removeSelectedButton.setText(R.string.remove_selected);

        removeSelectedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Series s : options.keySet()) {
                    boolean checked = views.get(s).isChecked();
                    options.put(s, checked);
                }
                dialog.dismiss();
                onRequestRemovalListener.onRequestRemoval();
            }
        });
    }

    public static interface OnRequestRemovalListener {
        public void onRequestRemoval();
    }
}
