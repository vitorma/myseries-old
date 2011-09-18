package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.thetvdb.Series;

/**
 * Displays a series short review.
 */
public class SeriesView extends Activity {
    private int seriesId;
    private boolean loaded = false;
    private ProgressDialog dialog;
    protected TextView seriesOverview;
    private TextView seriesName;
    private TextView seriesStatus;
    private TextView seriesAirTime;
    private TextView seriesAirDays;
    private TextView seriesDirectors;
    private TextView seriesActors;
    private TextView seriesFirsAirDay;
    private TextView seriesRuntime;
    private TextView seriesGenre;
    private TextView seriesNetwork;
    private TextSwitcher seasonsTextSwitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_view);
        
        // Show the contents we already know.
        this.seriesName = (TextView) findViewById(R.id.seriesNameTextView);
        this.seriesName.setText(R.string.unknownSeries);
        this.seriesOverview = (TextView) findViewById(R.id.seriesOverviewTextView);
        this.seriesOverview.setText("Loading...");
        this.seriesStatus = (TextView) findViewById(R.id.statusTextView);
        this.seriesAirTime = (TextView) findViewById(R.id.airTimeTextView);
        this.seriesAirDays = (TextView) findViewById(R.id.airDaysTextView);
        this.seriesDirectors = (TextView) findViewById(R.id.directedByTextView);
        this.seriesActors = (TextView) findViewById(R.id.actorsTextView);
        this.seriesFirsAirDay = (TextView) findViewById(R.id.firstAiredTextView);
        this.seriesNetwork = (TextView) findViewById(R.id.networkTextView);
        this.seriesGenre = (TextView) findViewById(R.id.genreTextView);
        this.seriesRuntime = (TextView) findViewById(R.id.runtimeTextView);
        this.seasonsTextSwitcher = (TextSwitcher) findViewById(R.id.seasonsTextSwitcher);

        populateView();
        setupSwitcherListener();
    }

    private void setupSwitcherListener() {
        this.seasonsTextSwitcher.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SeasonsView.class);
                intent.putExtra("series id", SeriesView.this.seriesId);
                
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    TextView tv =
                            (TextView) SeriesView.this
                                    .findViewById(R.id.viewTitleTextView);
                    tv.setText(e.getClass() + " " + e.getMessage());
                }

                
            }
        });

        
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
            seriesId = extras.getInt("series id");
            seriesName.setText(extras.getString("series name"));
        }
    }

    /**
     * Loads the description from database.
     */
    private void downloadDescription() {
        try {
            Series series = seriesProvider().getSeries(seriesId);
            this.seriesName.setText(series.getName());
            this.seriesOverview.setText(series.getOverview());
            this.seriesStatus.setText(series.getStatus());
            this.seriesAirTime.setText(series.getAirsTime());
            this.seriesAirDays.setText(series.getAirsDay());
            this.seriesDirectors.setText(series.getId());
            this.seriesActors.setText(series.getActorsAsString(", "));
            this.seriesFirsAirDay.setText(series.getFirstAired());
            this.seriesNetwork.setText(series.getNetwork());
            this.seriesGenre.setText(series.getGenresAsString(", "));
            this.seriesRuntime.setText(series.getRuntime());
            
            Bitmap bmp = seriesProvider().getSmallPoster(series);
            if (bmp != null) {
                // WallpaperManager.
                // View v = this.findViewById(R.layout.series_view);
                // // v.setBackgroundDrawable(BitmapDrawable.);
                ImageView view =
                        (ImageView) this
                                .findViewById(R.id.seriesPosterImageView);
                view.setImageBitmap(bmp);
                
            }
            
        } catch (Exception e) {
            this.seriesOverview.setText(R.string.reviewNotAvailable);
        }
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

    /**
     * @return the app's series provider
     */
    private SeriesProvider seriesProvider() {
        return Environment.instance().getSeriesProvider();
    }
}
