package mobi.myseries.gui.preferences;

import mobi.myseries.application.schedule.ScheduleMode;

public class ScheduleWidgetPreferences extends MySchedulePreferences {
    private static final String SCHEDULE_MODE_KEY = "scheduleMode";
    private static final int SCHEDULE_MODE_DEFAULT_VALUE = ScheduleMode.NEXT;

    public ScheduleWidgetPreferences(PrimitivePreferences primitive) {
        super(primitive);
    }

    public int scheduleMode() {
        return this.primitive.getInt(SCHEDULE_MODE_KEY, SCHEDULE_MODE_DEFAULT_VALUE);
    }

    public void putScheduleMode(int scheduleMode) {
        this.primitive.putInt(SCHEDULE_MODE_KEY, scheduleMode);
    }

    public void clear() {
        this.primitive.clear();
    }
}