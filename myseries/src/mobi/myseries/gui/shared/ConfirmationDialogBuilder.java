package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.shared.Strings;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmationDialogBuilder {
    private Context context;
    private String title;
    private String message;
    private String surrogateMessage;
    private String positiveButtonText;
    private String negativeButtonText;
    private ButtonOnClickListener positiveButtonListener;
    private ButtonOnClickListener negativeButtonListener;

    public ConfirmationDialogBuilder(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    public ConfirmationDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public ConfirmationDialogBuilder setTitle(int titleResourceId) {
        this.title = this.context.getString(titleResourceId);
        return this;
    }

    public ConfirmationDialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public ConfirmationDialogBuilder setMessage(int messageResourceId) {
        this.message = this.context.getString(messageResourceId);
        return this;
    }

    public ConfirmationDialogBuilder setSurrogateMessage(String surrogateMessage) {
        this.surrogateMessage = surrogateMessage;
        return this;
    }

    public ConfirmationDialogBuilder setSurrogateMessage(int surrogateMessageResourceId) {
        this.surrogateMessage = this.context.getString(surrogateMessageResourceId);
        return this;
    }

    public ConfirmationDialogBuilder setPositiveButton(String text, ButtonOnClickListener listener) {
        this.positiveButtonText = text;
        this.positiveButtonListener = listener;
        return this;
    }

    public ConfirmationDialogBuilder setPositiveButton(int textResourceId, ButtonOnClickListener listener) {
        this.positiveButtonText = this.context.getString(textResourceId);
        this.positiveButtonListener = listener;
        return this;
    }

    public ConfirmationDialogBuilder setNegativeButton(String text, ButtonOnClickListener listener) {
        this.negativeButtonText = text;
        this.negativeButtonListener = listener;
        return this;
    }

    public ConfirmationDialogBuilder setNegativeButton(int textResourceId, ButtonOnClickListener listener) {
        this.negativeButtonText = this.context.getString(textResourceId);
        this.negativeButtonListener = listener;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_confirmation);

        this.setupTitleFor(dialog);
        this.setupMessageFor(dialog);
        this.setupPositiveButtonFor(dialog);
        this.setupNegativeButtonFor(dialog);

        return dialog;
    }

    private void setupTitleFor(Dialog dialog) {
        if (this.title == null) {return;}

        TextView titleView = (TextView) dialog.findViewById(R.id.title);
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(this.title);

        View titleDivider = dialog.findViewById(R.id.titleDivider);
        titleDivider.setVisibility(View.VISIBLE);
    }

    private void setupMessageFor(Dialog dialog) {
        TextView messageView = (TextView) dialog.findViewById(R.id.message);

        if (this.message == null || Strings.isBlank(this.message)) {
            messageView.setText(this.surrogateMessage);
            return;
        }

        messageView.setText(this.message);
    }

    private void setupPositiveButtonFor(Dialog dialog) {
        Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);

        positiveButton.setText(this.positiveButtonText);

        positiveButton.setOnClickListener(
            this.positiveButtonListener != null ?
            this.positiveButtonListenerFor(dialog) :
            this.defaultButtonListenerFor(dialog));
    }

    private void setupNegativeButtonFor(Dialog dialog) {
        Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);

        negativeButton.setText(this.negativeButtonText);

        negativeButton.setOnClickListener(
            this.negativeButtonListener != null ?
            this.negativeButtonListenerFor(dialog) :
            this.defaultButtonListenerFor(dialog));
    }

    private OnClickListener positiveButtonListenerFor(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveButtonListener.onClick(dialog);
            }
        };
    }

    private OnClickListener negativeButtonListenerFor(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeButtonListener.onClick(dialog);
            }
        };
    }

    private OnClickListener defaultButtonListenerFor(final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        };
    }

    public static interface ButtonOnClickListener {
        public void onClick(Dialog dialog);
    }
}
