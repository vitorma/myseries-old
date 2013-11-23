package mobi.myseries.application.preferences;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import android.content.Context;

public class ScheduleWidgetPreferences extends SchedulePreferences {
    private static final int SCHEDULE_MODE_DEFAULT_VALUE = ScheduleMode.TO_WATCH;

    public ScheduleWidgetPreferences(Context context, int widgetId) {
        super(context);

        addKeySuffix(String.valueOf(widgetId));
    }

    public int scheduleMode() {
        return getInt(
                key(R.string.prefKey_scheduleWidget_scheduleMode),
                SCHEDULE_MODE_DEFAULT_VALUE);
    }

    public void putScheduleMode(int scheduleMode) {
        putInt(
                key(R.string.prefKey_scheduleWidget_scheduleMode),
                scheduleMode);
    }

    public void clear() {
        remove(key(R.string.prefKey_scheduleWidget_scheduleMode));
    }
}