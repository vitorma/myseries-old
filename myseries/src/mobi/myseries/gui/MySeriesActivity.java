/*
 *   MySeriesActivity.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.UpdateListener;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MySeriesActivity extends SherlockFragmentActivity implements UpdateListener {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();

    private SeriesListFragment seriesListFragment;
    private SeriesCoverFlowFragment seriesCoverFlowFragment;

    public MySeriesActivity() {
        seriesProvider.addListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.my_series_layout);

        if (savedInstanceState == null) {
            this.seriesListFragment = new SeriesListFragment();
            this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.series_list_fragment, this.seriesListFragment);

            this.seriesCoverFlowFragment = new SeriesCoverFlowFragment();
            this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.series_coverflow_fragment, this.seriesCoverFlowFragment);
        }
    }

    //Menu--------------------------------------------------------------------------------------------------------------

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(R.menu.series_list_options_menu, menu);
//        this.updateMenuItem = menu.findItem(R.id.updateMenuItem);
//        this.updateMenuItem.setEnabled(this.updateMenuItemStatus);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addSeriesMenuItem:
//                this.showSearchActivity();
//                return true;
//            case R.id.updateMenuItem:
//                seriesProvider.updateData();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    //Update------------------------------------------------------------------------------------------------------------
    
    private UpdateNotificationLauncher updateNotificationLauncher;
    private MenuItem updateMenuItem;
    private boolean updateMenuItemStatus = true;
    
    @Override
    public void onUpdateStart() {
        this.updateNotificationLauncher.launchUpdatingNotification();
        this.disableUpdateMenuItem();
    }
    
    @Override
    public void onUpdateFailure() {
        this.updateNotificationLauncher.clearNotification();
        this.updateNotificationLauncher.launchUpdatingFailureNotification();
        this.enableUpdateMenuItem();
    }
    
    @Override
    public void onUpdateSuccess() {
        this.updateNotificationLauncher.clearNotification();
        this.enableUpdateMenuItem();
    }
    
    private void disableUpdateMenuItem() {
        if (this.updateMenuItem != null) {
            this.updateMenuItem.setEnabled(false);
        }
        this.updateMenuItemStatus = false;
    }
    
    private void enableUpdateMenuItem() {
        if (this.updateMenuItem != null) {
            this.updateMenuItem.setEnabled(true);
        }
        this.updateMenuItemStatus = true;
    }

    private class UpdateNotificationLauncher {
        private final int id = 0;
        private final int updateNotificationText = R.string.updating_series_data;
        private final int updateNotificationTitle = R.string.updating_series_notification_title;
        private final int updateFailureText = R.string.update_failure_notification_message;
        private final int updateFailureTitle = R.string.updating_series_failure_notification_title;
        private final int icon = R.drawable.stat_sys_download;
        private final NotificationManager nm = (NotificationManager) MySeriesActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        private void launchNotification(String title, String text) {
            final long when = System.currentTimeMillis();

            final Notification notification = new Notification(this.icon,
                    MySeriesActivity.this.getString(this.updateNotificationText), when);
            final Context context = MySeriesActivity.this.getApplicationContext();

            final Intent notificationIntent = new Intent(MySeriesActivity.this, MySeriesActivity.class);
            final PendingIntent contentIntent = PendingIntent.getActivity(MySeriesActivity.this,
                    0, notificationIntent, 0);

            notification.setLatestEventInfo(context, title, text, contentIntent);

            this.nm.notify(this.id, notification);
        }

        public void launchUpdatingNotification() {
            this.launchNotification(
                    MySeriesActivity.this.getString(this.updateNotificationTitle),
                    MySeriesActivity.this.getString(this.updateNotificationText));
        }

        public void launchUpdatingFailureNotification() {
            this.launchNotification(
                    MySeriesActivity.this.getString(this.updateFailureTitle),
                    MySeriesActivity.this.getString(this.updateFailureText));
        }

        public void clearNotification() {
            this.nm.cancelAll();
        }
    }

    //Search------------------------------------------------------------------------------------------------------------

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchActivity.class);
        this.startActivity(intent);
    }
}