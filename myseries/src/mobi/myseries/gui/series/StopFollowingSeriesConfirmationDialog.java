/*
 *   StopFollowingSeriesConfirmationDialog.java
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

package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

// TODO: make this class extend AlertDialog
public class StopFollowingSeriesConfirmationDialog {

    public static AlertDialog buildFor(final Series series, Context context) {
        String notFormatedDialgText = context.getString(R.string.do_you_want_to_stop_following);
        final String dialogText = String.format(notFormatedDialgText, series.name());

        final String yesText = context.getString(R.string.yes_i_do);
        final String noText = context.getString(R.string.no_i_dont);

        return new AlertDialog.Builder(context)
                        .setMessage(dialogText)
                        .setPositiveButton(yesText, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.stopFollowing(series);
                            }
                        })
                        .setNegativeButton(noText, null)
                        .create();
    }
}
