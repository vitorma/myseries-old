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

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.SortMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OptionListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public abstract class ScheduleFragment extends SherlockListFragment {
    private int scheduleMode;

    public ScheduleFragment(int scheduleMode) {
        this.scheduleMode = scheduleMode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpPadding();
        this.setUpEmptyText();
        this.setUpListAdapter();
        this.setUpItemClickListener();
        this.setHasOptionsMenu(true);
    }

    private void setUpPadding() {
        int padding = this.getActivity().getResources().getDimensionPixelSize(R.dimen.gap_large);
        this.getListView().setPadding(padding, 0, padding, 0);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpListAdapter() {
        this.setListAdapter(new ScheduleAdapter(this.getActivity(), this.scheduleMode));
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

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        boolean showSpecialEpisodes = MyScheduleActivity.inclusionOfSpecialEpisodes(this.getActivity(), this.scheduleMode);
        boolean showSeenEpisodes = MyScheduleActivity.inclusionOfSeenEpisodes(this.getActivity(), this.scheduleMode);

        if (showSpecialEpisodes) {
            menu.findItem(R.id.hideShowSpecialEpisodes).setTitle(R.string.hideSpecialEpisodes);
        } else {
            menu.findItem(R.id.hideShowSpecialEpisodes).setTitle(R.string.showSpecialEpisodes);
        }

        if (showSeenEpisodes) {
            menu.findItem(R.id.hideShowSeenEpisodes).setTitle(R.string.hideSeenEpisodes);
        } else {
            menu.findItem(R.id.hideShowSeenEpisodes).setTitle(R.string.showSeenEpisodes);
        }
    }
 
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.myschedule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sorting:
                this.showSortDialog();
                return true;
            case R.id.hideShowSpecialEpisodes:
                boolean showSpecialEpisodes = MyScheduleActivity.inclusionOfSpecialEpisodes(this.getActivity(), this.scheduleMode);
                MyScheduleActivity.saveInclusionOfSpecialEpisodes(this.getActivity(), this.scheduleMode, !showSpecialEpisodes);
                ((ScheduleAdapter) this.getListAdapter()).reload();
                return true;
            case R.id.hideShowSeenEpisodes:
                boolean showSeenEpisodes = MyScheduleActivity.inclusionOfSeenEpisodes(this.getActivity(), this.scheduleMode);
                MyScheduleActivity.saveInclusionOfSeenEpisodes(this.getActivity(), this.scheduleMode, !showSeenEpisodes);
                ((ScheduleAdapter) this.getListAdapter()).reload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortDialog() {
        final Context context = this.getActivity();
        final int scheduleMode = this.scheduleMode;
        final int sortMode = MyScheduleActivity.sortMode(context, scheduleMode);
        final ScheduleAdapter adapter = (ScheduleAdapter) this.getListAdapter();

        new SortingDialogBuilder(context)
            .setCategoryTitle(R.string.episodes)
            .setDefaultSortMode(sortMode)
            .setNewestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    MyScheduleActivity.saveSortMode(context, scheduleMode, SortMode.NEWEST_FIRST);
                    adapter.reload();
                }
            })
            .setOldestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    MyScheduleActivity.saveSortMode(context, scheduleMode, SortMode.OLDEST_FIRST);
                    adapter.reload();
                }
            })
            .build()
            .show();
    }

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
}
