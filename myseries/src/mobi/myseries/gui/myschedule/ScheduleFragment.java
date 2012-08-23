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
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SortingDialogBuilder;
import mobi.myseries.gui.shared.SortingDialogBuilder.OptionListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpContentView();
        this.setUpListAdapter();
        this.setUpItemClickListener();
        this.setHasOptionsMenu(true);
    }

    private void setUpContentView() {
        TextView empty = (TextView) this.getActivity().findViewById(android.R.id.empty);
        empty.setText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpListAdapter() {
        ScheduleAdapter adapter = new ScheduleAdapter(this.getActivity(), this.scheduleMode);

        this.setListAdapter(adapter);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortDialog() {
        final Context context = this.getActivity();
        final int scheduleMode = this.scheduleMode;
        final int sortMode = MyScheduleActivity.sortModeBy(context, scheduleMode);
        final ScheduleAdapter adapter = (ScheduleAdapter) this.getListAdapter();

        new SortingDialogBuilder(context)
            .setCategoryTitle(R.string.episodes)
            .setDefaultSortMode(sortMode)
            .setNewestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    MyScheduleActivity.saveSortMode(context, scheduleMode, SortMode.NEWEST_FIRST);
                    adapter.sort(EpisodeComparator.byNewestFirst());
                }
            })
            .setOldestFirstOptionListener(new OptionListener() {
                @Override
                public void onClick() {
                    MyScheduleActivity.saveSortMode(context, scheduleMode, SortMode.OLDEST_FIRST);
                    adapter.sort(EpisodeComparator.byOldestFirst());
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

    public static class TodayFragment extends ScheduleFragment {
        public TodayFragment() {
            super(ScheduleMode.TODAY);
        }
    }

    public static class UpcomingFragment extends ScheduleFragment {
        public UpcomingFragment() {
            super(ScheduleMode.UPCOMING);
        }
    }
}
