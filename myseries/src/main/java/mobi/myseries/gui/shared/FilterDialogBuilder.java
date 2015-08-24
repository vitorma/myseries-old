package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FilterDialogBuilder {
    private Context mContext;
    private TextView mTitleView;
    private CheckedTextView mCheckableTitleView;
    private CheckedTextView[] mOptionViews;

    private String mTitle = "";
    private boolean mIsTitleCheckable = false;
    private String[] mOptionIds;
    private boolean[] mCheckedOptions;
    private int mNumberOfCheckedOptions;
    private OnToggleOptionListener mOnToggleOptionListener;
    private OnFilterListener mOnFilterListener;

    public FilterDialogBuilder(Context context) {
        mContext = context;
    }

    public FilterDialogBuilder setTitle(int titleResourceId) {
        mTitle = mContext.getString(titleResourceId);
        mIsTitleCheckable = false;

        return this;
    }

    public FilterDialogBuilder setCheckableTitle(int titleResourceId) {
        mTitle = mContext.getString(titleResourceId);
        mIsTitleCheckable = true;

        return this;
    }

    public FilterDialogBuilder setDefaultFilterOptions(int optionsResourceId, boolean[] checkedOptions, OnToggleOptionListener onToggleOptionListener) {
        mOptionIds = mContext.getResources().getStringArray(optionsResourceId);
        mCheckedOptions = checkedOptions;
        mOnToggleOptionListener = onToggleOptionListener;

        return this;
    }

    public FilterDialogBuilder setOnFilterListener(OnFilterListener onFilterListener) {
        mOnFilterListener = onFilterListener;

        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(mContext, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_filter);

        setUpTitleFor(dialog);
        setUpOptionViewsFor(dialog);
        setUpCancelButtonFor(dialog);
        setUpOkButtonFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        mTitleView = (TextView) dialog.findViewById(R.id.title);
        mCheckableTitleView = (CheckedTextView) dialog.findViewById(R.id.checkableTitle);

        if (mOptionIds.length <= 1) {
            mIsTitleCheckable = false;
        }

        if (mIsTitleCheckable) {
            mTitleView.setVisibility(View.GONE);
            mCheckableTitleView.setVisibility(View.VISIBLE);

            setUpCheckableTitleFor(dialog);
        } else {
            mTitleView.setVisibility(View.VISIBLE);
            mCheckableTitleView.setVisibility(View.GONE);

            mTitleView.setText(mTitle);
        }
    }

    private void setUpCheckableTitleFor(final Dialog dialog) {
        mCheckableTitleView.setText(mTitle);

        mCheckableTitleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckableTitleView.toggle();

                for (CheckedTextView tv : mOptionViews) {
                    tv.setChecked(mCheckableTitleView.isChecked());
                }

                mNumberOfCheckedOptions = mCheckableTitleView.isChecked() ? mOptionViews.length : 0;

                mOnToggleOptionListener.onToggleAllOptions(dialog, mCheckableTitleView.isChecked());
            }
        });
    }

    private void setUpOptionViewsFor(Dialog dialog) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout optionPanel = (LinearLayout) dialog.findViewById(R.id.optionPanel);

        mOptionViews = new CheckedTextView[mOptionIds.length];

        for (int i=0; i< mOptionIds.length; i++) {
            View v = inflater.inflate(R.layout.dialog_filter_option, null);
            CheckedTextView optionView = (CheckedTextView) v.findViewById(R.id.checkBox);

            optionView.setText(mOptionIds[i]);
            optionView.setChecked(mCheckedOptions[i]);

            if (optionView.isChecked()) { mNumberOfCheckedOptions++; }

            setUpOnOptionViewClickListener(dialog, optionView, i);

            if (i == mOptionIds.length - 1) {
                v.findViewById(R.id.divider).setVisibility(View.INVISIBLE);
            }

            optionPanel.addView(v);

            mOptionViews[i] = optionView;
        }

        if (mIsTitleCheckable) {
            mCheckableTitleView.setChecked(mNumberOfCheckedOptions == mOptionIds.length);
        }
    }

    private void setUpOnOptionViewClickListener(final Dialog dialog, final CheckedTextView optionView, final int optionIndex) {
        optionView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                optionView.toggle();

                if (optionView.isChecked()) {
                    mNumberOfCheckedOptions++;
                } else {
                    mNumberOfCheckedOptions--;
                }

                if (mIsTitleCheckable) {
                    mCheckableTitleView.setChecked(mNumberOfCheckedOptions == mOptionIds.length);
                }

                mOnToggleOptionListener.onToggleOption(dialog, optionIndex, optionView.isChecked());
            }
        });
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
                dialog.dismiss();

                mOnFilterListener.onFilter();
            }
        });
    }

    public static interface OnFilterListener {
        public void onFilter();
    }

    public static interface OnToggleOptionListener {
        public void onToggleOption(DialogInterface dialog, int which, boolean isChecked);
        public void onToggleAllOptions(DialogInterface dialog, boolean isChecked);
    }
}
