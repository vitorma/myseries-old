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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.DomainObjectListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;

/**
 * Displays a series short review.
 */
public class SeriesDetailsActivity extends Activity implements DomainObjectListener<Series> {
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
    private TextView nextToAir;
    private TextView nextToSee;
    private TextView nextToAirLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.series_view);

        // Show the contents we already know.
        this.seriesName = (TextView) findViewById(R.id.seriesNameTextView);
        this.seriesName.setText(R.string.unknown_series);
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
        this.nextToAir = (TextView) findViewById(R.id.nextToAirTextView);
        this.nextToAirLabel = (TextView) findViewById(R.id.nextToAirLabel);
        this.nextToSee = (TextView) findViewById(R.id.nextToSeeTextView);

        populateView();
        setupSeasonsButtonListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.series_details_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stopFollowingSeriesMenuItem:
                this.showUnfollowingDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showUnfollowingDialog() {
        final Series series = seriesProvider().getSeries(this.seriesId);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        onBackPressed();
                        seriesProvider().unfollow(series);
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(
                        String.format(getString(R.string.do_you_want_to_stop_following),
                                series.getName()))
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
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
                    TextView tv = (TextView) SeriesDetailsActivity.this
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
            this.seriesRuntime.setText(String.format(
                    this.getString(R.string.runtime_minutes_format), series.getRuntime()));

            if (series.isContinuing()) {
                final Episode nextToAir = series.getSeasons().getNextEpisodeToAir();

                if (nextToAir != null) {
                    this.nextToAir.setText(series.getSeasons().getNextEpisodeToAir().getName());
                } else {
                    this.nextToAir.setText(R.string.up_to_date);

                }
            }

            if (series.isEnded()) {
                this.nextToAirLabel.setText(R.string.last_episode_aired);
                final Episode e = series.getSeasons().getLastAiredEpisode();
                if (e != null) {
                    this.nextToAir.setText(e.toString());
                } else {
                    this.nextToAir.setText(R.string.no_episode_aired);
                }

            }

            final Episode nextToSee = series.getSeasons().getNextEpisodeToSee();
            if (nextToSee != null) {
                this.nextToSee.setText(series.getSeasons().getNextEpisodeToSee().getName());
            } else {
                this.nextToSee.setText(R.string.up_to_date);
            }

            Bitmap bmp = seriesProvider().getPosterOf(series);
            if (bmp != null) {
                // WallpaperManager.
                // View v = this.findViewById(R.layout.series_view);
                // // v.setBackgroundDrawable(BitmapDrawable.);
                ImageView view = (ImageView) this.findViewById(R.id.seriesPosterImageView);
                view.setImageBitmap(bmp);

            }

        } catch (Exception e) {
            this.seriesOverview.setText(R.string.review_not_available);
        }
    }

    /**
     * Shows progress dialog.
     */
    private void showProgressDialog() {
        this.dialog = ProgressDialog.show(SeriesDetailsActivity.this, "",
                this.getString(R.string.loading), true);

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