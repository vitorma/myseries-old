package mobi.myseries.application.notification.service;

import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.preferences.NotificationPreferences;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.Log;

public class NotificationScheduler extends Service {
    private static final long FIRST_RUN_DELAY = 10 * 1000;

    public static long wakeupInterval() {
        return AlarmManager.INTERVAL_HALF_DAY;
    }

    private WakeLock mWakeLock;

    public static void setupAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent newIntent = new Intent(context, NotificationScheduler.class);

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, newIntent, PendingIntent.FLAG_NO_CREATE);

        if (pendingIntent != null) {
            Log.d(NotificationScheduler.class.getName(), "Service is already scheduled");
            return;
        }

        pendingIntent = PendingIntent.getService(context, 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.cancel(pendingIntent);

        // Needed because AlarmManager usually doesn't respect first execution
        alarmManager.set(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                pendingIntent);

        alarmManager.setInexactRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + FIRST_RUN_DELAY,
                wakeupInterval(),
                pendingIntent
                );

        Log.d(NotificationScheduler.class.getName(), "Service scheduled. Interval (ms): " + wakeupInterval());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getName());
        mWakeLock.acquire();

        scheduleNotificationsForEpisodesStartingSoon();

        mWakeLock.release();

        return START_NOT_STICKY;
    }

    private static void scheduleNotificationsForEpisodesStartingSoon() {
        Collection<Episode> unaired = unwatchedEpisodes();
        Context context = App.context();
        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(context);

        NotificationPreferences prefs = App.preferences().forNotifications();

        long notificationAdvance = toMiliseconds(prefs.notificationAdvanceMinutes());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long currentTime = System.currentTimeMillis();

        Log.d(NotificationScheduler.class.getName(), "looking for episodes starting soon...");

        for (Episode episode : unaired) {
            if (episode.airDate() == null) {
                continue;
            }

            long difftime = episode.airDate().getTime() - currentTime;

            if ((0 < difftime) && (difftime <= wakeupInterval())) {

                Series s = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

                Intent in = new Intent(context, ScheduledNotificationAgent.class);
                in.putExtra("seriesId", episode.seriesId());
                in.putExtra("seasonNumber", episode.seasonNumber());
                in.putExtra("episodeNumber", episode.number());

                PendingIntent pi = PendingIntent.getService(context, episode.seriesId(), in, PendingIntent.FLAG_CANCEL_CURRENT);

                long notificationTime = episode.airDate().getTime() - notificationAdvance;

                Log.d(NotificationScheduler.class.getName(),
                        String.format(
                                "%s starts soon. Notification scheduled to %s",
                                s.name(),
                                airtimeFormat.format(new Date(notificationTime))
                                )
                        );

                alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pi);
            }
        }
    }

    private static long toMiliseconds(long minutes) {
        return minutes * 60 * 1000;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private static Collection<Episode> unwatchedEpisodes() {
        List<Episode> unairedEpisodes = new LinkedList<Episode>();

        Collection<Series> allSeries = App.seriesFollowingService().getAllFollowedSeries();

        for (Series s : allSeries) {
            unairedEpisodes.addAll(s.episodesBy(new UnairedEpisodeSpecification()));
        }

        return unairedEpisodes;
    }
}
