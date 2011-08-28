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
        seriesName.setText(R.string.unknownSeries);
        TextView seriesReview =
                (TextView) findViewById(R.id.seriesReviewTextView);

        if (extras != null) {
            db = new TheTVDB(extras.getString("api key"));
            seriesId = extras.getInt("series id");
            seriesName.setText(extras.getString("series name"));
        }


        try {
            Series series = db.getSeries(seriesId);
            seriesReview.setText(this.formatReview(series));
        } catch (Exception e) {
            seriesReview.setText(R.string.reviewNotAvailable);
        }
    }
    
    /**
     * Returns a string containing a short review of the series. 
     * @param series The series object retrieve review from.
     * @return A String containing the review.
     */
    private String formatReview(Series series) {
        StringBuilder builder = new StringBuilder();
        
        String genres = series.getGenre().substring(1, series.getGenre().length()-1)
                .replace("\\|", "").replaceAll("\\|", ", ");
        
        String actors = series.getActors().substring(1, series.getActors().length()-1)
                .replace("\\|", "").replaceAll("\\|", ", ");
        
        builder.append(genres).append(" starring ");
        builder.append(actors).append(". ");
        builder.append("Airs every ").append(series.getAirsDay());
        builder.append(" at ").append(series.getAirsTime());
        builder.append(" on ").append(series.getNetwork());
        
        return builder.toString();
    }
}
