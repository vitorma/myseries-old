/*
 *   SchedulePreferences.java
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

package mobi.myseries.gui.myschedule;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.PrimitivePreferences;
import mobi.myseries.gui.shared.SortMode;
import android.content.Context;

public class SchedulePreferences {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private static final String NAME = "mobi.myseries.gui.schedule.SchedulePreferences";

    private static final String SORT_MODE_KEY = "sortMode";
    private static final String SHOW_SPECIAL_EPISODES_KEY = "showSpecialEpisodes";
    private static final String SHOW_SEEN_EPISODES_KEY = "showSeenEpisodes";
    private static final String SHOW_SERIES_KEY = "showSeries";

    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;
    private static final boolean SHOW_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SEEN_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SERIES_DEFAULT_VALUE = true;

    private PrimitivePreferences primitive;

    private SchedulePreferences(Context context, String clientId) {
        this.primitive = new PrimitivePreferences(context, NAME).setKeySuffix(clientId);
    }

    public static SchedulePreferences from(Context context, String clientId) {
        return new SchedulePreferences(context, clientId);
    }

    public int sortMode() {
        return this.primitive.getInt(SORT_MODE_KEY, SORT_MODE_DEFAULT_VALUE);
    }

    public boolean showSpecialEpisodes() {
        return this.primitive.getBoolean(SHOW_SPECIAL_EPISODES_KEY, SHOW_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean showSeenEpisodes() {
        return this.primitive.getBoolean(SHOW_SEEN_EPISODES_KEY, SHOW_SEEN_EPISODES_DEFAULT_VALUE);
    }

    public boolean showSeries(int seriesId) {
        return this.primitive.getBoolean(SHOW_SERIES_KEY + seriesId, SHOW_SERIES_DEFAULT_VALUE);
    }

    public Map<Series, Boolean> seriesFilterOptions() {
        Map<Series, Boolean> seriesFilterOptions = new HashMap<Series, Boolean>();

        for (Series s : SERIES_PROVIDER.followedSeries()) {
            seriesFilterOptions.put(s, this.showSeries(s.id()));
        }

        return seriesFilterOptions;
    }

    public ScheduleSpecification fullSpecification() {
        ScheduleSpecification specification = new ScheduleSpecification()
            .specifySortMode(this.sortMode())
            .specifyInclusionOfSpecialEpisodes(this.showSpecialEpisodes())
            .specifyInclusionOfSeenEpisodes(this.showSeenEpisodes());

        for (Series s : SERIES_PROVIDER.followedSeries()) {
            specification.specifyInclusionOf(s, this.showSeries(s.id()));
        }

        return specification;
    }

    public boolean setSortMode(int sortMode) {
        return this.primitive.putInt(SORT_MODE_KEY, sortMode);
    }

    public boolean setIfShowSpecialEpisodes(boolean show) {
        return this.primitive.putBoolean(SHOW_SPECIAL_EPISODES_KEY, show);
    }

    public boolean setIfShowSeenEpisodes(boolean show) {
        return this.primitive.putBoolean(SHOW_SEEN_EPISODES_KEY, show);
    }

    public boolean setIfShowSeries(int seriesId, boolean show) {
        return this.primitive.putBoolean(SHOW_SERIES_KEY + seriesId, show);
    }

    public void setIfShowSeries(Map<Series, Boolean> filterOptions) {
        for (Series s : filterOptions.keySet()) {
            this.setIfShowSeries(s.id(), filterOptions.get(s));
        }
    }

    public void deletePreferencesRelatedTo(Series series) {
        this.primitive.remove(SHOW_SERIES_KEY + series.id());
    }

    public void deletePreferencesRelatedToAll(Collection<Series> series) {
        for (Series s : series) {
            this.deletePreferencesRelatedTo(s);
        }
    }
}
