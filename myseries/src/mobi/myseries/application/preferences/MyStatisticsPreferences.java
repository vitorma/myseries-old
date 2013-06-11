package mobi.myseries.application.preferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;

public class MyStatisticsPreferences {

    public MyStatisticsPreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
    }

    private static final String COUNT_SPECIAL_EPISODES_KEY = "countSpecialEpisodes";
    private static final String COUNT_UNAIRED_EPISODES_KEY = "countUnairedEpisodes";
    private static final String COUNT_SERIES_KEY = "countSeries";

    private static final boolean COUNT_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean COUNT_UNAIRED_EPISODES_DEFAULT_VALUE = false;
    private static final boolean COUNT_SERIES_DEFAULT_VALUE = true;

    protected PrimitivePreferences primitive;

    public boolean countSpecialEpisodes() {
        return this.primitive.getBoolean(MyStatisticsPreferences.COUNT_SPECIAL_EPISODES_KEY,
            MyStatisticsPreferences.COUNT_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean countUnairedEpisodes() {
        return this.primitive.getBoolean(MyStatisticsPreferences.COUNT_UNAIRED_EPISODES_KEY,
            MyStatisticsPreferences.COUNT_UNAIRED_EPISODES_DEFAULT_VALUE);
    }

    public boolean countSeries(int seriesId) {
        return this.primitive.getBoolean(MyStatisticsPreferences.COUNT_SERIES_KEY + seriesId,
            MyStatisticsPreferences.COUNT_SERIES_DEFAULT_VALUE);
    }

    public Map<Series, Boolean> seriesToCount() {
        Map<Series, Boolean> seriesToShow = new HashMap<Series, Boolean>();

        for (Series s : App.seriesProvider().followedSeries()) {
            seriesToShow.put(s, this.countSeries(s.id()));
        }

        return seriesToShow;
    }

    public void putIfCountSpecialEpisodes(boolean count) {
        this.primitive.putBoolean(MyStatisticsPreferences.COUNT_SPECIAL_EPISODES_KEY, count);
    }

    public void putIfCountUnairedEpisodes(boolean count) {
        this.primitive.putBoolean(MyStatisticsPreferences.COUNT_UNAIRED_EPISODES_KEY, count);
    }

    public void putIfCountSeries(int seriesId, boolean count) {
        this.primitive.putBoolean(MyStatisticsPreferences.COUNT_SERIES_KEY + seriesId, count);
    }

    public void putIfCountSeries(Map<Series, Boolean> seriesToCount) {
        for (Series s : seriesToCount.keySet()) {
            this.putIfCountSeries(s.id(), seriesToCount.get(s));
        }
    }

    public void removeEntriesRelatedToSeries(Series series) {
        this.primitive.remove(MyStatisticsPreferences.COUNT_SERIES_KEY + series.id());
    }

    public void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        for (Series s : series) {
            this.removeEntriesRelatedToSeries(s);
        }
    }

}
