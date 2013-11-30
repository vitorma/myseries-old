package mobi.myseries.application.preferences;

import mobi.myseries.R;
import android.content.Context;
import android.net.Uri;

public class NotificationPreferences extends BasePreferences {
    private static final int NOTIFICATION_ADVANCE_MINUTES_DEFAULT_VALUE = 10;
    private static final boolean NOTIFICATION_ENABLED_DEFAULT_VALUE = true;
    private static final String NOTIFICATION_SOUND_DEFAULT = "content://settings/system/notification_sound";

    public NotificationPreferences(Context context) {
        super(context);
    }

    public boolean notificationsEnabled() {
        return getBoolean(key(R.string.prefKey_notifications_enabled),
                NOTIFICATION_ENABLED_DEFAULT_VALUE);
    }

    public int notificationAdvanceMinutes() {
        return getInt(key(R.string.prefKey_notification_advance_minutes),
                NOTIFICATION_ADVANCE_MINUTES_DEFAULT_VALUE);
    }

    public Uri notificationSound() {
        return Uri.parse(getString(key(R.string.prefKey_notification_sound),
                NOTIFICATION_SOUND_DEFAULT));
    }

    public void putNotificationAdvanceMinutes(int notificationAdvance) {
        putInt(key(R.string.prefKey_notification_advance_minutes),
                notificationAdvance);
    }

    public void putNotificationsEnabled(boolean enabled) {
        putBoolean(key(R.string.prefKey_notifications_enabled), enabled);
    }

}
