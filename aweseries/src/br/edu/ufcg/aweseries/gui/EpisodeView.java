package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
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

    private boolean loaded = false;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.episode_view);

        this.episodeName = (TextView) findViewById(R.id.episodeNameTextView);
        this.episodeName.setText(R.string.unnamedEpisode);
        this.episodeFirstAired = (TextView) findViewById(R.id.episodeFirstAiredTextView);
        this.episodeOverview = (TextView) findViewById(R.id.episodeOverviewTextView);
        this.episodeDirector = (TextView) findViewById(R.id.episodeDirectorTextView);
        this.episodeWriter = (TextView) findViewById(R.id.episodeWriterTextView);
        this.episodeGuestStars = (TextView) findViewById(R.id.episodeGuestStarsTextView);

        populateView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!loaded && hasFocus) {
            showProgressDialog();

            final Handler mHandler = new Handler();

            final Runnable mPopulateView = new Runnable() {
                @Override
                public void run() {
                    downloadDescription();
                    dismissProgressDialog();
                }

            };

            Thread t = new Thread() {
                @Override
                public void run() {
                    mHandler.post(mPopulateView);
                }
            };

            t.start();

            loaded = true;
        }
    }

    /**
     * Populates seriesNameTextView and seriesReviewTextField with the data
     * retrieved from TheTVDB database.
     */
    private void populateView() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.episodeId = extras.getString("episode id");
            this.episodeName.setText(extras.getString("episode name"));
        }
    }

    /**
     * Loads the description from database.
     */
    private void downloadDescription() {
            Episode episode = seriesProvider().getEpisode(this.episodeId);
            this.episodeName.setText(episode.getName());
            this.episodeOverview.setText(episode.getOverview());
            this.episodeFirstAired.setText(episode.getFirstAired());
            this.episodeOverview.setText(episode.getOverview());
            this.episodeDirector.setText(episode.getDirector());
            this.episodeWriter.setText(episode.getWriter());
            this.episodeGuestStars.setText(episode.getGuestStars());
    }

    /**
     * Shows progress dialog.
     */
    private void showProgressDialog() {
        this.dialog =
                ProgressDialog
                        .show(EpisodeView.this, "", "Downloading...", true);

    }

    /**
     * Destroys progress dialog.
     */
    private void dismissProgressDialog() {
        this.dialog.dismiss();
    }

    /**
     * @return the app's series provider
     */
    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }
}
