/*
 *   Preferences.java
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

package mobi.myseries.gui.preferences;

import java.util.Collection;

import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.preferences.SchedulePreferences.AppWidgetPreferences;
import mobi.myseries.gui.preferences.SchedulePreferences.MySchedulePreferences;

public class Preferences {

    public static MySchedulePreferences forMySchedule(int scheduleMode) {
        return SchedulePreferences.forMySchedule().suffixingKeysWith(scheduleMode);
    }

    public static AppWidgetPreferences forAppWidget(int appWidgetId) {
        return SchedulePreferences.forAppWidget().suffixingKeysWith(appWidgetId);
    }

    public static void removeEntriesRelatedToSeries(Series series) {
        SchedulePreferences.forMySchedule().removeEntriesRelatedToSeries(series);
        SchedulePreferences.forAppWidget().removeEntriesRelatedToSeries(series);
    }

    public static void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        SchedulePreferences.forMySchedule().removeEntriesRelatedToAllSeries(series);
        SchedulePreferences.forAppWidget().removeEntriesRelatedToAllSeries(series);
    }

    public static void removeEntriesRelatedToAppWidget(int appWidgetId) {
        SchedulePreferences.forAppWidget().removeEntriesRelatedToAppWidget(appWidgetId);
    }
}
