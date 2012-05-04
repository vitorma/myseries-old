/*
 *   App.java
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

package mobi.myseries.application;

import android.app.Application;

public class App extends Application {
    private static Environment environment;

    @Override
    public void onCreate() {
        super.onCreate();
        environment = Environment.newEnvironment(this);
    }

    public static Environment environment() {
        return environment;
    }

    public static void searchSeries(String seriesName, SearchSeriesListener listener) {
        new SearchSeriesService(environment.theTVDB()).search(seriesName, localLanguage(), listener);
    }

    private static String localLanguage() {
        return environment.localization().language();
    }
}
