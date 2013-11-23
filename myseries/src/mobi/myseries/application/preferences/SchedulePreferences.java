package mobi.myseries.application.preferences;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.Iterables;
import android.content.Context;

public class SchedulePreferences extends BasePreferences {
    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;
    private static final boolean SHOW_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean SHOW_WATCHED_EPISODES_DEFAULT_VALUE = false;
    private static final Set<String> SERIES_TO_SHOW_DEFAULT_VALUE = new HashSet<String>();

    public SchedulePreferences(Context context) {
        super(context);
    }

    public int sortMode() {
        return getInt(
                key(R.string.prefKey_schedule_sortMode),
                SORT_MODE_DEFAULT_VALUE);
    }

    public boolean showSpecialEpisodes() {
        return getBoolean(
                key(R.string.prefKey_schedule_showSpecialEpisodes),
                SHOW_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean showWatchedEpisodes() {
        return getBoolean(
                key(R.string.prefKey_schedule_showWatchedEpisodes),
                SHOW_WATCHED_EPISODES_DEFAULT_VALUE);
    }

    public boolean hideSeries(int seriesId) {
        return _seriesToHide().contains(String.valueOf(seriesId));
    }

    public int[] seriesToHide() {
        return Iterables.toIntArray(_seriesToHide());
    }

    private Set<String> _seriesToHide() {
        return getStringSet(
                key(R.string.prefKey_schedule_seriesToHide),
                SERIES_TO_SHOW_DEFAULT_VALUE);
    }

    public ScheduleSpecification fullSpecification() {
        return new ScheduleSpecification()
                .specifySortMode(sortMode())
                .includingSpecialEpisodes(showSpecialEpisodes())
                .includingWatchedEpisodes(showWatchedEpisodes())
                .excludingAllSeries(seriesToHide());
    }

    public void putSortMode(int sortMode) {
        putInt(
                key(R.string.prefKey_schedule_sortMode),
                sortMode);
    }

    public void putIfShowSpecialEpisodes(boolean show) {
        putBoolean(
                key(R.string.prefKey_schedule_showSpecialEpisodes),
                show);
    }

    public void putIfShowWatchedEpisodes(boolean show) {
        putBoolean(
                key(R.string.prefKey_schedule_showWatchedEpisodes),
                show);
    }

    public void putSeriesToHide(int[] seriesIds) {
        putStringSet(
                key(R.string.prefKey_schedule_seriesToHide),
                Iterables.toStringSet(seriesIds));
    }

    public void removeEntriesRelatedToSeries(int seriesId) {
        removeValueFromStringSet(
                key(R.string.prefKey_schedule_seriesToHide),
                String.valueOf(seriesId));
    }

    public void removeEntriesRelatedToAllSeries(int[] seriesIds) {
        removeAllValuesFromStringSet(
                key(R.string.prefKey_schedule_seriesToHide),
                Iterables.toStringSet(seriesIds));
    }
}
