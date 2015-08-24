package mobi.myseries.application.notification.service;

import mobi.myseries.gui.appwidget.ScheduleWidget;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationScheduler.setupAlarm(context);
        ScheduleWidget.scheduleAlarm(context);
    }
}
