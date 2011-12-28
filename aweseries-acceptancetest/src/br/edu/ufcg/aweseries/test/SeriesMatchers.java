/*
 *   SeriesMatchers.java
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

package br.edu.ufcg.aweseries.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesMatchers {

    public static SeriesNameMatcher namedAs(String seriesName) {
        return new SeriesNameMatcher(seriesName);
    }

    private static class SeriesNameMatcher extends BaseMatcher<Series> {

        private String seriesName;

        public SeriesNameMatcher(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof Series)) {
                return false;
            }

            Series series = (Series) item;            
            return (series.getName().equals(this.seriesName));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a Series named as ");
            description.appendValue(this.seriesName);
        }
    }
}
