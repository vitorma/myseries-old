package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SortingDialogBuilder {
    private String mTitle;
    private String[] mOptions;
    private int mSelectedOptionIndex;
    private OnSelectOptionListener mOnSelectOptionListener;

    private Context mContext;

    public SortingDialogBuilder(Context context) {
        mContext = context;
    }

    public SortingDialogBuilder setTitleArgument(int titleArgumentResourceId) {
        mTitle = mContext.getString(
                R.string.sort_by_format,
                mContext.getString(titleArgumentResourceId));

        return this;
    }

    public SortingDialogBuilder setSortingOptions(int optionsId, int selectedOptionIndex, OnSelectOptionListener onSelectOptionListener) {
        mOptions = mContext.getResources().getStringArray(optionsId);
        mSelectedOptionIndex = selectedOptionIndex;
        mOnSelectOptionListener = onSelectOptionListener;

        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(mContext, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_sorting);

        setupTitleFor(dialog);
        setUpOptionsFor(dialog);

        return dialog;
    }

    private void setupTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);

        titleView.setText(mTitle);
    }

    private void setUpOptionsFor(Dialog dialog) {
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.sortModes);
        LayoutInflater inflater = LayoutInflater.from(mContext);

        for (int i=0; i<mOptions.length; i++) {
            View v = inflater.inflate(R.layout.dialog_sorting_option, null);

            RadioButton radioButton = (RadioButton) v.findViewById(R.id.radioButton);
            radioButton.setId(i);
            radioButton.setText(mOptions[i]);
            radioButton.setChecked(mSelectedOptionIndex == i);
            radioButton.setOnClickListener(onOptionClickListenerFor(dialog, i));

            radioGroup.addView(v);
        }
    }

    private OnClickListener onOptionClickListenerFor(final Dialog dialog, final int index) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mOnSelectOptionListener.onSelect(index);
            }
        };
    }

    public static interface OnSelectOptionListener {
        public void onSelect(int index);
    }
}
