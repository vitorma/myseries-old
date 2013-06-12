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

public class SeriesFilterDialogBuilder {
    private static final int DEFAULT_TITLE_RESOURCE = R.string.seriesToShow;
    private static final int SELECTED_BUTTON_TEXT_RESOURCE = R.string.showSelected;
    private static final int ALL_BUTTON_TEXT_RESOURCE = R.string.showAll;

    private final Context context;
    private Map<Series, Boolean> options;
    private final Map<Series, CheckedTextView> views;
    private OnFilterListener onFilterListener;
    private int titleResource = SeriesFilterDialogBuilder.DEFAULT_TITLE_RESOURCE;
    private int onlySelectedButtonText = SeriesFilterDialogBuilder.SELECTED_BUTTON_TEXT_RESOURCE;
    private int allButtonText = SeriesFilterDialogBuilder.ALL_BUTTON_TEXT_RESOURCE;

    public SeriesFilterDialogBuilder(Context context) {
        this.context = context;
        this.views = new HashMap<Series, CheckedTextView>();
    }

    public SeriesFilterDialogBuilder setDefaultFilterOptions(Map<Series, Boolean> options) {
        this.options = options;
        return this;
    }

    public SeriesFilterDialogBuilder setOnFilterListener(OnFilterListener listener) {
        this.onFilterListener = listener;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_filter);

        this.setUpTitleFor(dialog);
        this.setUpOptionsFor(dialog);
        this.setUpShowAllButtonFor(dialog);
        this.setUpShowSelectedButtonFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);

        titleView.setText(this.context.getText(this.titleResource));
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

    private void setUpShowAllButtonFor(final Dialog dialog) {
        Button showAllButton = (Button) dialog.findViewById(R.id.showAllButton);

        showAllButton.setText(this.allButtonText);
        showAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Series s : SeriesFilterDialogBuilder.this.options.keySet()) {
                    SeriesFilterDialogBuilder.this.options.put(s, true);
                }
                dialog.dismiss();
                SeriesFilterDialogBuilder.this.onFilterListener.onFilter();
            }
        });
    }

    private void setUpShowSelectedButtonFor(final Dialog dialog) {
        Button showSelectedButton = (Button) dialog.findViewById(R.id.showSelectedButton);
        showSelectedButton.setText(this.onlySelectedButtonText);

        showSelectedButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Series s : SeriesFilterDialogBuilder.this.options.keySet()) {
                    boolean checked = SeriesFilterDialogBuilder.this.views.get(s).isChecked();
                    SeriesFilterDialogBuilder.this.options.put(s, checked);
                }
                dialog.dismiss();
                SeriesFilterDialogBuilder.this.onFilterListener.onFilter();
            }
        });
    }

    public static interface OnFilterListener {
        public void onFilter();
    }

    public SeriesFilterDialogBuilder setTitle(int stringResource) {
        this.titleResource = stringResource;
        return this;
    }

    public SeriesFilterDialogBuilder setAllButtonText(int stringResource) {
        this.allButtonText = stringResource;
        return this;
    }

    public SeriesFilterDialogBuilder setSelectedButtonText(int stringResource) {
        this.onlySelectedButtonText = stringResource;
        return this;
    }
}
