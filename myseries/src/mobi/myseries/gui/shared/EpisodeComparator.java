/*
 *   EpisodeComparator.java
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

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Validate;

public class EpisodeComparator {

    public static Comparator<Episode> byNumber() {
        return new Comparator<Episode>() {
            @Override
            public int compare(Episode episode1, Episode episode2) {
                checkThatBothAreNotNull(episode1, episode2);

                return compareByNumber(episode1, episode2);
            }
        };
    }

    public static Comparator<Episode> byOldestFirst() {
        return new Comparator<Episode>() {
            @Override
            public int compare(Episode episode1, Episode episode2) {
                checkThatBothAreNotNull(episode1, episode2);

                int byAirdate = compareByAirdate(episode1, episode2);

                if (byAirdate != 0) {
                    return byAirdate;
                }

                int bySeason = compareBySeason(episode1, episode2);

                if (bySeason != 0) {
                    return bySeason;
                }

                return compareByNumber(episode1, episode2);
            }
        };
    }

    public static Comparator<Episode> byNewestFirst() {
        return new Comparator<Episode>() {
            @Override
            public int compare(Episode episode1, Episode episode2) {
                return byOldestFirst().compare(episode2, episode1);
            }
        };
    }

    /* Auxiliary */

    private static void checkThatBothAreNotNull(Episode episode1, Episode episode2) {
        Validate.isNonNull(episode1, "episode1");
        Validate.isNonNull(episode2, "episode2");
    }

    private static int compareByNumber(Episode episode1, Episode episode2) {
        return episode1.number() - episode2.number();
    }

    private static int compareBySeason(Episode episode1, Episode episode2) {
        return episode1.seasonNumber() - episode2.seasonNumber();
    }

    private static int compareByAirdate(Episode episode1, Episode episode2) {
        return Dates.compareByNullLast(episode1.airDate(), episode2.airDate());
    }
}
