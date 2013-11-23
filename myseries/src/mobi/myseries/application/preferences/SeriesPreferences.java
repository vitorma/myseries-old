package mobi.myseries.application.preferences;

import mobi.myseries.R;
import mobi.myseries.gui.shared.SortMode;
import android.content.Context;

public class SeriesPreferences extends BasePreferences {
    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;

    public SeriesPreferences(Context context) {
        super(context);
    }

    public int sortMode() {
        return getInt(
                key(R.string.prefKey_series_sortMode),
                SORT_MODE_DEFAULT_VALUE);
    }

    public void putSortMode(int sortMode) {
        putInt(
                key(R.string.prefKey_series_sortMode),
                sortMode);
    }
}
