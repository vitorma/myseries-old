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

public class EpisodeView extends Activity {
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
        this.setCheckBoxListener();
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void setFields() {
        this.episodeName = (TextView) this.findViewById(R.id.episodeNameTextView);
        this.episodeFirstAired = (TextView) this.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) this.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) this.findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) this.findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) this.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) this.findViewById(R.id.isEpisodeViewedCheckBox);
    }

    private void getExtras() {
        final Bundle extras = this.getIntent().getExtras();
        this.episode = seriesProvider.getEpisode(extras.getString("episode id"));
    }

    private void populateView() {
        this.setFields();
        this.getExtras();

        this.episodeName.setText(this.episode.getName());
        this.episodeFirstAired.setText(this.episode.getFirstAired());
        this.episodeDirector.setText(this.episode.getDirector());
        this.episodeWriter.setText(this.episode.getWriter());
        this.episodeGuestStars.setText(this.episode.getGuestStars());
        this.episodeOverview.setText(this.episode.getOverview());
        this.isViewed.setChecked(this.episode.isViewed());
    }

    private void setCheckBoxListener() {
        this.isViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EpisodeView.this.isViewed.isChecked()) {
                    seriesProvider.markEpisodeAsViewed(EpisodeView.this.episode);
                } else {
                    seriesProvider.markEpisodeAsNotViewed(EpisodeView.this.episode);
                }
            }
        });
    }
}
