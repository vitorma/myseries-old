package mobi.myseries.application.notification.service;

import java.text.DateFormat;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.NotificationPreferences;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.schedule.dualpane.ScheduleDualPaneActivity;
import mobi.myseries.gui.schedule.singlepane.ScheduleListActivity;
import mobi.myseries.gui.shared.UniversalImageLoader;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
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
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        mWakeLock.acquire();

        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(App.context());

        int seriesId = intent.getExtras().getInt("seriesId");
        int seasonNumber = intent.getExtras().getInt("seasonNumber");
        int episodeNumber = intent.getExtras().getInt("episodeNumber");

        Series s = App.seriesFollowingService().getFollowedSeries(seriesId);

        if (!prefs.notificationsEnabled() || s == null || isHiddenInSchedule(seriesId)) { // TODO: Is there a better way to do this?
            mWakeLock.release();
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

        String screenPath = UniversalImageLoader.httpURI(e.screenUrl());

        Bitmap picture = UniversalImageLoader.loader().loadImageSync(screenPath);

        Log.d(getClass().getName(), "Notification screen: " + (picture == null ? ("null") : picture.toString()));

        Notification noti = new NotificationCompat.Builder(App.context())
                .setContentTitle(App.context().getText(R.string.app))
                .setContentText(notificationMessage)
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(notificationDefaults())
                .setSound(App.preferences().forNotifications().notificationSound())
                .setContentIntent(PendingIntent.getActivity(App.context(), 0, clickIntent(), 0))
                .setStyle(new BigPictureStyle()
                        .bigPicture(picture)
                        .setSummaryText(notificationMessage)
                        .setBigContentTitle(App.context().getText(R.string.app)))
                .build();

        NotificationManager manager = (NotificationManager) App.context().getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(e.hashCode(), noti);

        mWakeLock.release();
        return START_NOT_STICKY;
    }

    private boolean isHiddenInSchedule(int seriesId) {
        int[] seriesToHide = App.preferences().forSchedule().seriesToHide();

        for (int i : seriesToHide) {
            if (i == seriesId) {
                return true;
            }
        }

        return false;
    }

    private Intent clickIntent() {
        Intent intent = App.resources().getBoolean(R.bool.isTablet) ?
                ScheduleDualPaneActivity.newIntent(App.context(), ScheduleMode.UNAIRED) :
                ScheduleListActivity.newIntent(App.context(), ScheduleMode.UNAIRED);

        return intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private int notificationDefaults() {
        NotificationPreferences prefs = App.preferences().forNotifications();

        int defaults = 0;

        if (prefs.vibrationEnabled()) {
            defaults |= Notification.DEFAULT_VIBRATE;
        }
        if (prefs.lightsEnabled()) {
            defaults |= Notification.DEFAULT_LIGHTS;
        }
        defaults |= Notification.DEFAULT_SOUND;

        return defaults;
    }

}
