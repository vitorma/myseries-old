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
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.ScheduleFragment.NextFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.RecentFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.UpcomingFragment;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabsAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity {
    private static final String TAG = MyScheduleActivity.class.getName();

    private int currentMode;
    private StateHolder stateHolder;

    public static Intent newIntent(Context context, int scheduleMode) {
        Intent intent = new Intent(context, MyScheduleActivity.class);

        intent.putExtra(Extra.SCHEDULE_MODE, scheduleMode);

        return intent;
    }

    /* Interface */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myschedule);
        this.setUpCurrentModeFromExtras(savedInstanceState);
        this.setUpStateHolder();
        this.setUpActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SCHEDULE_MODE, this.getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
       return this.stateHolder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportMenuInflater().inflate(R.menu.myschedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public ScheduleAdapter getAdapterForScheduleMode(int scheduleMode) {
        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                return this.stateHolder.adapterForModeRecent;
            case ScheduleMode.UPCOMING:
                return this.stateHolder.adapterForModeUpcoming;
            case ScheduleMode.NEXT:
                return this.stateHolder.adapterForModeNext;
            default:
                return null;
        }
    }

    /* Auxiliary */

    private void setUpCurrentModeFromExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.currentMode = savedInstanceState.getInt(Extra.SCHEDULE_MODE);
        } else {
            this.currentMode = this.getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
        }
    }

    private void setUpStateHolder() {
        Object retainedStateHolder = this.getLastCustomNonConfigurationInstance();

        if (retainedStateHolder != null) {
            this.stateHolder = (StateHolder) retainedStateHolder;
            return;
        }

        this.stateHolder = new StateHolder();

        this.stateHolder.adapterForModeRecent = this.newAdapterForScheduleMode(ScheduleMode.RECENT);
        this.stateHolder.adapterForModeUpcoming = this.newAdapterForScheduleMode(ScheduleMode.UPCOMING);
        this.stateHolder.adapterForModeNext = this.newAdapterForScheduleMode(ScheduleMode.NEXT);
    }

    private ScheduleAdapter newAdapterForScheduleMode(int scheduleMode) {
        SchedulePreferences preferences = getSchedulePreferences(scheduleMode);

        return new ScheduleAdapter(this, scheduleMode, preferences.fullSpecification());
    }

    public static SchedulePreferences getSchedulePreferences(int scheduleMode) {
        return SchedulePreferences.from(App.context(), TAG + scheduleMode);
    }

    private void setUpActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.my_schedule);

        this.setUpNavigationFor(actionBar);
    }

    private void setUpNavigationFor(ActionBar actionBar) {
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ViewPager viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        TabsAdapter tabsAdapter = new TabsAdapter(this, actionBar, viewPager);

        tabsAdapter.addTab(this.newTab(R.string.recent), RecentFragment.class, null, ScheduleMode.RECENT, false);
        tabsAdapter.addTab(this.newTab(R.string.next), NextFragment.class, null, ScheduleMode.NEXT, false);
        tabsAdapter.addTab(this.newTab(R.string.upcoming), UpcomingFragment.class, null, ScheduleMode.UPCOMING, false);

        actionBar.setSelectedNavigationItem(this.currentMode);
    }

    private ActionBar.Tab newTab(int tabNameResource) {
        return this.getSupportActionBar().newTab().setText(tabNameResource);
    }

    private static class StateHolder {
        private ScheduleAdapter adapterForModeRecent;
        private ScheduleAdapter adapterForModeUpcoming;
        private ScheduleAdapter adapterForModeNext;
    }
}
