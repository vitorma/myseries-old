package mobi.myseries.application.preferences;

import java.util.Collection;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.content.Context;

public class Preferences {
    private static final String PREFERENCES_NAME = "mobi.myseries.preferences";
    private static final String PREFERENCES_NAME_APPWIDGET = "mobi.myseries.preferences.appwidget";

    private static final String KEY_PREFIX_MYSERIES = "MySeries";
    private static final String KEY_PREFIX_MYSCHEDULE = "MySchedule";
    private static final String KEY_PREFIX_UPDATE = "Update";
    private static final String KEY_PREFIX_BACKUP_RESTORE = "BackupRestore";
    private static final String KEY_PREFIX_SCHEDULEWIDGET = "ScheduleWidget";

    private Context context;

    public Preferences(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
    }

    /* For activities */

    public PrimitivePreferences forActivities() {
        return new PrimitivePreferences(this.context, PREFERENCES_NAME);
    }

    public MySeriesPreferences forMySeries() {
        return new MySeriesPreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_MYSERIES));
    }

    public MySchedulePreferences forMySchedule() {
        return new MySchedulePreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_MYSCHEDULE));
    }

    public MySchedulePreferences forMySchedule(int scheduleMode) {
        return new MySchedulePreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_MYSCHEDULE).addKeyPrefix(String.valueOf(scheduleMode)));
    }

    public UpdatePreferences forUpdate() {
        return new UpdatePreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_UPDATE));
    }

    public BackupPreferences forBackupRestore() {
        return new BackupPreferences(this.forActivities().addKeyPrefix(KEY_PREFIX_BACKUP_RESTORE));
    }

    /* For app widgets */

    public PrimitivePreferences forAppWidgets() {
        return new PrimitivePreferences(App.context(), PREFERENCES_NAME_APPWIDGET);
    }

    public ScheduleWidgetPreferences forScheduleWidget() {
        return new ScheduleWidgetPreferences(this.forAppWidgets().addKeyPrefix(KEY_PREFIX_SCHEDULEWIDGET));
    }

    public ScheduleWidgetPreferences forScheduleWidget(int appWidgetId) {
        return new ScheduleWidgetPreferences(this.forAppWidgets().addKeyPrefix(KEY_PREFIX_SCHEDULEWIDGET).addKeyPrefix(String.valueOf(appWidgetId)));
    }

    /* Removal */

    public void removeEntriesRelatedToSeries(Series series) {
        this.forMySeries().removeEntriesRelatedToSeries(series);
        this.forMySchedule().removeEntriesRelatedToSeries(series);
        this.forScheduleWidget().removeEntriesRelatedToSeries(series);
    }

    public void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        this.forMySeries().removeEntriesRelatedToAllSeries(series);
        this.forMySchedule().removeEntriesRelatedToAllSeries(series);
        this.forScheduleWidget().removeEntriesRelatedToAllSeries(series);
    }

    public void removeEntriesRelatedToAppWidget(int appWidgetId) {
        this.forScheduleWidget(appWidgetId).clear();
    }
}
