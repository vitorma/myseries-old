package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;

public class EpisodeView extends Activity {
    private String episodeId;
    private TextView episodeName;
    private TextView episodeFirstAired;
    private TextView episodeOverview;
    private TextView episodeDirector;
    private TextView episodeWriter;
    private TextView episodeGuestStars;
    private CheckBox isViewed;
    private boolean loaded = false;
    private ProgressDialog dialog;

    private void setCheckBoxListener() {
        this.isViewed.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton checkButton, boolean checked) {
                if (checked) {
                    EpisodeView.this.seriesProvider().getEpisode(EpisodeView.this.episodeId)
                            .markAsViewed();
                } else {
                    EpisodeView.this.seriesProvider().getEpisode(EpisodeView.this.episodeId)
                            .markAsNotViewed();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.episode_view);
        this.episodeName = (TextView) this.findViewById(R.id.episodeNameTextView);
        this.episodeName.setText(R.string.unnamedEpisode);
        this.episodeFirstAired = (TextView) this.findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) this.findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) this.findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) this.findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) this.findViewById(R.id.episodeGuestStarsTextView);
        this.isViewed = (CheckBox) this.findViewById(R.id.isEpisodeViewedCheckBox);
        this.populateView();
        this.setCheckBoxListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!this.loaded && hasFocus) {
            this.showProgressDialog();
            final Handler mHandler = new Handler();
            final Runnable mPopulateView = new Runnable() {
                @Override
                public void run() {
                    EpisodeView.this.downloadDescription();
                    EpisodeView.this.dismissProgressDialog();
                }
            };
            final Thread t = new Thread() {
                @Override
                public void run() {
                    mHandler.post(mPopulateView);
                }
            };
            t.start();
            this.loaded = true;
        }
    }

    private void populateView() {
        final Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            this.episodeId = extras.getString("episode id");
            this.episodeName.setText(extras.getString("episode name"));
        }
    }

    private void downloadDescription() {
        final Episode episode = this.seriesProvider().getEpisode(this.episodeId);
        this.episodeName.setText(episode.getName());
        this.episodeFirstAired.setText(episode.getFirstAired());
        this.episodeDirector.setText(episode.getDirector());
        this.episodeWriter.setText(episode.getWriter());
        this.episodeGuestStars.setText(episode.getGuestStars());
        this.episodeOverview.setText(episode.getOverview());
        this.isViewed.setChecked(episode.isViewed());

    }

    private void showProgressDialog() {
        this.dialog = ProgressDialog.show(EpisodeView.this, "", "Downloading...", true);
    }

    private void dismissProgressDialog() {
        this.dialog.dismiss();
    }

    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }
}
