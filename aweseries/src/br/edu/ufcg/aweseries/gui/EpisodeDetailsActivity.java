/*
 *   EpisodeDetailsActivity.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.gui;

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

public class EpisodeDetailsActivity extends Activity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();

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

        this.episodeName.setText(this.episode.getName());
        this.episodeFirstAired.setText(this.episode.getFirstAiredAsString());
        this.episodeDirector.setText(this.episode.getDirector());
        this.episodeWriter.setText(this.episode.getWriter());
        this.episodeGuestStars.setText(this.episode.getGuestStars());
        this.episodeOverview.setText(this.episode.getOverview());
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

        this.episode = seriesProvider.getSeries(seriesId).getSeasons().getSeason(seasonNumber).get(episodeNumber);
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
