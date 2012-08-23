/*
 *   MyScheduleActivity.java
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

package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.EpisodeListFragment.RecentEpisodesFragment;
import mobi.myseries.gui.myschedule.EpisodeListFragment.TodayEpisodesFragment;
import mobi.myseries.gui.myschedule.EpisodeListFragment.UpcomingEpisodesFragment;
import mobi.myseries.gui.myseries.MySeriesActivity;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity {
    private int scheduleMode;

    public static Intent newIntent(Context context, int scheduleMode) {
        Intent intent = new Intent(context, MyScheduleActivity.class);

        intent.putExtra(Extra.SCHEDULE_MODE, scheduleMode);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myschedule);

        Bundle extras = this.getIntent().getExtras();
        this.scheduleMode = extras.getInt(Extra.SCHEDULE_MODE);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.my_schedule);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab recentTab = ab.newTab().setText(R.string.recent);
        recentTab.setTabListener(new ScheduleTabListener(new RecentEpisodesFragment()));

        ActionBar.Tab todayTab = ab.newTab().setText(R.string.today);
        todayTab.setTabListener(new ScheduleTabListener(new TodayEpisodesFragment()));

        ActionBar.Tab upcomingTab = ab.newTab().setText(R.string.upcoming);
        upcomingTab.setTabListener(new ScheduleTabListener(new UpcomingEpisodesFragment()));

        ab.addTab(recentTab, ScheduleMode.RECENT, false);
        ab.addTab(todayTab, ScheduleMode.TODAY, false);
        ab.addTab(upcomingTab, ScheduleMode.UPCOMING, false);

        ab.setSelectedNavigationItem(this.scheduleMode);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        this.getIntent().putExtra(Extra.SCHEDULE_MODE, this.scheduleMode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MySeriesActivity.class);
                this.startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class ScheduleTabListener implements ActionBar.TabListener {
        private SherlockListFragment fragment;

        private ScheduleTabListener(SherlockListFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            MyScheduleActivity.this.scheduleMode = tab.getPosition();
            ft.replace(R.id.container, this.fragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            ft.remove(this.fragment);
        }
    }
}
