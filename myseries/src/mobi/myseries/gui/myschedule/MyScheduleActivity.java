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

import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.preferences.SchedulePreferences;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OptionListener;
import mobi.myseries.gui.shared.TabPagerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MyScheduleActivity extends SherlockFragmentActivity implements ScheduleAdapter.Holder {
    private static final String TAG = MyScheduleActivity.class.getName();

    private State state;

    public static Intent newIntent(Context context, int scheduleMode) {
        return new Intent(context, MyScheduleActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.myschedule);
        this.setUpState();
        this.setUpActionBar();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.state.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.state.onStop();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
       return this.state;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getSupportMenuInflater().inflate(R.menu.myschedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean isShowingSpecialEpisodes = this.preferencesForCurrentMode().showSpecialEpisodes();
        boolean isShowingSeenEpisodes = this.preferencesForCurrentMode().showSeenEpisodes();

        MenuItem hideShowSpecialEpisodes = menu.findItem(R.id.hideShowSpecialEpisodes);
        MenuItem hideShowSeenEpisodes = menu.findItem(R.id.hideShowSeenEpisodes);

        hideShowSpecialEpisodes.setTitle(isShowingSpecialEpisodes ? R.string.hideSpecialEpisodes : R.string.showSpecialEpisodes);
        hideShowSeenEpisodes.setTitle(isShowingSeenEpisodes ? R.string.hideSeenEpisodes : R.string.showSeenEpisodes);

        hideShowSeenEpisodes.setVisible(this.state.mode == ScheduleMode.NEXT ? false : true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.sorting:
                this.showSortingDialog();
                return true;
            case R.id.hideShowSpecialEpisodes:
                this.hideOrShowSpecialEpisodes();
                return true;
            case R.id.hideShowSeenEpisodes:
                this.hideOrShowSeenEpisodes();
                return true;
            case R.id.filterSeries:
                this.showFilterDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public ScheduleAdapter adapterForMode(int scheduleMode) {
        switch (scheduleMode) {
            case ScheduleMode.RECENT:
                return this.state.adapterForModeRecent;
            case ScheduleMode.UPCOMING:
                return this.state.adapterForModeUpcoming;
            case ScheduleMode.NEXT:
                return this.state.adapterForModeNext;
            default:
                return null;
        }
    }

    private void setUpState() {
        Object retainedState = this.getLastCustomNonConfigurationInstance();

        if (retainedState != null) {
            this.state = (State) retainedState;
        } else {
            this.state = new State();
            this.state.messageLauncher = new MessageLauncher(this);
            this.state.mode = this.getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
            this.state.adapterForModeRecent = this.newAdapterForMode(ScheduleMode.RECENT);
            this.state.adapterForModeUpcoming = this.newAdapterForMode(ScheduleMode.UPCOMING);
            this.state.adapterForModeNext = this.newAdapterForMode(ScheduleMode.NEXT);
        }
    }

    private ScheduleAdapter newAdapterForMode(int scheduleMode) {
        return new ScheduleAdapter(scheduleMode, SchedulePreferences.from(TAG + scheduleMode));
    }

    private void setUpActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.my_schedule);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        new TabPagerAdapter(this)
            .addTab(R.string.recent, ScheduleFragment.newInstance(ScheduleMode.RECENT))
            .addTab(R.string.next, ScheduleFragment.newInstance(ScheduleMode.NEXT))
            .addTab(R.string.upcoming, ScheduleFragment.newInstance(ScheduleMode.UPCOMING))
            .register(this.state);

        actionBar.setSelectedNavigationItem(this.state.mode);
    }

    private void hideOrShowSpecialEpisodes() {
        boolean isShowingSpecialEpisodes = this.preferencesForCurrentMode().showSpecialEpisodes();

        this.adapterForCurrentMode().hideOrShowSpecialEpisodes(!isShowingSpecialEpisodes);
    }

    private void hideOrShowSeenEpisodes() {
        boolean isShowingSeenEpisodes = this.preferencesForCurrentMode().showSeenEpisodes();

        this.adapterForCurrentMode().hideOrShowSeenEpisodes(!isShowingSeenEpisodes);
    }

    private void showSortingDialog() {
        final SchedulePreferences preferences = this.preferencesForCurrentMode();
        final ScheduleAdapter adapter = this.adapterForCurrentMode();

        this.state.dialog = new SortingDialogBuilder(this)
            .setTitleArgument(R.string.episodes)
            .setDefaultSortMode(preferences.sortMode())
            .setNewestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    adapter.sortBy(SortMode.NEWEST_FIRST);
                }
            })
            .setOldestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    adapter.sortBy(SortMode.OLDEST_FIRST);
                }
            })
            .build();

        this.state.dialog.show();
    }

    private void showFilterDialog() {
        final SchedulePreferences preferences = this.preferencesForCurrentMode();
        final Map<Series, Boolean> filterOptions = preferences.seriesFilterOptions();
        final ScheduleAdapter adapter = this.adapterForCurrentMode();

        this.state.dialog = new SeriesFilterDialogBuilder(this)
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    adapter.hideOrShowSeries(filterOptions);
                }
            })
            .build();

        this.state.dialog.show();
    }

    private ScheduleAdapter adapterForCurrentMode() {
        return this.adapterForMode(this.state.mode);
    }

    private SchedulePreferences preferencesForCurrentMode() {
        return SchedulePreferences.from(TAG + this.state.mode);
    }

    private static class State implements TabPagerAdapter.Listener {
        private int mode;
        private Dialog dialog;
        private boolean isShowingDialog;
        private MessageLauncher messageLauncher;
        private ScheduleAdapter adapterForModeRecent;
        private ScheduleAdapter adapterForModeUpcoming;
        private ScheduleAdapter adapterForModeNext;

        @Override
        public void onSelected(int position) {
            this.mode = position;
        }

        private void onStart() {
            if (this.isShowingDialog) {
                this.dialog.show();
            }

            this.messageLauncher.loadState();
        }

        private void onStop() {
            if (this.dialog != null && this.dialog.isShowing()) {
                this.isShowingDialog = true;
                this.dialog.dismiss();
            } else {
                this.isShowingDialog = false;
            }

            this.messageLauncher.onStop();
        }
    }
}
