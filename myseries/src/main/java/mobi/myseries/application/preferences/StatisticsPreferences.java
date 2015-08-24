package mobi.myseries.application.preferences;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.R;
import mobi.myseries.shared.Iterables;
import android.content.Context;

public class StatisticsPreferences extends BasePreferences {
    private static final boolean COUNT_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean COUNT_UNAIRED_EPISODES_DEFAULT_VALUE = false;
    private static final Set<String> SERIES_TO_COUNT_DEFAULT_VALUE = new HashSet<String>();

    public StatisticsPreferences(Context context) {
        super(context);
    }

    public boolean countSpecialEpisodes() {
        return getBoolean(
                key(R.string.prefKey_statistics_countSpecialEpisodes),
                COUNT_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean countUnairedEpisodes() {
        return getBoolean(
                key(R.string.prefKey_statistics_countUnairedEpisodes),
                COUNT_UNAIRED_EPISODES_DEFAULT_VALUE);
    }

    public boolean dontCountSeries(int id) {
        return _seriesToDontCount().contains(String.valueOf(id));
    }

    public int[] seriesToDontCount() {
        return Iterables.toIntArray(_seriesToDontCount());
    }

    private Set<String> _seriesToDontCount() {
        return getStringSet(
                key(R.string.prefKey_statistics_seriesToDontCount),
                SERIES_TO_COUNT_DEFAULT_VALUE);
    }

    public void putIfCountSpecialEpisodes(boolean count) {
        putBoolean(
                key(R.string.prefKey_statistics_countSpecialEpisodes),
                count);
    }

    public void putIfCountUnairedEpisodes(boolean count) {
        putBoolean(
                key(R.string.prefKey_statistics_countUnairedEpisodes),
                count);
    }

    public void putSeriesToDontCount(int[] seriesIds) {
        putStringSet(
                key(R.string.prefKey_statistics_seriesToDontCount),
                Iterables.toStringSet(seriesIds));
    }

    public void removeEntriesRelatedToSeries(int seriesId) {
        removeValueFromStringSet(
                key(R.string.prefKey_statistics_seriesToDontCount),
                String.valueOf(seriesId));
    }

    public void removeEntriesRelatedToAllSeries(int[] seriesIds) {
        removeAllValuesFromStringSet(
                key(R.string.prefKey_statistics_seriesToDontCount),
                Iterables.toStringSet(seriesIds));
    }
}
