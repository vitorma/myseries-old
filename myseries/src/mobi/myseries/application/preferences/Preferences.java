package mobi.myseries.application.preferences;

import java.util.Collection;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.util.SparseArray;

public class Preferences {
    private static final String KEY_PREFIX_BACKUP_RESTORE = "BackupRestore";
    private static final String KEY_PREFIX_MYSCHEDULE = "MySchedule";
    private static final String KEY_PREFIX_MYSERIES = "MySeries";
    private static final String KEY_PREFIX_MYSTATISTICS = "MyStatistics";
    private static final String KEY_PREFIX_SCHEDULEWIDGET = "ScheduleWidget";
    private static final String KEY_PREFIX_UPDATE = "Update";
    private static final String KEY_PREFIX_SERIES_DETAILS = "Series";
    private static final String KEY_PREFIX_EPISODES = "Episodes";

    private static final String PREFERENCES_NAME = "mobi.myseries.preferences";
    private static final String PREFERENCES_NAME_APPWIDGET = "mobi.myseries.preferences.appwidget";

    private final Context context;
    private PrimitivePreferences forActivities;
    private BackupPreferences forBackupRestore;
    private MySchedulePreferences forMySchedule;
    private SparseArray<MySchedulePreferences> forMySchedules;
    private MySeriesPreferences forMySeries;
    private MyStatisticsPreferences forMyStatistics;
    private UpdatePreferences forUpdate;
    private SeriesDetailsPreferences forSeriesDetails;
    private EpisodesPreferences forEpisodes;
    private PrimitivePreferences forAppWidgets;
    private ScheduleWidgetPreferences forScheduleWidget;
    private SparseArray<ScheduleWidgetPreferences> forScheduleWidgets;

    public Preferences(Context context) {
        Validate.isNonNull(context, "context");
        this.context = context;
    }

    /* For activities */

    public PrimitivePreferences forActivities() {
        this.forActivities = new PrimitivePreferences(this.context, Preferences.PREFERENCES_NAME);
        return this.forActivities;
    }

    public BackupPreferences forBackupRestore() {
        if(this.forBackupRestore == null)
            this.forBackupRestore = new BackupPreferences(this.forActivities().addKeyPrefix(
                    Preferences.KEY_PREFIX_BACKUP_RESTORE));
        return forBackupRestore;
    }

    public MySchedulePreferences forMySchedule() {
        if(this.forMySchedule == null)
            this.forMySchedule = new MySchedulePreferences(this.forActivities().addKeyPrefix(
                    Preferences.KEY_PREFIX_MYSCHEDULE));
        return forMySchedule;
    }

    public MySchedulePreferences forMySchedule(int scheduleMode) {
        if(this.forMySchedules == null)
            this.forMySchedules = new SparseArray<MySchedulePreferences>();
        if(this.forMySchedules.get(scheduleMode) == null)
            this.forMySchedules.put(scheduleMode, 
                                    new MySchedulePreferences(this.forActivities()
                                            .addKeyPrefix(Preferences.KEY_PREFIX_MYSCHEDULE)
                                            .addKeyPrefix(String.valueOf(scheduleMode))));
        return this.forMySchedules.get(scheduleMode);
    }

    public MySeriesPreferences forMySeries() {
        if(this.forMySeries == null)
            this.forMySeries = new MySeriesPreferences(this.forActivities().addKeyPrefix(
                    Preferences.KEY_PREFIX_MYSERIES));
        return this.forMySeries;
    }

    public MyStatisticsPreferences forMyStatistics() {
        if(this.forMyStatistics == null)
            this.forMyStatistics = new MyStatisticsPreferences(this.forActivities().addKeyPrefix(
                    Preferences.KEY_PREFIX_MYSTATISTICS));
        return this.forMyStatistics;
    }

    public UpdatePreferences forUpdate() {
        if(this.forUpdate == null)
            this.forUpdate = new UpdatePreferences(this.forActivities().addKeyPrefix(
                    Preferences.KEY_PREFIX_UPDATE));
            return this.forUpdate;
    }

    public SeriesDetailsPreferences forSeriesDetails() {
        if(this.forSeriesDetails == null)
            this.forSeriesDetails = new SeriesDetailsPreferences(this.forActivities().addKeyPrefix(Preferences.KEY_PREFIX_SERIES_DETAILS));
        return this.forSeriesDetails;
    }

    public EpisodesPreferences forEpisodes() {
        if(this.forEpisodes == null)
            this.forEpisodes = new EpisodesPreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_EPISODES));
        return this.forEpisodes;
    }

    /* For app widgets */

    public PrimitivePreferences forAppWidgets() {
        this.forAppWidgets = new PrimitivePreferences(App.context(), Preferences.PREFERENCES_NAME_APPWIDGET);
        return this.forAppWidgets;
    }

    public ScheduleWidgetPreferences forScheduleWidget() {
        if(this.forScheduleWidget == null)
            this.forScheduleWidget = new ScheduleWidgetPreferences(this.forAppWidgets().addKeyPrefix(
                    Preferences.KEY_PREFIX_SCHEDULEWIDGET));
        return this.forScheduleWidget;
    }

    public ScheduleWidgetPreferences forScheduleWidget(int appWidgetId) {
        if(this.forScheduleWidgets == null)
            this.forScheduleWidgets = new SparseArray<ScheduleWidgetPreferences>();
        if(this.forScheduleWidgets.get(appWidgetId) == null)
            this.forScheduleWidgets.put(appWidgetId,
                    new ScheduleWidgetPreferences(this.forAppWidgets()
                            .addKeyPrefix(Preferences.KEY_PREFIX_SCHEDULEWIDGET)
                            .addKeyPrefix(String.valueOf(appWidgetId))));
        return this.forScheduleWidgets.get(appWidgetId);
    }

    /* Removal */

    public void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        this.forMySeries().removeEntriesRelatedToAllSeries(series);
        this.forMySchedule().removeEntriesRelatedToAllSeries(series);
        this.forScheduleWidget().removeEntriesRelatedToAllSeries(series);
        this.forMyStatistics().removeEntriesRelatedToAllSeries(series);
    }

    public void removeEntriesRelatedToAppWidget(int appWidgetId) {
        this.forScheduleWidget(appWidgetId).clear();
    }

    public void removeEntriesRelatedToSeries(Series series) {
        this.forMySeries().removeEntriesRelatedToSeries(series);
        this.forMySchedule().removeEntriesRelatedToSeries(series);
        this.forScheduleWidget().removeEntriesRelatedToSeries(series);
        this.forMyStatistics().removeEntriesRelatedToSeries(series);
    }
}
