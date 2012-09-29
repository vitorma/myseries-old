/*
 *   ScheduleFragment.java
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

import java.util.HashMap;
import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder;
import mobi.myseries.gui.shared.SeriesFilterDialogBuilder.OnFilterListener;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OptionListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public abstract class ScheduleFragment extends SherlockListFragment implements ScheduleAdapter.LoadingListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private int scheduleMode;
    private SchedulePreferences preferences;

    public ScheduleFragment(int scheduleMode) {
        this.scheduleMode = scheduleMode;
        this.preferences = MyScheduleActivity.getSchedulePreferences(this.scheduleMode);
    }

    public int scheduleMode() {
        return this.scheduleMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpPadding();
        this.setUpSelector();
        this.setUpEmptyText();
        this.setUpItemClickListener();
        this.setListAdapter(((MyScheduleActivity) this.getActivity()).getAdapterForScheduleMode(this.scheduleMode));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean isShowingSpecialEpisodes = this.preferences.showSpecialEpisodes();
        boolean isShowingSeenEpisodes = this.preferences.showSeenEpisodes();

        MenuItem hideShowSpecialEpisodes = menu.findItem(R.id.hideShowSpecialEpisodes);
        MenuItem hideShowSeenEpisodes = menu.findItem(R.id.hideShowSeenEpisodes);

        hideShowSpecialEpisodes.setTitle(isShowingSpecialEpisodes ? R.string.hideSpecialEpisodes : R.string.showSpecialEpisodes);
        hideShowSeenEpisodes.setTitle(isShowingSeenEpisodes ? R.string.hideSeenEpisodes : R.string.showSeenEpisodes);

        hideShowSeenEpisodes.setVisible(this.scheduleMode == ScheduleMode.NEXT ? false : true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    private void setUpPadding() {
        int padding = this.getActivity().getResources().getDimensionPixelSize(R.dimen.gap_large);
        this.getListView().setPadding(padding, 0, padding, 0);
    }

    private void setUpSelector() {
        this.getListView().setSelector(R.color.transparent);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = (Episode) parent.getItemAtPosition(position);

                Intent intent = EpisodesActivity.newIntent(
                        view.getContext(), e.seriesId(), e.seasonNumber(), e.number());

                ScheduleFragment.this.startActivity(intent);
            }
        });
    }

    private ScheduleAdapter scheduleAdapter() {
        return (ScheduleAdapter) this.getListAdapter();
    }

    private void hideOrShowSpecialEpisodes() {
        boolean isShowingSpecialEpisodes = this.preferences.showSpecialEpisodes();

        this.preferences.setIfShowSpecialEpisodes(!isShowingSpecialEpisodes);
        this.scheduleAdapter().hideOrShowSpecialEpisodes(!isShowingSpecialEpisodes);
    }

    private void hideOrShowSeenEpisodes() {
        boolean isShowingSeenEpisodes = this.preferences.showSeenEpisodes();

        this.preferences.setIfShowSeenEpisodes(!isShowingSeenEpisodes);
        this.scheduleAdapter().hideOrShowSeenEpisodes(!isShowingSeenEpisodes);
    }

    private void showSortingDialog() {
        final Context context = this.getActivity();
        final SchedulePreferences preferences = this.preferences;
        final ScheduleAdapter adapter = this.scheduleAdapter();

        new SortingDialogBuilder(context)
            .setTitleArgument(R.string.episodes)
            .setDefaultSortMode(preferences.sortMode())
            .setNewestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    preferences.setSortMode(SortMode.NEWEST_FIRST);
                    adapter.sortBy(SortMode.NEWEST_FIRST);
                }
            })
            .setOldestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    preferences.setSortMode(SortMode.OLDEST_FIRST);
                    adapter.sortBy(SortMode.OLDEST_FIRST);
                }
            })
            .build()
            .show();
    }

    private void showFilterDialog() {
        final Context context = this.getActivity();
        final SchedulePreferences preferences = this.preferences;
        final Map<Series, Boolean> filterOptions = new HashMap<Series, Boolean>();
        final ScheduleAdapter adapter = this.scheduleAdapter();

        for (Series s : SERIES_PROVIDER.followedSeries()) {
            filterOptions.put(s, preferences.showSeries(s.id()));
        }

        new SeriesFilterDialogBuilder(context)
            .setDefaultFilterOptions(filterOptions)
            .setOnFilterListener(new OnFilterListener() {
                @Override
                public void onFilter() {
                    for (Series s : filterOptions.keySet()) {
                        preferences.setIfShowSeries(s.id(), filterOptions.get(s));
                    }
                    adapter.hideOrShowSeries(filterOptions);
                }
            })
            .build()
            .show();
    }

    /* Concrete children */

    public static class RecentFragment extends ScheduleFragment {
        public RecentFragment() {
            super(ScheduleMode.RECENT);
        }
    }

    public static class NextFragment extends ScheduleFragment {
        public NextFragment() {
            super(ScheduleMode.NEXT);
        }
    }

    public static class UpcomingFragment extends ScheduleFragment {
        public UpcomingFragment() {
            super(ScheduleMode.UPCOMING);
        }
    }

    @Override
    public void onStartLoading() {
        this.setListShown(false);
    }

    @Override
    public void onFinishLoading() {
        this.setListShown(true);
    }

    private void setListAdapter(ScheduleAdapter adapter) {
        super.setListAdapter(adapter);

        adapter.register(this);

        if (adapter.isLoading()) {
            this.onStartLoading();
        }
    }
}
