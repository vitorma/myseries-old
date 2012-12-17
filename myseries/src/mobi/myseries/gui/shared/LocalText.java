/*
 *   LocalText.java
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

package mobi.myseries.gui.shared;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Validate;

public class LocalText {

    public static String of(Status status, String defaultTextForUnknownStatus) {
        Validate.isNonNull(status, "status");
        Validate.isNonNull(defaultTextForUnknownStatus, "defaultTextForUnknownStatus");

        switch (status) {
            case CONTINUING:
                return stringFromId(R.string.status_continuing);
            case ENDED:
                return stringFromId(R.string.status_ended);
            case UNKNOWN:
            default:
                return defaultTextForUnknownStatus;
        }
    }

    private static String stringFromId(int stringResourceId) {
        return App.resources().getString(stringResourceId);
    }
}
