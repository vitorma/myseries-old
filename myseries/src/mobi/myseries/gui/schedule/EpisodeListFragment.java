/*
 *   EpisodeListFragment.java
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
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.detail.episode.EpisodeDetailsActivity;
import mobi.myseries.shared.Validate;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class EpisodeListFragment extends SherlockListFragment {
    private static final int LAYOUT = R.layout.list;

    private EpisodeListFactory episodeListFactory;

    public EpisodeListFragment(EpisodeListFactory episodeListFactory) {
        Validate.isNonNull(episodeListFactory, "episodeListFactory");
        this.episodeListFactory = episodeListFactory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(LAYOUT, container, false);
    }

    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpContentView();
        this.setUpListAdapter();
        this.setUpItemClickListener();
    }

    private void setUpContentView() {
        TextView empty = (TextView) this.getActivity().findViewById(android.R.id.empty);
        empty.setText(this.getString(R.string.no_episodes));
    }

    private void setUpListAdapter() {
        EpisodeListAdapter dataAdapter = new EpisodeListAdapter(this.getActivity(),
                                                                this.episodeListFactory);
        this.setListAdapter(dataAdapter);
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = (Episode) parent.getItemAtPosition(position);

                Intent intent = EpisodeDetailsActivity.newIntent(
                    view.getContext(), e.seriesId(), e.seasonNumber(), e.number());

                EpisodeListFragment.this.startActivity(intent);
            }
        });
    }
}
