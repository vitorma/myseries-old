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

public class SeriesFilterDialogBuilder {
    private static final int DEFAULT_TITLE_RESOURCE = R.string.seriesToShow;

    private final Context context;
    private int titleResource = DEFAULT_TITLE_RESOURCE;
    private Map<Series, Boolean> options;

    private CheckedTextView titleView;
    private final Map<Series, CheckedTextView> optionViews;
    private int numberOfCheckedViews;

    private OnFilterListener onFilterListener;

    public SeriesFilterDialogBuilder(Context context) {
        this.context = context;
        this.optionViews = new HashMap<Series, CheckedTextView>();
    }

    public SeriesFilterDialogBuilder setDefaultFilterOptions(Map<Series, Boolean> options) {
        this.options = options;
        return this;
    }

    public SeriesFilterDialogBuilder setTitle(int stringResource) {
        this.titleResource = stringResource;
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
        this.setUpOptionViewsFor(dialog);
        this.setUpCancelButtonFor(dialog);
        this.setUpOkButtonFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        this.titleView = (CheckedTextView) dialog.findViewById(R.id.title);

        this.titleView.setText(this.context.getText(this.titleResource));

        this.titleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesFilterDialogBuilder.this.titleView.toggle();

                for (CheckedTextView tv : SeriesFilterDialogBuilder.this.optionViews.values()) {
                    tv.setChecked(SeriesFilterDialogBuilder.this.titleView.isChecked());
                }

                if (SeriesFilterDialogBuilder.this.titleView.isChecked()) {
                    SeriesFilterDialogBuilder.this.numberOfCheckedViews = SeriesFilterDialogBuilder.this.optionViews.size();
                } else {
                    SeriesFilterDialogBuilder.this.numberOfCheckedViews = 0;
                }
            }
        });
    }

    private void setUpOptionViewsFor(Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        LinearLayout optionPanel = (LinearLayout) dialog.findViewById(R.id.optionPanel);
        int counter = 0;

        for (Series s : this.options.keySet()) {
            View v = inflater.inflate(R.layout.dialog_filter_option, null);
            final CheckedTextView seriesCheck = (CheckedTextView) v.findViewById(R.id.seriesCheck);

            seriesCheck.setText(s.name());
            seriesCheck.setChecked(this.options.get(s));

            if (seriesCheck.isChecked()) { this.numberOfCheckedViews++; }

            seriesCheck.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    seriesCheck.toggle();

                    if (seriesCheck.isChecked()) {
                        SeriesFilterDialogBuilder.this.numberOfCheckedViews++;
                    } else {
                        SeriesFilterDialogBuilder.this.numberOfCheckedViews--;
                    }

                    SeriesFilterDialogBuilder.this.titleView.setChecked(
                            SeriesFilterDialogBuilder.this.numberOfCheckedViews == SeriesFilterDialogBuilder.this.optionViews.size());
                }
            });

            counter++;
            if (counter == this.options.size()) {
                View divider = v.findViewById(R.id.divider);
                divider.setVisibility(View.INVISIBLE);
            }

            optionPanel.addView(v);

            this.optionViews.put(s, seriesCheck);
        }

        this.titleView.setChecked(this.numberOfCheckedViews == this.optionViews.size());
    }

    private void setUpCancelButtonFor(final Dialog dialog) {
        Button cancelButton = (Button) dialog.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void setUpOkButtonFor(final Dialog dialog) {
        Button okButton = (Button) dialog.findViewById(R.id.okButton);

        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Series s : SeriesFilterDialogBuilder.this.options.keySet()) {
                    boolean checked = SeriesFilterDialogBuilder.this.optionViews.get(s).isChecked();

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
}
