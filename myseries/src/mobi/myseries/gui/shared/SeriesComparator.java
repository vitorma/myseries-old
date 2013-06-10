/*
 *   SeriesComparator.java
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

package mobi.myseries.gui.shared;

import java.util.Comparator;

import mobi.myseries.domain.model.Series;

public class SeriesComparator {

    public static Comparator<Series> bySortMode(int sortMode) {
        switch (sortMode) {
            case SortMode.A_Z:
                return byAscendingAlphabeticalOrder();
            case SortMode.Z_A:
                return byDescendingAlphabeticalOrder();
            default:
                throw new RuntimeException("invalid sort mode for series");
        }
    }

    public static Comparator<Series> byAscendingAlphabeticalOrder() {
        return new Comparator<Series>() {
            @Override
            public int compare(Series s1, Series s2) {
                return s1.name().compareTo(s2.name());
            }
        };
    }

    public static Comparator<Series> byDescendingAlphabeticalOrder() {
        return new Comparator<Series>() {
            @Override
            public int compare(Series s1, Series s2) {
                return s2.name().compareTo(s1.name());
            }
        };
    }
}