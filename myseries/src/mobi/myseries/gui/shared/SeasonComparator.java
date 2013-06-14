package mobi.myseries.gui.shared;

import java.util.Comparator;

import mobi.myseries.domain.model.Season;

public class SeasonComparator {

    public static Comparator<Season> fromSortMode(int sortMode) {
        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                return byOldestFirst();
            case SortMode.NEWEST_FIRST:
                return byNewestFirst();
            default:
                throw new IllegalArgumentException("invalid sortMode");
        }
    }

    private static Comparator<Season> byOldestFirst() {
        return new Comparator<Season>() {
            @Override
            public int compare(Season s1, Season s2) {
                return s1.number() - s2.number();
            }
        };
    }

    private static Comparator<Season> byNewestFirst() {
        return new Comparator<Season>() {
            @Override
            public int compare(Season s1, Season s2) {
                return s2.number() - s1.number();
            }
        };
    }
}
