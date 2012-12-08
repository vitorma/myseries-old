/*
 *   SeriesNotFoundException.java
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

package mobi.myseries.domain.source;

public class SeriesNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    private String seriesName;

    public SeriesNotFoundException() {
        super();
    }

    public SeriesNotFoundException(Throwable cause) {
        super(cause);
    }

    public SeriesNotFoundException withSeriesName(String seriesName) {
        this.seriesName = seriesName;
        return this;
    }

    public String seriesName() {
        return this.seriesName;
    }
}
