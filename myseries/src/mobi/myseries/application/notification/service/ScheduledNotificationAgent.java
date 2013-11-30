package mobi.myseries.application.notification.service;

import java.text.DateFormat;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.notification.AndroidNotificationDispatcher;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationLauncher;
import mobi.myseries.application.notification.TextOnlyNotification;
import mobi.myseries.application.preferences.NotificationPreferences;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

public class ScheduledNotificationAgent extends Service {
    private WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationPreferences prefs = App.preferences().forNotifications();

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,  this.getClass().getName());

        mWakeLock.acquire();

        NotificationLauncher notificationLauncher = new NotificationLauncher(new AndroidNotificationDispatcher(App.context()));
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(App.context());

        int seriesId = intent.getExtras().getInt("seriesId");
        long episodeId = intent.getExtras().getLong("episodeId");
        int seasonNumber = intent.getExtras().getInt("seasonNumber");
        int episodeNumber = intent.getExtras().getInt("episodeNumber");

        Series s = App.seriesFollowingService().getFollowedSeries(seriesId);

        if (!prefs.notificationsEnabled() || s == null || isHiddenInSchedule(seriesId)) {
            //TODO: Is there a better way to do this?
            return START_NOT_STICKY;
        }

        Episode e = s.season(seasonNumber).episode(episodeNumber);

        String episodeFormat = String.format(
                App.context().getResources().getString(R.string.episode_number_format),
                seasonNumber,
                episodeNumber
                );

        String notificationMessage = String.format(
                App.context().getResources().getString(R.string.episode_starting_notification),
                s.name(),
                episodeFormat,
                timeFormat.format(e.airDate())
                );

        Log.d(getClass().getName(), "Notification sound: " + App.preferences().forNotifications().notificationSound());

        Notification notification = new TextOnlyNotification(seriesId, notificationMessage);
        notification.setSoundUri(App.preferences().forNotifications().notificationSound());
        notificationLauncher.launch(notification);

        mWakeLock.release();
        return START_NOT_STICKY;
    }

    private boolean isHiddenInSchedule(int seriesId) {
        int[] seriesToHide = App.preferences().forSchedule().seriesToHide();

        for(int i : seriesToHide) { if (i == seriesId) { return true; } }

        return false;
    }

}
