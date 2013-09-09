package mobi.myseries.application;

import java.text.DateFormat;

import mobi.myseries.R;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.message.MessageService;
import mobi.myseries.application.notification.NotificationService;
import mobi.myseries.application.preferences.Preferences;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.search.SearchService;
import mobi.myseries.application.trending.TrendingService;
import mobi.myseries.application.update.UpdateService;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

public class App extends Application {
    private static Environment environment;
    private static SearchService searchService;
    private static TrendingService trendingService;
    private static SeriesFollowingService seriesFollowingService;
    private static Schedule schedule;
    private static UpdateService updateService;
    private static ImageService imageService;
    private static MessageService messageService;
    private static SeriesProvider seriesProvider;
    private static BackupService backupService;
    private static BroadcastService broadcastService;
    private static NotificationService notificationService;
    private static Preferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.setLogger(Log.ANDROID_LOGGER);

        environment = new EnvironmentImpl(this);

        broadcastService = new BroadcastService(this);

        imageService = new ImageService(
                environment.imageSource(),
                environment.imageRepository(),
                this.getResources().getDimensionPixelSize(R.dimen.myseries_poster_width),
                this.getResources().getDimensionPixelSize(R.dimen.myseries_poster_height),
                this.getResources().getDimensionPixelSize(R.dimen.myschedule_poster_width),
                this.getResources().getDimensionPixelSize(R.dimen.myschedule_poster_height));

        searchService = new SearchService(environment);
        trendingService = new TrendingService(environment);
        seriesFollowingService = new SeriesFollowingService(environment, imageService);

        updateService = new UpdateService(
                environment.seriesSource(),
                environment.seriesRepository(),
                environment.localizationProvider(),
                imageService,
                broadcastService);

        schedule = new Schedule(
                environment.seriesRepository(),
                seriesFollowingService,
                updateService);

        backupService = new BackupService(environment.seriesRepository(), environment.dropboxHelper());

        messageService = new MessageService(seriesFollowingService, updateService, backupService);

        notificationService = new NotificationService(this, updateService, backupService);

        seriesProvider = new SeriesProvider(environment.seriesRepository(), broadcastService);

        preferences = new Preferences(this);
    }

    public static Context context() {
        return environment.context();
    }

    public static String getApplicationName() {
        int stringId = context().getApplicationInfo().labelRes;
        return context().getString(stringId);
    }

    public static Resources resources() {
        return context().getResources();
    }

    public static SearchService searchService() {
        return searchService;
    }

    public static TrendingService trendingService() {
        return trendingService;
    }

    public static SeriesFollowingService seriesFollowingService() {
        return seriesFollowingService;
    }

    public static UpdateService updateSeriesService() {
        return updateService;
    }

    public static SeriesProvider seriesProvider() {
        return seriesProvider;
    }

    public static ImageService imageService() {
        return imageService;
    }

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

    public static NotificationService notificationService() {
        return notificationService;
    }

    public static BackupService backupService() {
        return backupService;
    }

    public static Preferences preferences() {
        return preferences;
    }
}
