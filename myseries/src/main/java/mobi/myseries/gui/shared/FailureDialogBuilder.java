package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FailureDialogBuilder {
    private Context context;
    private String title;
    private String message;

    public FailureDialogBuilder(Context context) {
        this.context = context;
    }

    public FailureDialogBuilder setTitle(int titleResourceId) {
        this.title = this.context.getString(titleResourceId);
        return this;
    }

    public FailureDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public FailureDialogBuilder setMessage(int messageResourceId) {
        this.message = this.context.getString(messageResourceId);
        return this;
    }

    public FailureDialogBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_failure);

        this.setupTitleFor(dialog);
        this.setupMessageFor(dialog);
        this.setupButtonFor(dialog);

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
        messageView.setText(this.message);
    }

    private void setupButtonFor(final Dialog dialog) {
        Button okButton = (Button) dialog.findViewById(R.id.button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
