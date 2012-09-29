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
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.myschedule.ScheduleFragment.NextFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.RecentFragment;
import mobi.myseries.gui.myschedule.ScheduleFragment.UpcomingFragment;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.gui.shared.TabsAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private static final String PREFS_NAME = "mobi.myseries.gui.schedule.SchedulePreferences";
    private static final String SORT_MODE_KEY = "sortMode_";
    private static final String SHOW_SPECIAL_EPISODES_KEY = "showSpecialEpisodes_";
    private static final String SHOW_SEEN_EPISODES_KEY = "showSeenEpisodes_";
    private static final String SHOW_SERIES_KEY = "showSeries_";

    private int currentMode;
    private AdapterHolder adapterHolder;

    public static Intent newIntent(Context context, int scheduleMode) {
        Intent intent = new Intent(context, MyScheduleActivity.class);

        intent.putExtra(Extra.SCHEDULE_MODE, scheduleMode);

        return intent;
    }

    //TODO (Cleber) Extract class SchedulePreferences-------------------------------------------------------------------

    public static ScheduleSpecification scheduleSpecification(Context context, int scheduleMode) {
        ScheduleSpecification specification = new ScheduleSpecification();

        boolean showSpecialEpisodes = showSpecialEpisodes(context, scheduleMode);
        specification.specifyInclusionOfSpecialEpisodes(showSpecialEpisodes);

        boolean showSeenEpisodes = showSeenEpisodes(context, scheduleMode);
        specification.specifyInclusionOfSeenEpisodes(showSeenEpisodes);

        for (Series s : SERIES_PROVIDER.followedSeries()) {
            boolean showSeries = showSeries(context, scheduleMode, s.id());
            specification.specifyInclusionOf(s, showSeries);
        }

        int sortMode = sortMode(context, scheduleMode);
        specification.specifySortMode(sortMode);

        return specification;
    }

    public static int sortMode(Context context, int scheduleMode) {
        return getIntPreference(context, scheduleMode, SORT_MODE_KEY, SortMode.OLDEST_FIRST);
    }

    public static boolean showSpecialEpisodes(Context context, int scheduleMode) {
        return getBooleanPreference(context, scheduleMode, SHOW_SPECIAL_EPISODES_KEY, false);
    }

    public static boolean showSeenEpisodes(Context context, int scheduleMode) {
        return getBooleanPreference(context, scheduleMode, SHOW_SEEN_EPISODES_KEY, false);
    }

    public static boolean showSeries(Context context, int scheduleMode, int seriesId) {
        return getBooleanPreference(context, scheduleMode, SHOW_SERIES_KEY + seriesId, true);
    }

    public static boolean saveSortMode(Context context, int scheduleMode, int sortMode) {
        return saveIntPreference(context, scheduleMode, SORT_MODE_KEY, sortMode);
    }

    public static boolean setIfShowSpecialEpisodes(Context context, int scheduleMode, boolean show) {
        return saveBooleanPreference(context, scheduleMode, SHOW_SPECIAL_EPISODES_KEY, show);
    }

    public static boolean setIfShowSeenEpisodes(Context context, int scheduleMode, boolean show) {
        return saveBooleanPreference(context, scheduleMode, SHOW_SEEN_EPISODES_KEY, show);
    }

    public static boolean setIfShowSeries(Context context, int scheduleMode, int seriesId, boolean show) {
        return saveBooleanPreference(context, scheduleMode, SHOW_SERIES_KEY + seriesId, show);
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

    /* Interface */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myschedule);
        this.setUpCurrentModeFromExtras(savedInstanceState);
        this.setUpAdapterHolder();
        this.setUpActionBar();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        ScheduleFragment scheduleFragment = (ScheduleFragment) fragment;

        switch (scheduleFragment.scheduleMode()) {
            case ScheduleMode.RECENT:
                scheduleFragment.setListAdapter(this.adapterHolder.adapterForModeRecent);
                break;
            case ScheduleMode.UPCOMING:
                scheduleFragment.setListAdapter(this.adapterHolder.adapterForModeUpcoming);
                break;
            case ScheduleMode.NEXT:
            default:
                scheduleFragment.setListAdapter(this.adapterHolder.adapterForModeNext);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SCHEDULE_MODE, this.getSupportActionBar().getSelectedNavigationIndex());
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
       return this.adapterHolder;
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
                return this.adapterHolder.adapterForModeRecent;
            case ScheduleMode.UPCOMING:
                return this.adapterHolder.adapterForModeUpcoming;
            case ScheduleMode.NEXT:
                return this.adapterHolder.adapterForModeNext;
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

    private void setUpAdapterHolder() {
        Object retainedAdapterHolder = this.getLastCustomNonConfigurationInstance();

        if (retainedAdapterHolder != null) {
            this.adapterHolder = (AdapterHolder) retainedAdapterHolder;
            return;
        }

        this.adapterHolder = new AdapterHolder();

        this.adapterHolder.adapterForModeRecent = this.newAdapterForScheduleMode(ScheduleMode.RECENT);
        this.adapterHolder.adapterForModeUpcoming = this.newAdapterForScheduleMode(ScheduleMode.UPCOMING);
        this.adapterHolder.adapterForModeNext = this.newAdapterForScheduleMode(ScheduleMode.NEXT);
    }

    private ScheduleAdapter newAdapterForScheduleMode(int scheduleMode) {
        ScheduleSpecification specification = scheduleSpecification(this, scheduleMode);

        return new ScheduleAdapter(this, scheduleMode, specification);
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

    private static class AdapterHolder {
        private ScheduleAdapter adapterForModeRecent;
        private ScheduleAdapter adapterForModeUpcoming;
        private ScheduleAdapter adapterForModeNext;
    }
}
