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

package mobi.myseries.gui.preferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.appwidget.ItemPageBrowser;
import mobi.myseries.gui.shared.SortMode;

public abstract class SchedulePreferences<T extends SchedulePreferences<T>> {
    private static final String SORT_MODE_KEY = "SortMode";
    private static final String SHOW_SPECIAL_EPISODES_KEY = "ShowSpecialEpisodes";
    private static final String SHOW_SEEN_EPISODES_KEY = "ShowSeenEpisodes";
    private static final String SHOW_SERIES_KEY = "ShowSeries";

    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;
    private static final boolean SHOW_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SEEN_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SERIES_DEFAULT_VALUE = true;

    protected PrimitivePreferences primitive;

    protected SchedulePreferences(String name) {
        this.primitive = new PrimitivePreferences(name);
    }

    public static MySchedulePreferences forMySchedule() {
        return new MySchedulePreferences();
    }

    public static AppWidgetPreferences forAppWidget() {
        return new AppWidgetPreferences();
    }

    @SuppressWarnings("unchecked")
    public <S> T suffixingKeysWith(S suffix) {
        this.primitive.appendingSuffixToKeys(suffix.toString());

        return (T) this;
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

    public Map<Series, Boolean> seriesToShow() {
        Map<Series, Boolean> seriesToShow = new HashMap<Series, Boolean>();

        for (Series s : App.seriesProvider().followedSeries()) {
            seriesToShow.put(s, this.showSeries(s.id()));
        }

        return seriesToShow;
    }

    public ScheduleSpecification fullSpecification() {
        return new ScheduleSpecification()
            .specifySortMode(this.sortMode())
            .specifyInclusionOfSpecialEpisodes(this.showSpecialEpisodes())
            .specifyInclusionOfSeenEpisodes(this.showSeenEpisodes())
            .specifyInclusionOfAllSeries(this.seriesToShow());
    }

    public void setSortMode(int sortMode) {
        this.primitive.putInt(SORT_MODE_KEY, sortMode);
    }

    public void setIfShowSpecialEpisodes(boolean show) {
        this.primitive.putBoolean(SHOW_SPECIAL_EPISODES_KEY, show);
    }

    public void setIfShowSeenEpisodes(boolean show) {
        this.primitive.putBoolean(SHOW_SEEN_EPISODES_KEY, show);
    }

    public void setIfShowSeries(int seriesId, boolean show) {
        this.primitive.putBoolean(SHOW_SERIES_KEY + seriesId, show);
    }

    public void setIfShowSeries(Map<Series, Boolean> seriesToShow) {
        for (Series s : seriesToShow.keySet()) {
            this.setIfShowSeries(s.id(), seriesToShow.get(s));
        }
    }

    public void removeEntriesRelatedToSeries(Series series) {
        String prefix = SHOW_SERIES_KEY + series.id();

        for (String key : this.primitive.getKeys()) {
            if (key.startsWith(prefix)) {
                this.primitive.remove(key);
            }
        }
    }

    public void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        for (Series s : series) {
            this.removeEntriesRelatedToSeries(s);
        }
    }

    /* Concrete children */

    public static class MySchedulePreferences extends SchedulePreferences<MySchedulePreferences> {
        private static final String NAME = "MySchedulePreferences";

        private MySchedulePreferences() {
            super(NAME);
        }
    }

    public static class AppWidgetPreferences extends SchedulePreferences<AppWidgetPreferences> {
        private static final String NAME = "AppWidgetPreferences";
        private static final String SCHEDULE_MODE_KEY = "ScheduleMode";
        private static final int SCHEDULE_MODE_DEFAULT_VALUE = ScheduleMode.NEXT;
        private static final String CURRENT_PAGE_KEY = "CurrentPage";
        private static final int CURRENT_PAGE_DEFAULT_VALUE = ItemPageBrowser.FIRST_PAGE;

        private AppWidgetPreferences() {
            super(NAME);
        }

        public int scheduleMode() {
            return this.primitive.getInt(SCHEDULE_MODE_KEY, SCHEDULE_MODE_DEFAULT_VALUE);
        }

        public int currentPage() {
            return this.primitive.getInt(CURRENT_PAGE_KEY, CURRENT_PAGE_DEFAULT_VALUE);
        }

        public void setScheduleMode(int scheduleMode) {
            this.primitive.putInt(SCHEDULE_MODE_KEY, scheduleMode);
        }

        public void setCurrentPage(int currentPage) {
            this.primitive.putInt(CURRENT_PAGE_KEY, currentPage);
        }

        public void removeEntriesRelatedToAppWidget(int appWidgetId) {
            String suffix = PrimitivePreferences.SUFFIX_SEPARATOR + appWidgetId;

            for (String key : this.primitive.getKeys()) {
                if (key.endsWith(suffix)) {
                    this.primitive.remove(key);
                }
            }
        }
    }
}
