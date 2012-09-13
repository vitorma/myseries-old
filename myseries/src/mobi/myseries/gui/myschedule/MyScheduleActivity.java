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
import mobi.myseries.application.schedule.SortMode;
import mobi.myseries.gui.myschedule.ScheduleFragment.NextFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.RecentFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.UpcomingFragment;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabsAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity {
    private static final String PREFS_NAME = "mobi.myseries.gui.schedule.SchedulePreferences";
    private static final String PREF_SORT_MODE_KEY = "sortMode_";
    private static final String PREF_INCLUSION_OF_SPECIAL_EPISODES_KEY = "includesSpecialEpisodes_";
    private static final String PREF_INCLUSION_OF_SEEN_EPISODES_KEY = "includesSeenEpisodes_";
    private static final String PREF_INCLUSION_OF_EPISODES_OF_SERIES_KEY = "includesEpisodesOfSeries_";

    private int scheduleMode;

    public static Intent newIntent(Context context, int scheduleMode) {
        Intent intent = new Intent(context, MyScheduleActivity.class);

        intent.putExtra(Extra.SCHEDULE_MODE, scheduleMode);

        return intent;
    }

    //TODO Extract class SchedulePreferences----------------------------------------------------------------------------

    public static int sortMode(Context context, int scheduleMode) {
        return getIntPreference(context, scheduleMode, PREF_SORT_MODE_KEY, SortMode.OLDEST_FIRST);
    }

    public static boolean inclusionOfSpecialEpisodes(Context context, int scheduleMode) {
        return getBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_SPECIAL_EPISODES_KEY, false);
    }

    public static boolean inclusionOfSeenEpisodes(Context context, int scheduleMode) {
        return getBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_SEEN_EPISODES_KEY, false);
    }

    public static boolean inclusionOfEpisodesOfSeries(Context context, int scheduleMode, int seriesId) {
        return getBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_EPISODES_OF_SERIES_KEY + seriesId, true);
    }

    public static boolean saveSortMode(Context context, int scheduleMode, int sortMode) {
        return saveIntPreference(context, scheduleMode, PREF_SORT_MODE_KEY, sortMode);
    }

    public static boolean saveInclusionOfSpecialEpisodes(Context context, int scheduleMode, boolean includingSpecialEpisodes) {
        return saveBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_SPECIAL_EPISODES_KEY, includingSpecialEpisodes);
    }

    public static boolean saveInclusionOfSeenEpisodes(Context context, int scheduleMode, boolean includingSeenEpisodes) {
        return saveBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_SEEN_EPISODES_KEY, includingSeenEpisodes);
    }

    public static boolean saveInclusionOfEpisodesOfSeries(Context context, int scheduleMode, int seriesId, boolean includingEpisodesOfSeries) {
        return saveBooleanPreference(context, scheduleMode, PREF_INCLUSION_OF_EPISODES_OF_SERIES_KEY + seriesId, includingEpisodesOfSeries);
    }

    private static int getIntPreference(Context context, int scheduleMode, String key, int defaultValue) {
        return getPreferences(context)
            .getInt(key + scheduleMode, defaultValue);
    }

    private static boolean getBooleanPreference(Context context, int scheduleMode, String key, boolean defaultValue) {
        return getPreferences(context)
            .getBoolean(key + scheduleMode, defaultValue);
    }

    private static boolean saveIntPreference(Context context, int scheduleMode, String key, int value) {
        return getPreferences(context)
            .edit()
            .putInt(key + scheduleMode, value)
            .commit();
    }

    private static boolean saveBooleanPreference(Context context, int scheduleMode, String key, boolean value) {
        return getPreferences(context)
            .edit()
            .putBoolean(key + scheduleMode, value)
            .commit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myschedule);
        this.setUpScheduleModeFromExtras(savedInstanceState);
        this.setUpActionBar();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SCHEDULE_MODE, this.getSupportActionBar().getSelectedNavigationIndex());
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

    private void setUpScheduleModeFromExtras(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.scheduleMode = savedInstanceState.getInt(Extra.SCHEDULE_MODE);
        } else {
            this.scheduleMode = this.getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
        }
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

        actionBar.setSelectedNavigationItem(this.scheduleMode);
    }

    private ActionBar.Tab newTab(int tabNameResource) {
        return this.getSupportActionBar().newTab().setText(tabNameResource);
    }
}
