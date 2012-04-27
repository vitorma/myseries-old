/*
 *   EpisodeListActivity.java
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

package mobi.myseries.gui;

import java.util.Comparator;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class EpisodeListActivity extends ListActivity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final Comparator<Episode> EPISODE_COMPARATOR = EpisodeComparator.byNumber();

    private Series series;
    private Season season;
    private CheckBox isSeasonViewed;

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements EpisodeListener {

        public EpisodeItemViewAdapter(Context context, int episodeItemResourceId, List<Episode> objects) {
            super(context, episodeItemResourceId, objects);

            for (Episode e : objects) {
                e.register(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = this.itemViewFrom(convertView);

            Episode episode = this.getItem(position);

            this.showEpisodesDataOn(episode, itemView);
            this.setUpSeenEpisodeCheckBoxFor(episode, itemView);

            return itemView;
        }

        private View itemViewFrom(View convertView) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) EpisodeListActivity.this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.episode_list_item, null);
            }

            return itemView;
        }

        private void showEpisodesDataOn(Episode episode, View itemView) {
            TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
            TextView numberTextView = (TextView) itemView.findViewById(R.id.episodeNumberTextView);
            TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
            CheckBox isViewedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.episodeIsViewedCheckBox);

            nameTextView.setText(Objects.nullSafe(episode.name(), this.getContext().getResources()
                    .getString(R.string.unnamed_episode)));
            numberTextView.setText(String.format(
                    EpisodeListActivity.this.getString(R.string.episode_number_format),
                    episode.number()));
            dateTextView.setText(Dates.toString(episode.airDate(), App.environment().localization()
                    .dateFormat(), ""));
            isViewedCheckBox.setChecked(episode.wasSeen());
        }

        private void setUpSeenEpisodeCheckBoxFor(final Episode episode, View itemView) {
            final CheckBox isViewedCheckBox = (CheckBox) itemView
            .findViewById(R.id.episodeIsViewedCheckBox);

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        seriesProvider.markEpisodeAsSeen(episode);
                    } else {
                        seriesProvider.markEpisodeAsNotSeen(episode);
                    }
                }
            });
        }

        @Override
        public void onMarkAsSeen(Episode e) {
            this.notifyDataSetChanged();

        }

        @Override
        public void onMarkAsNotSeen(Episode e) {
            this.notifyDataSetChanged();
            

        }

        @Override
        public void onMerge(Episode e) {
            this.notifyDataSetChanged();
        };
    }

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_with_checkbox);

        this.populateView();

        this.setUpEpisodeItemClickListener();
        this.setUpSeenSeasonCheckBoxClickListener();
        this.setUpListenerForSeason();
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void populateView() {
        this.loadSeason();

        this.setUpListActivityParameters();

        this.loadSeasonDataOnView();

        this.setUpEpisodeListAdapter();
    }

    private void loadSeason() {
        final Bundle extras = this.getIntent().getExtras();

        this.series = seriesProvider.getSeries(extras.getInt("series id"));
        this.season = this.series.seasons().season(extras.getInt("season number"));
    }

    private void setUpListActivityParameters() {
        TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText(this.series.name());

        TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText(R.string.no_episodes);
    }

    private void loadSeasonDataOnView() {
        this.isSeasonViewed = (CheckBox) this.findViewById(R.id.isSeasonViewedCheckBox);
        this.isSeasonViewed.setChecked(this.season.wasSeen());

        TextView seasonName = (TextView) this.findViewById(R.id.seasonTextView);

        if (this.season.number() == 0) {
            seasonName.setText(this.getString(R.string.special_episodes));
        }

        else {
            seasonName.setText(String.format(this.getString(R.string.season_number_format),
                    this.season.number()));
        }

    }

    private void setUpEpisodeListAdapter() {
        EpisodeItemViewAdapter dataAdapter = new EpisodeItemViewAdapter(this,
                R.layout.episode_list_item, this.season.episodes());
        this.setListAdapter(dataAdapter);
        dataAdapter.sort(EPISODE_COMPARATOR);
    }

    private void setUpEpisodeItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode episode = (Episode) parent.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), EpisodeDetailsActivity.class);
                intent.putExtra("series id", episode.seriesId());
                intent.putExtra("season number", episode.seasonNumber());
                intent.putExtra("episode number", episode.number());

                EpisodeListActivity.this.startActivity(intent);
            }
        });
    }

    private void setUpSeenSeasonCheckBoxClickListener() {
        this.isSeasonViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (EpisodeListActivity.this.isSeasonViewed.isChecked()) {
                    seriesProvider.markSeasonAsSeen(EpisodeListActivity.this.season);
                } else {
                    seriesProvider.markSeasonAsNotSeen(EpisodeListActivity.this.season);
                }
            }
        });
    }

    private void setUpListenerForSeason() {
        this.season.register(new SeasonListener() {

            @Override
            public void onChangeNextEpisodeToSee(Season season) {
                EpisodeListActivity.this.loadSeasonDataOnView();
            }

            @Override
            public void onMarkAsNotSeen(Season season) {
                EpisodeListActivity.this.loadSeasonDataOnView();
            }

            @Override
            public void onMarkAsSeen(Season season) {
                EpisodeListActivity.this.loadSeasonDataOnView();
            }

            @Override
            public void onMerge(Season season) {
                EpisodeListActivity.this.loadSeasonDataOnView();
            }

            @Override
            public void onChangeNumberOfSeenEpisodes(Season season) {
                EpisodeListActivity.this.loadSeasonDataOnView();
            }
        });
    }
}