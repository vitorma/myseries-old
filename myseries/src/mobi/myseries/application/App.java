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

import mobi.myseries.R;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.broadcast.ConfigurationChangeService;
import mobi.myseries.application.error.ErrorService;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.message.MessageService;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.search.SeriesSearch;
import mobi.myseries.application.update.UpdateService;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class App extends Application {
    private static Environment environment;
    private static SeriesSearch seriesSearch;
    private static FollowSeriesService followSeriesService;
    private static Schedule schedule;
    private static UpdateService updateService;
    private static ErrorService errorService;
    private static ImageService imageService;
    private static MessageService messageService;
    private static SeriesProvider seriesProvider;
    private static BackupService backupService;
    private static BroadcastService broadcastService;

    @Override
    public void onCreate() {
        super.onCreate();

        environment = new Environment(this);

        broadcastService = new BroadcastService(this);

        imageService = new ImageService(
                environment.imageSource(),
                environment.imageRepository(),
                this.getResources().getDimensionPixelSize(R.dimen.poster_width_thumbnail),
                this.getResources().getDimensionPixelSize(R.dimen.poster_height_thumbnail));

        errorService = new ErrorService();

        seriesSearch = new SeriesSearch(
                environment.seriesSource(),
                environment.trendingSource());

        followSeriesService = new FollowSeriesService(
                environment.seriesSource(),
                environment.seriesRepository(),
                environment.localizationProvider(),
                imageService,
                errorService,
                broadcastService);

        updateService = new UpdateService(
                environment.seriesSource(),
                environment.seriesRepository(),
                environment.localizationProvider(),
                imageService,
                broadcastService);

        schedule = new Schedule(
                environment.seriesRepository(),
                followSeriesService,
                updateService);

        backupService = new BackupService(environment.seriesRepository());

        messageService = new MessageService(followSeriesService, updateService, backupService);

        seriesProvider = new SeriesProvider(environment.seriesRepository(), broadcastService);

        this.startService(ConfigurationChangeService.newIntent(this));
    }

    public static Context context() {
        return environment.context();
    }

    public static Resources resources() {
        return context().getResources();
    }

    public static ErrorService errorService(){
        return errorService;
    }

    /* SERIES SEARCHING */

    public static SeriesSearch seriesSearch() {
        return seriesSearch;
    }

    /* SERIES FOLLOWING */

    public static FollowSeriesService followSeriesService() {
        return followSeriesService;
    }

    /* UPDATE */

    public static UpdateService updateSeriesService() {
        return updateService;
    }

    /* SERIES */

    public static SeriesProvider seriesProvider() {
        return seriesProvider;
    }

    /* IMAGES */

    public static ImageService imageService() {
        return imageService;
    }

    /* SCHEDULE */

    public static Schedule schedule() {
        return schedule;
    }

    /* LOCALIZATION */

    public static DateFormat dateFormat() {
        return environment.localizationProvider().dateFormat();
    }

    public static MessageService messageService() {
        return messageService;
    }

    public static BackupService backupService() {
        return backupService;
    }
}
