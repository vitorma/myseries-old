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


package br.edu.ufcg.aweseries;

import android.app.Application;

/**
 * The way to get the context from outside an Activity as seen at
 * http://stackoverflow.com/questions/4391720/android-how-can-i-get-a-resources-object-from-a-static-context
 */
public class App extends Application {

    private static Environment environment;

    @Override
    public void onCreate() {
        super.onCreate();
        environment = Environment.newEnvironment(this);
    }

    /**
     * @return the application's environment
     * @see Environment
     */
    public static Environment environment() {
    	return environment;
    }
}
