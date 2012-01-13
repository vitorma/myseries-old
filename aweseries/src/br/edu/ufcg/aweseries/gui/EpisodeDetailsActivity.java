/*
 *   EpisodeDetailsActivity.java
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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Objects;

public class EpisodeDetailsActivity extends Activity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();

    //TODO This is not the best place for this constant
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Episode episode;
    private TextView episodeName;
    private TextView episodeFirstAired;
    private TextView episodeOverview;
    private TextView episodeDirector;
    private TextView episodeWriter;
    private TextView episodeGuestStars;
    private CheckBox isViewed;

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.episode_view);
        this.populateView();
        this.setUpSeenEpisodeCheckBox();
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void populateView() {
        this.setUpLocalReferencesToViewFields();
        this.loadEpisode();

        this.episodeName.setText(Objects.nullSafe(
                this.episode.name(),
                this.getResources().getString(R.string.unnamed_episode)));
        this.episodeFirstAired.setText(Dates.toString(this.episode.airdate(), FORMAT, ""));
        this.episodeDirector.setText(this.episode.directors());
        this.episodeWriter.setText(this.episode.writers());
        this.episodeGuestStars.setText(this.episode.guestStars());
        this.episodeOverview.setText(this.episode.overview());
        this.isViewed.setChecked(this.episode.wasSeen());
    }

    private void setUpLocalReferencesToViewFields() {
        this.episodeName = (TextView) this.findViewById(R.id.episodeNameTextView);
        this.episodeFirstAired = (TextView) this.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) this.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) this.findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) this.findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) this.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) this.findViewById(R.id.isEpisodeViewedCheckBox);
    }

    private void loadEpisode() {
        final Bundle extras = this.getIntent().getExtras();

        String seriesId = extras.getString("series id");
        int seasonNumber = extras.getInt("season number");
        int episodeNumber = extras.getInt("episode number");

        this.episode = seriesProvider.getSeries(seriesId).seasons().getSeason(seasonNumber).get(episodeNumber);
    }

    private void setUpSeenEpisodeCheckBox() {
        this.isViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EpisodeDetailsActivity.this.isViewed.isChecked()) {
                    seriesProvider.markEpisodeAsSeen(EpisodeDetailsActivity.this.episode);
                } else {
                    seriesProvider.markEpisodeAsNotSeen(EpisodeDetailsActivity.this.episode);
                }
            }
        });
    }
}
