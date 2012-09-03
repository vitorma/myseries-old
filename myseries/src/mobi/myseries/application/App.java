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

import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ExternalStorageNotAvailableException;
import android.app.Application;
import android.graphics.Bitmap;

public class App extends Application {
    private static Environment environment;
    private static SearchSeriesService searchService;
    private static FollowSeriesService followSeriesService;
    private static ImageloaderService imageLoadService;
    private static Schedule schedule;
    private static UpdateSeriesService updateService;

    @Override
    public void onCreate() {
        super.onCreate();
        environment = Environment.newEnvironment(this);
        searchService = new SearchSeriesService(environment.theTVDB());
        imageLoadService = new ImageloaderService();
        schedule = new Schedule(environment.repository());
    }

    public static Environment environment() {
        return environment;
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
    

    // Follow Series

    private static FollowSeriesService followSeriesService() {
        if (followSeriesService == null) {
            followSeriesService = new FollowSeriesService(environment.theTVDB(),
                                                          environment.repository(),
                                                          environment.localization(),
                                                          environment.imageProvider());
        }

        return followSeriesService;
    }

    public static void registerSeriesFollowingListener(SeriesFollowingListener listener) {
        followSeriesService().registerSeriesFollowingListener(listener);
    }

    public static void follow(Series series) {
        followSeriesService().follow(series);
    }

    public static void stopFollowing(Series series) {
        followSeriesService().stopFollowing(series);
    }

    public static boolean follows(Series series) {
        return followSeriesService().follows(series);
    }
    
    // Update Series
    
    public static void updateSeriesData() {
        updateSeriesService().updateSeriesData();
    }
    
    private static UpdateSeriesService updateSeriesService() {
        if (updateService == null) {
            updateService = new UpdateSeriesService(environment.theTVDB(),
                                                    environment.repository(),
                                                    environment.localization(),
                                                    environment.imageProvider());
        }
        
        return updateService;
    }

    /* SERIES */


    public static Series getSeries(int seriesId) {
        return environment.seriesProvider().getSeries(seriesId);
    }

    /* IMAGES */

    public static void loadPoster(Series series, ImageLoadSupplicant suplicant) {
        imageLoadService.loadPoster(series, suplicant);
    }

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
            poster = environment.imageProvider().genericPosterImage();
        }

        return poster;
    }

    /* SCHEDULE */

    public static Schedule schedule() {
        return schedule;
    }

//    public static List<Episode> scheduledEpisodes(int scheduleMode, int sortMode) {
//        return schedule.episodes(scheduleMode, sortMode);
//    }

    /* LOCALIZATION */

    public static DateFormat dateFormat() {
        return environment.localization().dateFormat();
    }
}
