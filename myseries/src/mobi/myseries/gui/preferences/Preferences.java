package mobi.myseries.gui.preferences;

import java.util.Collection;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;

public class Preferences {
    private static final String PREFERENCES_NAME = "mobi.myseries.preferences";
    private static final String PREFERENCES_NAME_APPWIDGET = "mobi.myseries.preferences.appwidget";

    private static final String KEY_PREFIX_MYSERIES = "MySeries";
    private static final String KEY_PREFIX_MYSCHEDULE = "MySchedule";
    private static final String KEY_PREFIX_UPDATE = "Update";
    private static final String KEY_PREFIX_BACKUP_RESTORE = "BackupRestore";
    private static final String KEY_PREFIX_SCHEDULEWIDGET = "ScheduleWidget";

    /* For activities */

    public static PrimitivePreferences forActivities() {
        return new PrimitivePreferences(App.context(), PREFERENCES_NAME);
    }

    public static MySeriesPreferences forMySeries() {
        return new MySeriesPreferences(forActivities().addKeyPrefix(KEY_PREFIX_MYSERIES));
    }

    public static MySchedulePreferences forMySchedule() {
        return new MySchedulePreferences(forActivities().addKeyPrefix(KEY_PREFIX_MYSCHEDULE));
    }

    public static MySchedulePreferences forMySchedule(int scheduleMode) {
        return new MySchedulePreferences(forActivities().addKeyPrefix(KEY_PREFIX_MYSCHEDULE).addKeyPrefix(String.valueOf(scheduleMode)));
    }

    public static UpdatePreferences forUpdate() {
        return new UpdatePreferences(forActivities().addKeyPrefix(KEY_PREFIX_UPDATE));
    }

    public static BackupPreferences forBackupRestore() {
        return new BackupPreferences(forActivities().addKeyPrefix(KEY_PREFIX_BACKUP_RESTORE));
    }

    /* For app widgets */

    public static PrimitivePreferences forAppWidgets() {
        return new PrimitivePreferences(App.context(), PREFERENCES_NAME_APPWIDGET);
    }

    public static ScheduleWidgetPreferences forScheduleWidget() {
        return new ScheduleWidgetPreferences(forAppWidgets().addKeyPrefix(KEY_PREFIX_SCHEDULEWIDGET));
    }

    public static ScheduleWidgetPreferences forScheduleWidget(int appWidgetId) {
        return new ScheduleWidgetPreferences(forAppWidgets().addKeyPrefix(KEY_PREFIX_SCHEDULEWIDGET).addKeyPrefix(String.valueOf(appWidgetId)));
    }

    /* Removal */

    public static void removeEntriesRelatedToSeries(Series series) {
        forMySeries().removeEntriesRelatedToSeries(series);
        forMySchedule().removeEntriesRelatedToSeries(series);
        forScheduleWidget().removeEntriesRelatedToSeries(series);
    }

    public static void removeEntriesRelatedToAllSeries(Collection<Series> series) {
        forMySeries().removeEntriesRelatedToAllSeries(series);
        forMySchedule().removeEntriesRelatedToAllSeries(series);
        forScheduleWidget().removeEntriesRelatedToAllSeries(series);
    }

    public static void removeEntriesRelatedToAppWidget(int appWidgetId) {
        forScheduleWidget(appWidgetId).clear();
    }
}
