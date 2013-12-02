package mobi.myseries.gui.settings;

import mobi.myseries.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

public class NumberPickerDialogPreference extends DialogPreference {
	private static final int DEFAULT_MIN_VALUE = 0;
	private static final int DEFAULT_MAX_VALUE = 100;
	private static final int DEFAULT_VALUE = 0;

	private int mMinValue;
	private int mMaxValue;
	private int mValue;
	private NumberPicker mNumberPicker;
	private CharSequence mDialogTitle;
	private String mDialogMessage;

	public NumberPickerDialogPreference(Context context) {
		this(context, null);
	}

	public NumberPickerDialogPreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.NumberPickerDialogPreference, 0, 0);
		try {
			setMinValue(a.getInteger(
					R.styleable.NumberPickerDialogPreference_min,
					DEFAULT_MIN_VALUE));
			setMaxValue(a.getInteger(
					R.styleable.NumberPickerDialogPreference_max,
					DEFAULT_MAX_VALUE));

		} finally {
			a.recycle();
		}

		mDialogTitle = getTitle();
		mDialogMessage = String.format(getSummary().toString(), DEFAULT_VALUE);

		setDialogLayoutResource(R.layout.preference_number_picker_dialog);

		setPositiveButtonText(android.R.string.ok);
		setNegativeButtonText(android.R.string.cancel);

		setDialogIcon(null);
		setDialogTitle(null);
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		setValue(restore ? getPersistedInt(DEFAULT_VALUE)
				: (Integer) defaultValue);
	}

	@Override
	protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInt(index, DEFAULT_VALUE);
	}

	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);

		TextView title = (TextView) view.findViewById(R.id.title);
		title.setText(mDialogTitle);

		TextView message = (TextView) view.findViewById(R.id.message);
		message.setText(mDialogMessage);

		mNumberPicker = (NumberPicker) view.findViewById(R.id.numberPicker);
		mNumberPicker.setMinValue(mMinValue);
		mNumberPicker.setMaxValue(mMaxValue);
		mNumberPicker.setValue(mValue);
	}

	public int getMinValue() {
		return mMinValue;
	}

	public void setMinValue(int minValue) {
		mMinValue = minValue;
		setValue(Math.max(mValue, mMinValue));
	}

	public int getMaxValue() {
		return mMaxValue;
	}

	public void setMaxValue(int maxValue) {
		mMaxValue = maxValue;
		setValue(Math.min(mValue, mMaxValue));
	}

	public int getValue() {
		return mValue;
	}

	public void setValue(int value) {
		value = Math.max(Math.min(value, mMaxValue), mMinValue);

		if (value != mValue) {
			mValue = value;
			persistInt(value);
		    mDialogMessage = String.format(getSummary().toString(), value);
	        setSummary(mDialogMessage);
			notifyChanged();
		}
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		if (positiveResult) {
			int numberPickerValue = mNumberPicker.getValue();
			if (callChangeListener(numberPickerValue)) {
				setValue(numberPickerValue);
			}
		}
	}
}