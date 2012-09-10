package mobi.myseries.gui.shared;

import mobi.myseries.R;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class SeriesFilterDialogBuilder {
    private Context context;

    public SeriesFilterDialogBuilder(Context context) {
        this.context = context;
    }

    //TODO Implement me
    public Dialog build() {
        Dialog dialog = new Dialog(this.context, R.style.MySeriesTheme_Dialog);

        dialog.setContentView(R.layout.dialog_filter);

        this.setUpTitleFor(dialog);

        return dialog;
    }

    private void setUpTitleFor(Dialog dialog) {
        TextView titleView = (TextView) dialog.findViewById(R.id.title);

        titleView.setText(this.context.getText(R.string.filterSeries));
    }
}
