package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.Series;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Displays a series short review.
 */
public class SeriesView extends Activity {
    private TheTVDB db;
    private int seriesId;
    private Intent intent;
    private boolean loaded = false;
    private ProgressDialog dialog;
    protected TextView seriesReview;
    private TextView seriesName;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_view);
        this.intent = getIntent();

        // Show the contents we already know.
        this.seriesName = (TextView) findViewById(R.id.seriesNameTextView);
        this.seriesName.setText(R.string.unknownSeries);
        this.seriesReview = (TextView) findViewById(R.id.seriesReviewTextView);
        this.seriesReview.setText("Loading...");

        populateView();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
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
        Bundle extras = this.intent.getExtras();

        if (extras != null) {
            db = new TheTVDB(extras.getString("api key"));
            seriesId = extras.getInt("series id");
            seriesName.setText(extras.getString("series name"));
        }

    }

    /**
     * Loads the description from database.
     */
    private void downloadDescription() {
        try {
            Series series = db.getSeries(seriesId);
            this.seriesReview.setText(SeriesView.formatReview(series));
        } catch (Exception e) {
            this.seriesReview.setText(R.string.reviewNotAvailable);
        }
    }

    /**
     * Returns a string containing a short review of the series.
     * 
     * @param series The series object retrieve review from.
     * @return A String containing the review.
     */
    private static String formatReview(Series series) {
        StringBuilder builder = new StringBuilder();

        String genres =
                series.getGenre().substring(1, series.getGenre().length() - 1)
                        .replace("\\|", "").replaceAll("\\|", ", ");

        String actors =
                series.getActors()
                        .substring(1, series.getActors().length() - 1)
                        .replace("\\|", "").replaceAll("\\|", ", ");

        builder.append(genres).append(" starring ");
        builder.append(actors).append(". ");
        builder.append("Airs every ").append(series.getAirsDay());
        builder.append(" at ").append(series.getAirsTime());
        builder.append(" on ").append(series.getNetwork());

        return builder.toString();
    }

    /**
     * Shows progress dialog.
     */
    private void showProgressDialog() {
        this.dialog =
                ProgressDialog
                        .show(SeriesView.this, "", "Downloading...", true);

    }

    /**
     * Destroys progress dialog.
     */
    private void dismissProgressDialog() {
        this.dialog.dismiss();
    }

}
