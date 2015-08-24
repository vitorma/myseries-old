package mobi.myseries.gui.settings;

import mobi.myseries.R;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class ThemedListPreference extends ListPreference {
    private int mClickedDialogEntryIndex;
    private CharSequence mDialogTitle;

    public ThemedListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ThemedListPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView() {
        View view = View.inflate(getContext(), R.layout.dialog_list_preference, null);

        mDialogTitle = getDialogTitle();
        if(mDialogTitle == null) mDialogTitle = getTitle();
        ((TextView) view.findViewById(R.id.title)).setText(mDialogTitle);

        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sortModes);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i=0; i<getEntries().length; i++) {
            View v = inflater.inflate(R.layout.dialog_sorting_option, null);

            RadioButton radioButton = (RadioButton) v.findViewById(R.id.radioButton);
            radioButton.setId(i);
            radioButton.setText(getEntries()[i]);
            radioButton.setChecked(findIndexOfValue(getValue()) == i);
            radioButton.setOnClickListener(newOnClickListenerFor(i));

            radioGroup.addView(v);
        }

        return view;
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        if (getEntries() == null || getEntryValues() == null) {
            super.onPrepareDialogBuilder(builder);
            return;
        }

        mClickedDialogEntryIndex = findIndexOfValue(getValue());

        builder.setTitle(null);
        builder.setPositiveButton(null, null);
        builder.setNegativeButton(null, null);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mClickedDialogEntryIndex >= 0 && getEntryValues() != null) {
            String value = getEntryValues()[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) { setValue(value); }
        }
    }

    private OnClickListener newOnClickListenerFor(final int position) {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickedDialogEntryIndex = position;
                ThemedListPreference.this.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
                getDialog().dismiss();
            }
        };
    }
}
