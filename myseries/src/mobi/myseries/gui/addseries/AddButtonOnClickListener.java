package mobi.myseries.gui.addseries;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DialogButtonOnClickListener;
import android.app.Dialog;

public class AddButtonOnClickListener implements DialogButtonOnClickListener {
    private Series seriesToAdd;

    public AddButtonOnClickListener(Series seriesToAdd) {
        this.seriesToAdd = seriesToAdd;
    }

    @Override
    public void onClick(Dialog dialog) {
        App.followSeriesService().follow(this.seriesToAdd);

        dialog.dismiss();
    }
}