/*
 *   StopFollowingDialogBuilder.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;

public class StopFollowingDialogBuilder extends ConfirmationDialogBuilder {
    private static final FollowSeriesService FOLLOW_SERIES_SERVICE = App.followSeriesService();

    private Series series;

    public StopFollowingDialogBuilder(Series series, Context context) {
        super(context);
        this.series = series;
    }

    private String message() {
        String messageFormat = this.getContext().getString(R.string.stop_follow_message_format);
        Log.d("FORMAT", messageFormat);
        return String.format(messageFormat, series.name());
    }

    private ButtonOnClickListener yesButtonClickListener() {
        return new ButtonOnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                FOLLOW_SERIES_SERVICE.stopFollowing(series);
                dialog.dismiss();
            }
        };
    }

    public Dialog build() {
        this.setMessage(this.message())
            .setPositiveButton(R.string.yes, this.yesButtonClickListener())
            .setNegativeButton(R.string.no, null);

        return super.build();
    }
}
