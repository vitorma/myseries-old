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

import mobi.myseries.application.image.ImageLoadSupplicant;
import mobi.myseries.application.image.ImageProvider;
import mobi.myseries.application.image.ImageloaderService;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ExternalStorageNotAvailableException;
import mobi.myseries.domain.repository.ImageDirectory;
import android.app.Application;
import android.graphics.Bitmap;

public class App extends Application {
    private static Environment environment;
    private static SearchSeriesService searchService;
    private static FollowSeriesService followSeriesService;
    private static ImageloaderService imageLoadService;
    private static Schedule schedule;
    private static UpdateService updateService;
    private static ErrorService errorService;
    private static ImageProvider imageProvider;

    @Override
    public void onCreate() {
        super.onCreate();

        environment = Environment.newEnvironment(this);
        //TODO Use Environment#imageRepository (when ImageCache is implemented) instead of ImageDirectory
        imageProvider = new ImageProvider(environment.theTVDB(), new ImageDirectory(this));
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
    }

    public static Environment environment() {
        return environment;
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

    public static ImageProvider imageProvider() {
        return imageProvider;
    }

    //TODO Remove ASAP
    public static void loadPoster(Series series, ImageLoadSupplicant suplicant) {
        imageLoadService.loadPoster(series, suplicant);
    }

    //TODO Remove ASAP
    public static Bitmap seriesPoster(int seriesId) {
        //TODO Delegate

        Bitmap poster = null;

        try {
            poster = environment.imageRepository().getSeriesPoster(seriesId);
        } catch (ExternalStorageNotAvailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (poster == null) {
            poster = imageProvider.genericPosterImage();
        }

        return poster;
    }

    /* SCHEDULE */

    public static Schedule schedule() {
        return schedule;
    }

    /* LOCALIZATION */

    public static DateFormat dateFormat() {
        return environment.localization().dateFormat();
    }
}
