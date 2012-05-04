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
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class MyScheduleActivity extends SherlockFragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.schedule);

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

        ab.addTab(recentTab);
        ab.addTab(todayTab, true);
        ab.addTab(upcomingTab);
    }
}
