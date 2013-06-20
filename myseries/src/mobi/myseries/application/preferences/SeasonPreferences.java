package mobi.myseries.application.preferences;

import mobi.myseries.gui.shared.SortMode;

public class SeasonPreferences {
    private static final String SORT_MODE_KEY = "sortMode";

    private static final int SORT_MODE_DEFAULT_VALUE = SortMode.OLDEST_FIRST;

    private PrimitivePreferences primitive;

    public SeasonPreferences(PrimitivePreferences primitive) {
        this.primitive = primitive;
    }

    public int sortMode() {
        return this.primitive.getInt(SORT_MODE_KEY, SORT_MODE_DEFAULT_VALUE);
    }

    public void putSortMode(int sortMode) {
        this.primitive.putInt(SORT_MODE_KEY, sortMode);
    }
}
