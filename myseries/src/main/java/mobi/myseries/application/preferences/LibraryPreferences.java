package mobi.myseries.application.preferences;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.R;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.Iterables;
import android.content.Context;

public class LibraryPreferences extends BasePreferences {
    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.A_Z;
    private static final boolean COUNT_SPECIAL_EPISODES_DEFAULT_VALUE = false;
    private static final boolean COUNT_UNAIRED_EPISODES_DEFAULT_VALUE = false;
    private static final Set<String> SERIES_TO_SHOW_DEFAULT_VALUE = new HashSet<String>();

    public LibraryPreferences(Context context) {
        super(context);
    }

    public int sortMode() {
        return getInt(
                key(R.string.prefKey_library_sortMode),
                SORT_MODE_DEFAULT_VALUE);
    }

    public boolean countSpecialEpisodes() {
        return getBoolean(
                key(R.string.prefKey_library_countSpecialEpisodes),
                COUNT_SPECIAL_EPISODES_DEFAULT_VALUE);
    }

    public boolean countUnairedEpisodes() {
        return getBoolean(
                key(R.string.prefKey_library_countUnairedEpisodes),
                COUNT_UNAIRED_EPISODES_DEFAULT_VALUE);
    }

    public int[] seriesToHide() {
        return Iterables.toIntArray(getStringSet(
                key(R.string.prefKey_library_seriesToHide),
                SERIES_TO_SHOW_DEFAULT_VALUE));
    }

    public void putSortMode(int sortMode) {
        putInt(
                key(R.string.prefKey_library_sortMode),
                sortMode);
    }

    public void putCountSpecialEpisodes(boolean show) {
        putBoolean(
                key(R.string.prefKey_library_countSpecialEpisodes),
                show);
    }

    public void putCountUnairedEpisodes(boolean show) {
        putBoolean(
                key(R.string.prefKey_library_countUnairedEpisodes),
                show);
    }

    public void putSeriesToHide(int[] seriesIds) {
        putStringSet(
                key(R.string.prefKey_library_seriesToHide),
                Iterables.toStringSet(seriesIds));
    }

    public void putSeriesToHide(int seriesId, boolean hide) {
        if (hide) {
            addValueToStringSet(
                    key(R.string.prefKey_library_seriesToHide),
                    String.valueOf(seriesId));
        } else {
            removeValueFromStringSet(
                    key(R.string.prefKey_library_seriesToHide),
                    String.valueOf(seriesId));
        }
    }

    public void removeEntriesRelatedToSeries(int seriesId) {
        removeValueFromStringSet(
                key(R.string.prefKey_library_seriesToHide),
                String.valueOf(seriesId));
    }

    public void removeEntriesRelatedToAllSeries(int[] seriesIds) {
        removeAllValuesFromStringSet(
                key(R.string.prefKey_library_seriesToHide),
                Iterables.toStringSet(seriesIds));
    }
}
