/*
 *   OutOfContextEpisodesActivity.java
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

package br.edu.ufcg.aweseries.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.EpisodeListener;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Objects;

/**
 * An abstract activity for showing a list of episodes out of their series context, as happens
 * when we come through Series->Season->Episodes.
 */
public abstract class OutOfContextEpisodesActivity extends ListActivity {

    protected abstract List<Episode> episodes();

    protected abstract Comparator<Episode> episodesComparator();

    private static final int LIST_VIEW_RESOURCE_ID = R.layout.list_without_toolbar;

    //TODO This is not the best place for this constant
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(LIST_VIEW_RESOURCE_ID);

        this.setUpContentView();
        this.setUpListAdapter();

        this.setUpEpisodeItemClickListener();
    }

    private void setUpContentView() {
        final TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText(this.getString(R.string.no_episodes));
    }

    private void setUpListAdapter() {
        final EpisodeItemViewAdapter dataAdapter = new EpisodeItemViewAdapter(this, this.episodes());
        dataAdapter.sort(this.episodesComparator());
        this.setListAdapter(dataAdapter);
    }

    private void setUpEpisodeItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), EpisodeDetailsActivity.class);
                final Episode episode = (Episode) parent.getItemAtPosition(position);
                intent.putExtra("series id", String.valueOf(episode.seriesId()));
                intent.putExtra("season number", episode.seasonNumber());
                intent.putExtra("episode number", episode.number());
                OutOfContextEpisodesActivity.this.startActivity(intent);
            }
        });
    }

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements EpisodeListener {

        private static final int EPISODE_ITEM_RESOURCE_ID = R.layout.episode_alone_list_item;

        private final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

        public EpisodeItemViewAdapter(Context context, List<Episode> objects) {
            super(context, EPISODE_ITEM_RESOURCE_ID, objects);

            for (Episode e : objects) {
                e.register(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View itemView = this.itemViewFrom(convertView);

            // load episode data
            final Episode episode = this.getItem(position);
            final Series series = this.SERIES_PROVIDER.getSeries(String.valueOf(episode.seriesId()));
            final Season season = series.seasons().season(episode.seasonNumber());

            this.showData(episode, season, series, itemView);
            this.setUpSeenEpisodeCheckBoxListener(episode, itemView);

            return itemView;
        }

        private View itemViewFrom(View convertView) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) OutOfContextEpisodesActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(EPISODE_ITEM_RESOURCE_ID, null);
            }

            return itemView;
        }

        private void showData(Episode episode, Season season, Series series, View itemView) {
            final TextView nameTextView = (TextView) itemView
                    .findViewById(R.id.episodeNameTextView);
            final TextView seriesTextView = (TextView) itemView
                    .findViewById(R.id.episodeSeriesTextView);
            final TextView seasonEpisodeTextView = (TextView) itemView
                    .findViewById(R.id.episodeSeasonEpisodeTextView);
            final TextView dateTextView = (TextView) itemView
                    .findViewById(R.id.episodeDateTextView);
            final CheckBox isViewedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.episodeIsViewedCheckBox);

            nameTextView.setText(Objects.nullSafe(
                    episode.name(),
                    this.getContext().getResources().getString(R.string.unnamed_episode)));
            seriesTextView.setText(series.name());
            seasonEpisodeTextView
                    .setText(String.format(OutOfContextEpisodesActivity.this
                            .getString(R.string.season_and_episode_format), season.number(),
                            episode.number()));
            dateTextView.setText(Dates.toString(episode.airdate(), FORMAT, ""));
            isViewedCheckBox.setChecked(episode.wasSeen());
        }

        private void setUpSeenEpisodeCheckBoxListener(final Episode episode, View itemView) {
            final CheckBox isViewedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.episodeIsViewedCheckBox);

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        EpisodeItemViewAdapter.this.SERIES_PROVIDER.markEpisodeAsSeen(episode);
                    } else {
                        EpisodeItemViewAdapter.this.SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                    }
                }
            });
        }

        @Override
        public void onMarkedAsSeen(Episode e) {
            e.deregister(this);
            this.remove(e);
        }

        @Override
        public void onMarkedAsNotSeen(Episode e) {
            //All episodes here are already marked as not seen
        }

        @Override
        public void onMerged(Episode e) {
            this.notifyDataSetChanged();
        }
    }
}
