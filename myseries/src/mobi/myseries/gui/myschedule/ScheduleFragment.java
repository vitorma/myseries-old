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
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.Extra;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ScheduleFragment extends SherlockListFragment implements ScheduleAdapter.Listener {
    private int scheduleMode;
    private ScheduleAdapter adapter;

    public static ScheduleFragment newInstance(int scheduleMode) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        ScheduleFragment instance = new ScheduleFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.scheduleMode = this.getArguments().getInt(Extra.SCHEDULE_MODE);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpPadding();
        this.setUpScrollBarStyle();
        this.setUpSelector();
        this.setUpEmptyText();
        this.setUpItemClickListener();
        this.setUpAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.adapter.register(this);

        if (this.adapter.isLoading()) {
            this.onStartLoading();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this);
    }

    @Override
    public void onStartLoading() {
        this.setListShown(false);
    }

    @Override
    public void onFinishLoading() {
        this.setListShown(true);
    }

    private void setUpPadding() {
        int padding = this.getActivity().getResources().getDimensionPixelSize(R.dimen.gap_large);
        this.getListView().setPadding(padding, 0, padding, 0);
    }

    private void setUpScrollBarStyle() {
        this.getListView().setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);
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

    private void setUpAdapter() {
        try {
            ScheduleAdapter.Holder adapterHolder = (ScheduleAdapter.Holder) this.getActivity();
            this.adapter = adapterHolder.adapterForMode(this.scheduleMode);
            this.setListAdapter(this.adapter);
        } catch (ClassCastException e) {
            throw new RuntimeException("Activity should implement ScheduleAdapterHolder");
        }
    }
}
