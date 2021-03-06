package mobi.myseries.application;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.support.multidex.MultiDex;
import mobi.myseries.R;
import mobi.myseries.application.activityevents.ActivityEventsService;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.features.features.Features;
import mobi.myseries.application.features.store.Store;
import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.message.MessageService;
import mobi.myseries.application.notification.NotificationService;
import mobi.myseries.application.notification.service.NotificationScheduler;
import mobi.myseries.application.preferences.Preferences;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.search.SearchService;
import mobi.myseries.application.trending.TrendingService;
import mobi.myseries.application.update.UpdateService;

import java.text.DateFormat;

public class App extends Application {
    private static Environment environment;

    /* (Cleber) These guys already extend ApplicationService */
    private static SearchService searchService;
    private static TrendingService trendingService;
    private static SeriesFollowingService seriesFollowingService;
    private static MarkingService markingService;
    private static ActivityEventsService activityEventsService;
    private static Store store;

    /* XXX (Cleber) These guys should extend ApplicationService */
    private static UpdateService updateService;
    private static BackupService backupService;

    /* XXX (Cleber) This guy could be in Environment. Some services depend on it. */
    private static ImageService imageService;

    /* TODO (Cleber) These guys are constructed from a set of ApplicationService. They could be grouped. */
    private static Schedule schedule;
    private static MessageService messageService;
    private static NotificationService notificationService;

    /* (Cleber) This guy is ok */
    private static Preferences preferences;
    private static Features features;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.setLogger(Log.ANDROID_LOGGER);

        environment = new EnvironmentImpl(this);

        imageService = new ImageService(
                environment.imageRepository(),
                environment.communications(),
                this.getResources().getDimensionPixelSize(R.dimen.myseries_poster_width),
                this.getResources().getDimensionPixelSize(R.dimen.myseries_poster_height),
                this.getResources().getDimensionPixelSize(R.dimen.myschedule_poster_width),
                this.getResources().getDimensionPixelSize(R.dimen.myschedule_poster_height));

        searchService = new SearchService(environment);
        trendingService = new TrendingService(environment);
        seriesFollowingService = new SeriesFollowingService(environment, imageService);
        markingService = new MarkingService(environment);

        updateService = new UpdateService(environment, imageService);
        backupService = new BackupService(environment, imageService);

        schedule = new Schedule(seriesFollowingService, updateService, markingService);
        messageService = new MessageService(seriesFollowingService, updateService, backupService);
        notificationService = new NotificationService(this, updateService, backupService);

        preferences = new Preferences(this);

        activityEventsService = new ActivityEventsService(environment);

        store = new Store(environment, activityEventsService);
        features = new Features(store, preferences);

        NotificationScheduler.setupAlarm(context());
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

    public static MarkingService markingService() {
        return markingService;
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

    public static Features features() {
        return features;
    }

    public static Store store() {
        return store;
    }

    public static ActivityEventsService activityEvents() {
        return activityEventsService;
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
