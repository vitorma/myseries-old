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

import java.text.DateFormat;
import java.util.List;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.image.ImageloaderService;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.domain.model.Series;
import mobi.myseries.update.UpdateService;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class App extends Application {
    private static Environment environment;
    private static SearchSeriesService searchService;
    private static FollowSeriesService followSeriesService;
    private static ImageloaderService imageLoadService;
    private static Schedule schedule;
    private static UpdateService updateService;
    private static ErrorService errorService;
    private static ImageService imageProvider;
    private static MessageService messageService;

    @Override
    public void onCreate() {
        super.onCreate();

        environment = Environment.newEnvironment(this);
        imageProvider = new ImageService(
                environment.imageSource(),
                environment.imageRepository());
        errorService = new ErrorService();
        searchService = new SearchSeriesService(environment.theTVDB());
        imageLoadService = new ImageloaderService();
        followSeriesService = new FollowSeriesService(
                environment.theTVDB(),
                environment.repository(),
                environment.localization(),
                imageProvider,
                errorService());
        updateService = new UpdateService(
                environment.theTVDB(),
                environment.repository(),
                environment.localization(),
                imageProvider);
        schedule = new Schedule(
                environment.repository(),
                followSeriesService,
                updateService);
        messageService = new MessageService();
    }


    //TODO (Cleber) Turn this method private or delete it
    public static Environment environment() {
        return environment;
    }

    /* Use context() instead of environment().context() */

    public static Context context() {
        return environment.context();
    }

    /* Use resources() instead of environment().context().resources() */

    public static Resources resources() {
        return context().getResources();
    }

    public static ErrorService errorService(){
        return errorService;
    }

    // Search Series
    public static void searchSeries(String seriesName) {
        searchService.search(seriesName, localLanguage());
    }

    public static void registerSearchSeriesListener(SearchSeriesListener listener){
        searchService.registerListener(listener);
    }

    public static void deregisterSearchSeriesListener(SearchSeriesListener listener){
        searchService.deregisterListener(listener);
    }

    public static List<Series> getLastValidSearchResult(){
        return SearchSeriesService.getLastSearchResult();
    }

    private static String localLanguage() {
        return environment.localization().language();
    }

    /* SERIES FOLLOWING */

    public static FollowSeriesService followSeriesService() {
        return followSeriesService;
    }

    @Deprecated
    public static void registerSeriesFollowingListener(SeriesFollowingListener listener) {
        followSeriesService().registerSeriesFollowingListener(listener);
    }

    @Deprecated
    public static void follow(Series series) {
        followSeriesService().follow(series);
    }

    @Deprecated
    public static void stopFollowing(Series series) {
        followSeriesService().stopFollowing(series);
    }

    @Deprecated
    public static boolean follows(Series series) {
        return followSeriesService().follows(series);
    }

    // Update Series
    public static UpdateService updateSeriesService() {
        return updateService;
    }

    /* SERIES */

    public static Series getSeries(int seriesId) {
        return environment.seriesProvider().getSeries(seriesId);
    }

    /* IMAGES */

    public static ImageService imageProvider() {
        return imageProvider;
    }

    /* SCHEDULE */

    public static Schedule schedule() {
        return schedule;
    }

    /* LOCALIZATION */

    public static DateFormat dateFormat() {
        return environment.localization().dateFormat();
    }

    public static MessageService messageService() {
        return messageService;
    }
}
