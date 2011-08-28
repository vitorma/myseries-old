package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.Series;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Displays a series short review.
 */
public class SeriesView extends Activity {
    private TheTVDB db;
    private int seriesId;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_view);

        populateView();
    }

    /**
     * Populates seriesNameTextView and seriesReviewTextField with the data
     * retrieved from TheTVDB database.
     */
    private void populateView() {
        Bundle extras = getIntent().getExtras();
        TextView seriesName = (TextView) findViewById(R.id.seriesNameTextView);

        if (extras != null) {
            db = new TheTVDB(extras.getString("api key"));
            seriesId = extras.getInt("series id");
            seriesName.setText(extras.getString("series name"));
        }

        TextView seriesReview =
                (TextView) findViewById(R.id.seriesReviewTextView);

        try {
            Series series = db.getSeries(seriesId);
            seriesReview.setText(series.toString());
        } catch (Exception e) {
            seriesName.setText(R.string.unknownSeries);
            seriesReview.setText(R.string.reviewNotAvailable);
        }
    }
}
