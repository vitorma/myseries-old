package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

public class SortingDialogBuilder {
    private Context context;
    private String title;
    private int defaultSortMode;
    private OptionListener oldestFirstOptionListener;
    private OptionListener newestFirstOptionListener;

    public SortingDialogBuilder(Context context) {
        this.context = context;
    }

    public SortingDialogBuilder setTitleArgument(int stringResourceId) {
        String format = this.context.getString(R.string.sort_by_format);
        String elementsName = this.context.getString(stringResourceId);

        this.title = String.format(format, elementsName);

        return this;
    }

    public SortingDialogBuilder setDefaultSortMode(int sortMode) {
        this.defaultSortMode = sortMode;

        return this;
    }

    public SortingDialogBuilder setOldestFirstOptionListener(OptionListener listener) {
        this.oldestFirstOptionListener = listener;

        return this;
    }

    public SortingDialogBuilder setNewestFirstOptionListener(OptionListener listener) {
        this.newestFirstOptionListener = listener;

        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_sorting);

        this.setupTitleFor(dialog);
        this.setUpOldestFirstOptionFor(dialog);
        this.setUpNewestFirstOptionFor(dialog);

        return dialog;
    }

    private void setupTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);

        titleView.setText(this.title);
    }

    private void setUpOldestFirstOptionFor(Dialog dialog) {
        RadioButton oldestFirstOption = (RadioButton) dialog.findViewById(R.id.oldestFirst);

        if (this.defaultSortMode == SortMode.OLDEST_FIRST) {
            oldestFirstOption.setChecked(true);
        }

        oldestFirstOption.setOnClickListener(this.oldestFirstOptionListenerFor(dialog));
    }

    private void setUpNewestFirstOptionFor(Dialog dialog) {
        RadioButton newestFirstOption = (RadioButton) dialog.findViewById(R.id.newestFirst);

        if (this.defaultSortMode == SortMode.NEWEST_FIRST) {
            newestFirstOption.setChecked(true);
        }

        newestFirstOption.setOnClickListener(this.newestFirstOptionListenerFor(dialog));
    }

    private OnClickListener oldestFirstOptionListenerFor(final Dialog dialog) {
        return this.onClickListenerFor(dialog, this.oldestFirstOptionListener);
    }

    private OnClickListener newestFirstOptionListenerFor(final Dialog dialog) {
        return this.onClickListenerFor(dialog, this.newestFirstOptionListener);
    }

    private OnClickListener onClickListenerFor(final Dialog dialog, final OptionListener optionListener) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                optionListener.onClick();
                dialog.dismiss();
            }
        };
    }

    public static interface OptionListener {
        public void onClick();
    }
}
