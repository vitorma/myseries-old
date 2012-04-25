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

package mobi.myseries.gui;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.EpisodeImageDownloadListener;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class EpisodeDetailsActivity extends Activity implements EpisodeImageDownloadListener {
    private static final ImageProvider imageProvider = App.environment().imageProvider();
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();

    private Episode episode;
    private TextView episodeDirector;
    private TextView episodeFirstAired;
    private TextView episodeGuestStars;
    private TextView episodeName;
    private TextView episodeOverview;
    private TextView episodeWriter;
    private Bitmap image;
    private ImageView imageView;
    private CheckBox isViewed;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageProvider.register(this);
        this.setContentView(R.layout.episode_view);
        this.populateView();
        this.setUpSeenEpisodeCheckBox();
    }
    
    //Private-----------------------------------------------------------------------------------------------------------

    private void loadEpisode() {
        final Bundle extras = this.getIntent().getExtras();

        int seriesId = extras.getInt("series id");
        int seasonNumber = extras.getInt("season number");
        int episodeNumber = extras.getInt("episode number");

        this.episode = seriesProvider.getSeries(seriesId).seasons().season(seasonNumber)
                .episode(episodeNumber);
    }

    private void loadEpisodeImage() {
        this.image = imageProvider.getImageOf(episode);

        this.imageView.setImageBitmap(this.image);

        if (this.image != null) {
            this.setupForLoadedImage();
        }

        else
            this.setupForUnavailableImage();
    }

    private void setupForLoadedImage() {
        this.progressBar.setVisibility(View.GONE);
        this.imageView.setVisibility(View.VISIBLE);
    }

    private void setupForLoadingImage() {
        this.progressBar.setVisibility(View.VISIBLE);
        this.imageView.setVisibility(View.GONE);
    }

    private void setupForUnavailableImage() {
        this.progressBar.setVisibility(View.GONE);
        this.imageView.setVisibility(View.GONE);
    }

    private void populateView() {

        this.setUpLocalReferencesToViewFields();
        this.loadEpisode();

        this.episodeName.setText(Objects.nullSafe(this.episode.name(), this.getResources()
                .getString(R.string.unnamed_episode)));
        this.episodeFirstAired.setText(Dates.toString(this.episode.airDate(), App.environment()
                .localization().dateFormat(), ""));
        this.episodeDirector.setText(this.episode.directors());
        this.episodeWriter.setText(this.episode.writers());
        this.episodeGuestStars.setText(this.episode.guestStars());
        this.episodeOverview.setText(this.episode.overview());
        this.isViewed.setChecked(this.episode.wasSeen());

        this.loadEpisodeImage();

        if (this.image == null) {
            imageProvider.downloadImageOf(this.episode);
        }
    }

    private void setUpLocalReferencesToViewFields() {
        this.episodeName = (TextView) this.findViewById(R.id.episodeNameTextView);
        this.episodeFirstAired = (TextView) this.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) this.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) this.findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) this.findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) this.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) this.findViewById(R.id.isEpisodeViewedCheckBox);
        this.imageView = (ImageView) this.findViewById(R.id.imageView);
        this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
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

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    public void onConnectionFailureWhileDownloadingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForUnavailableImage();
        }
    }

    @Override
    public void onDownloadImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.loadEpisodeImage();
            this.setupForLoadedImage();
        }
    }

    @Override
    public void onFailureWhileSavingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForUnavailableImage();
        }
    }

    @Override
    public void onStartDownloadingImageOf(Episode episode) {
        if (episode.equals(this.episode)) {
            this.setupForLoadingImage();
        }
    }

}
