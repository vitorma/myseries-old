/*
 *   SeriesDetailsActivity.java
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.DomainEntityListener;
import br.edu.ufcg.aweseries.model.Series;

/**
 * Displays a series short review.
 */
public class SeriesDetailsActivity extends Activity implements DomainEntityListener<Series> {
    private String seriesId;
    private boolean loaded = false;
    private ProgressDialog dialog;
    protected TextView seriesOverview;
    private TextView seriesName;
    private TextView seriesStatus;
    private TextView seriesAirTime;
    private TextView seriesAirDays;
    private TextView seriesActors;
    private TextView seriesFirsAirDay;
    private TextView seriesRuntime;
    private TextView seriesGenre;
    private TextView seriesNetwork;
    private Button seasonsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_view);
        
        // Show the contents we already know.
        this.seriesName = (TextView) findViewById(R.id.seriesNameTextView);
        this.seriesName.setText(R.string.unknownSeries);
        this.seriesOverview = (TextView) findViewById(R.id.seriesOverviewTextView);
        this.seriesStatus = (TextView) findViewById(R.id.statusTextView);
        this.seriesAirTime = (TextView) findViewById(R.id.airTimeTextView);
        this.seriesAirDays = (TextView) findViewById(R.id.airDaysTextView);
        this.seriesActors = (TextView) findViewById(R.id.actorsTextView);
        this.seriesFirsAirDay = (TextView) findViewById(R.id.firstAiredTextView);
        this.seriesNetwork = (TextView) findViewById(R.id.networkTextView);
        this.seriesGenre = (TextView) findViewById(R.id.genreTextView);
        this.seriesRuntime = (TextView) findViewById(R.id.runtimeTextView);
        this.seasonsButton = (Button) findViewById(R.id.seasonsButton);

        populateView();
        setupSeasonsButtonListener();
    }

    private void setupSeasonsButtonListener() {
        this.seasonsButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SeasonListActivity.class);
                intent.putExtra("series id", SeriesDetailsActivity.this.seriesId);
                
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    TextView tv =
                            (TextView) SeriesDetailsActivity.this
                                    .findViewById(R.id.listingTitleTextView);
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
                @Override
                public void run() {
                    mHandler.post(mPopulateView);
                }
            };

            t.start();

            loaded = true;
        }
    }
    
    @Override
    public void onBackPressed() {
        Series series = seriesProvider().getSeries(seriesId);
        series.removeListener(this);
        super.onBackPressed();
        
    }

    /**
     * Populates seriesNameTextView and seriesReviewTextField with the data
     * retrieved from TheTVDB database.
     */
    private void populateView() {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            seriesId = extras.getString("series id");
            seriesName.setText(extras.getString("series name"));
        }
    }

    /**
     * Loads the description from database.
     */
    private void downloadDescription() {
        try {
            Series series = seriesProvider().getSeries(seriesId);
            series.addListener(this);
            
            this.seriesName.setText(series.getName());
            this.seriesOverview.setText(series.getOverview());
            this.seriesStatus.setText(series.getStatus());
            this.seriesAirTime.setText(series.getAirsTime());
            this.seriesAirDays.setText(series.getAirsDay());
            this.seriesActors.setText(series.getActors());
            this.seriesFirsAirDay.setText(series.getFirstAired());
            this.seriesNetwork.setText(series.getNetwork());
            this.seriesGenre.setText(series.getGenres());
            this.seriesRuntime.setText(series.getRuntime() + " minutes");
            
            Bitmap bmp = seriesProvider().getPosterOf(series);
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
                        .show(SeriesDetailsActivity.this, "", "Downloading...", true);

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

    @Override
    public void onUpdate(Series entity) {
        this.downloadDescription();
    }
}
