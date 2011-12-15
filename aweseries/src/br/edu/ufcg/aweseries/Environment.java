/*
 *   Environment.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries;

import android.content.Context;
import br.edu.ufcg.aweseries.repository.DefaultSeriesRepositoryFactory;
import br.edu.ufcg.aweseries.repository.SeriesRepositoryFactory;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

/**
 * Singleton repository. Stores all singleton objects. Allow dependency
 * injection.
 */
public class Environment {

    private Context context;
    private SeriesRepositoryFactory seriesRepositoryFactory;
    private TheTVDB theTVDB;
    private SeriesProvider seriesProvider;
    private LocalizationProvider localization;

    private static final String apiKey = "6F2B5A871C96FB05";

    /**
     * If you know what you are doing, use this method to instantiate an
     * Environment.
     * @see Environment()
     * @see App.environment()
     */
    public static Environment newEnvironment(Context context) {
        return new Environment(context);
    }

    /**
     * @see newSeriesProvider()
     */
    private Environment(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context should not be null");
        }
        this.context = context;
    }

    /**
     * @return the application context.
     */
    public Context context() {
        return this.context;
    }

    /**
     * @return the SeriesProvider for the app.
     */
    public SeriesProvider seriesProvider() {
        if (this.seriesProvider == null) {
            this.seriesProvider = this.defaultSeriesProvider();
        }
        return this.seriesProvider;
    }

    /**
     * @return a default series provider for the production environment
     */
    private SeriesProvider defaultSeriesProvider() {
        return SeriesProvider.newInstance(this.theTVDB(), this.seriesRepositoryFactory());
    }

    /**
     * Set the environment's series provider to newSeriesProvider.
     * If null, a new default series provider will be instantiated.
     */
    public void setSeriesProvider(SeriesProvider newSeriesProvider) {
        this.seriesProvider = newSeriesProvider;
    }

    /**
     * @return the thetvdb interface for the app.
     */
    public TheTVDB theTVDB() {
        if (this.theTVDB == null) {
            this.theTVDB = this.defaultTheTVDB();
        }
        return this.theTVDB;
    }

    /**
     * @return a default thetvdb interface for the production environment
     */
    private TheTVDB defaultTheTVDB() {
        return new TheTVDB(apiKey);
    }

    /**
     * Set the environment's thetvdb interface to newTheTVDB.
     * If null, a new default thetvdb interface will be instantiated.
     */
    public void setTheTVDBTo(TheTVDB newTheTVDB) {
        this.theTVDB = newTheTVDB;
    }

    public SeriesRepositoryFactory seriesRepositoryFactory() {
        if (this.seriesRepositoryFactory == null) {
            this.seriesRepositoryFactory = this.defaultSeriesRepositoryFactory();
        }
        return this.seriesRepositoryFactory;
    }

    private SeriesRepositoryFactory defaultSeriesRepositoryFactory() {
        return new DefaultSeriesRepositoryFactory(this.context);
    }

    public void setSeriesRepositoryFactoryTo(SeriesRepositoryFactory newSeriesRepositoryFactory) {
        this.seriesRepositoryFactory = newSeriesRepositoryFactory;
    }

    /**
     * @return the thetvdb interface for the app.
     */
    public LocalizationProvider localization() {
        if (this.localization == null) {
            this.localization = this.defaultLocalization();
        }
        return this.localization;
    }

    /**
     * @return a default thetvdb interface for the production environment
     */
    private LocalizationProvider defaultLocalization() {
        return new LocalizationProvider();
    }

    /**
     * Set the environment's thetvdb interface to newLocalization.
     * If null, a new default thetvdb interface will be instantiated.
     */
    public void setLocalizationTo(LocalizationProvider newLocalization) {
        this.localization = newLocalization;
    }

}
