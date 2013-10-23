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
import mobi.myseries.domain.model.Season;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Validate;

public class LocalText {

    public static String of(Status status, String defaultText) {
        Validate.isNonNull(status, "status");
        Validate.isNonNull(defaultText, "defaultText");

        switch (status) {
            case CONTINUING:
                return get(R.string.status_continuing);
            case ENDED:
                return get(R.string.status_ended);
            case UNKNOWN:
            default:
                return defaultText;
        }
    }

    public static String of(RelativeDay day, String defaultText) {
        Validate.isNonNull(defaultText, "defaultText");

        if (day == null) {
            return defaultText;
        }

        if (day.isToday()) {
            return get(R.string.relative_time_today);
        }

        if (day.isYesterday()) {
            return get(R.string.relative_time_yesterday);
        }

        if (day.isTomorrow()) {
            return get(R.string.relative_time_tomorrow);
        }

        if (day.wasLessThanAWeekAgo()) {
            return String.format(get(R.string.relative_time_past), day.daysUntilToday());
        }

        if (day.isInLessThanAWeek()) {
            return String.format(get(R.string.relative_time_future), day.daysSinceToday());
        }

        return defaultText;
    }

    public static String of(Season season) {
        Validate.isNonNull(season, "season");

        if (season.isSpecial()) {
             return get(R.string.special_episodes);
        } else {
            return get(R.string.season_number_format_ext, season.number());
        }
    }

    public static String get(int stringResourceId) {
        return App.resources().getString(stringResourceId);
    }

    public static String get(int formatResourceId, Object... formatArgs) {
        return App.resources().getString(formatResourceId, formatArgs);
    }

    public static String getPlural(int id, int quantity) {
        return App.resources().getQuantityString(id, quantity);
    }

    public static String getPlural(int id, int quantity, Object... formatArgs) {
        return App.resources().getQuantityString(id, quantity, formatArgs);
    }
}
