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

package mobi.myseries.gui.schedule;

import mobi.myseries.R;
import mobi.myseries.gui.MySeriesActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity {
    private static final int TODAY = 1;
    private static final String CURRENT_TAB = "currentTab";
    private int currentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.schedule);

        if (savedInstanceState == null) {
            this.currentTab = TODAY;
        } else {
            this.currentTab = savedInstanceState.getInt(CURRENT_TAB);
        }

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.my_schedule);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab recentTab = ab.newTab().setText(R.string.schedule_recent);
        recentTab.setTabListener(new ScheduleTabListener(new RecentEpisodesFragment()));

        ActionBar.Tab todayTab = ab.newTab().setText(R.string.schedule_today);
        todayTab.setTabListener(new ScheduleTabListener(new TodayEpisodesFragment()));

        ActionBar.Tab upcomingTab = ab.newTab().setText(R.string.schedule_upcoming);
        upcomingTab.setTabListener(new ScheduleTabListener(new UpcomingEpisodesFragment()));

        ab.addTab(recentTab, false);
        ab.addTab(todayTab, false);
        ab.addTab(upcomingTab, false);

        ab.setSelectedNavigationItem(this.currentTab);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_TAB, this.currentTab);
        super.onSaveInstanceState(outState);
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

    public class ScheduleTabListener implements ActionBar.TabListener {
        private SherlockListFragment fragment;

        public ScheduleTabListener(SherlockListFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) { }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            MyScheduleActivity.this.currentTab = tab.getPosition();
            ft.replace(R.id.container, this.fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(this.fragment);
        }
    }
}
