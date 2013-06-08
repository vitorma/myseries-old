package mobi.myseries.application.preferences;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SortMode;

public class MySeriesPreferences {
    private static final String SORT_MODE_KEY = "sortMode";
    private static final String COUNT_SPECIAL_EPISODES_KEY = "countSpecialEpisodes";
    private static final String COUNT_UNAIRED_EPISODES_KEY = "countUnairedEpisodes";
    private static final String SHOW_SERIES_KEY = "showSeries";

    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.A_Z;
    private static final boolean COUNT_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean COUNT_UNAIRED_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_SERIES_DEFAULT_VALUE = true;

    private PrimitivePreferences primitive;

    public MySeriesPreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
    }

    /* Access */

    public int sortMode() {
        return this.primitive.getInt(SORT_MODE_KEY, SORT_MODE_DEFAULT_VALUE);
    }

    public boolean countSpecialEpisodes() {
        return this.primitive.getBoolean(COUNT_SPECIAL_EPISODES_KEY, COUNT_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean countUnairedEpisodes() {
        return this.primitive.getBoolean(COUNT_UNAIRED_EPISODES_KEY, COUNT_UNAIRED_EPISODES_DEFAULT_VALUE);
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

    /* Modification */

    public void putSortMode(int sortMode) {
        this.primitive.putInt(SORT_MODE_KEY, sortMode);
    }

    public void putCountSpecialEpisodes(boolean show) {
        this.primitive.putBoolean(COUNT_SPECIAL_EPISODES_KEY, show);
    }

    public void putCountUnairedEpisodes(boolean show) {
        this.primitive.putBoolean(COUNT_UNAIRED_EPISODES_KEY, show);
    }

    public void putShowSeries(int seriesId, boolean show) {
        this.primitive.putBoolean(SHOW_SERIES_KEY + seriesId, show);
    }

    public void putShowSeries(Map<Series, Boolean> seriesToShow) {
        for (Series s : seriesToShow.keySet()) {
            this.putShowSeries(s.id(), seriesToShow.get(s));
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
