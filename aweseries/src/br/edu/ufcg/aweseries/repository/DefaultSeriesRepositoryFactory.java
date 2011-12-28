/*
 *   DefaultSeriesRepositoryFactory.java
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


package br.edu.ufcg.aweseries.repository;

import android.content.Context;

public class DefaultSeriesRepositoryFactory implements SeriesRepositoryFactory {

    private Context context;

    public DefaultSeriesRepositoryFactory(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context should not be null");
        }

        this.context = context;
    }

    @Override
    public SeriesRepository newSeriesDatabase() {
        return new SeriesDatabase(this.context);
    }

    @Override
    public SeriesRepository newSeriesCachedRepository() {
        return new SeriesCachedRepository(this.newSeriesDatabase());
    }
}
