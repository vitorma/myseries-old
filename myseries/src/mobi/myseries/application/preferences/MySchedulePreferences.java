package mobi.myseries.application.preferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SortMode;

public class MySchedulePreferences {
    private static final String SORT_MODE_KEY = "sortMode";
    private static final String SHOW_SPECIAL_EPISODES_KEY = "showSpecialEpisodes";
    private static final String SHOW_SEEN_EPISODES_KEY = "showSeenEpisodes";
    private static final String SHOW_SERIES_KEY = "showSeries";

    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;
    private static final boolean SHOW_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SEEN_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SERIES_DEFAULT_VALUE = true;

    protected PrimitivePreferences primitive;

    public MySchedulePreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
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

    public void putSortMode(int sortMode) {
        this.primitive.putInt(SORT_MODE_KEY, sortMode);
    }

    public void putIfShowSpecialEpisodes(boolean show) {
        this.primitive.putBoolean(SHOW_SPECIAL_EPISODES_KEY, show);
    }

    public void putIfShowSeenEpisodes(boolean show) {
        this.primitive.putBoolean(SHOW_SEEN_EPISODES_KEY, show);
    }

    public void putIfShowSeries(int seriesId, boolean show) {
        this.primitive.putBoolean(SHOW_SERIES_KEY + seriesId, show);
    }

    public void putIfShowSeries(Map<Series, Boolean> seriesToShow) {
        for (Series s : seriesToShow.keySet()) {
            this.putIfShowSeries(s.id(), seriesToShow.get(s));
        }
    }

    public void removeEntriesRelatedToSeries(Series series) {
        this.primitive.remove(SHOW_SERIES_KEY + series.id());
    }

    public void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        for (Series s : series) {
            this.removeEntriesRelatedToSeries(s);
        }
    }
}
