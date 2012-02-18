/*
 *   SeriesDetailsActivity.java
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
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesListener;

/**
 * Displays a series short review.
 */
public class SeriesDetailsActivity extends Activity implements SeriesListener {
    private int seriesId;
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
    private TextView nextToSee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.series_view);

        // Show the contents we already know.
        this.seriesName = (TextView) this.findViewById(R.id.seriesNameTextView);
        this.seriesName.setText(R.string.unknown_series);
        this.seriesOverview = (TextView) this.findViewById(R.id.seriesOverviewTextView);
        this.seriesStatus = (TextView) this.findViewById(R.id.statusTextView);
        this.seriesAirTime = (TextView) this.findViewById(R.id.airTimeTextView);
        this.seriesAirDays = (TextView) this.findViewById(R.id.airDaysTextView);
        this.seriesActors = (TextView) this.findViewById(R.id.actorsTextView);
        this.seriesFirsAirDay = (TextView) this.findViewById(R.id.firstAiredTextView);
        this.seriesNetwork = (TextView) this.findViewById(R.id.networkTextView);
        this.seriesGenre = (TextView) this.findViewById(R.id.genreTextView);
        this.seriesRuntime = (TextView) this.findViewById(R.id.runtimeTextView);
        this.seasonsButton = (Button) this.findViewById(R.id.seasonsButton);
        this.nextToSee = (TextView) this.findViewById(R.id.nextToSeeTextView);

        this.populateView();
        this.setupSeasonsButtonListener();
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
        final Series series = this.seriesProvider().getSeries(this.seriesId);
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SeriesDetailsActivity.this.onBackPressed();
                        SeriesDetailsActivity.this.seriesProvider().unfollow(series);
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
                String.format(this.getString(R.string.do_you_want_to_stop_following),
                        series.name()))
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
                    SeriesDetailsActivity.this.startActivity(intent);
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

        if (!this.loaded && hasFocus) {
            this.showProgressDialog();

            final Handler mHandler = new Handler();

            final Runnable mPopulateView = new Runnable() {
                @Override
                public void run() {
                    SeriesDetailsActivity.this.downloadDescription();
                    SeriesDetailsActivity.this.dismissProgressDialog();
                }

            };

            Thread t = new Thread() {
                @Override
                public void run() {
                    mHandler.post(mPopulateView);
                }
            };

            t.start();

            this.loaded = true;
        }
    }

    @Override
    public void onBackPressed() {
        Series series = this.seriesProvider().getSeries(this.seriesId);
        series.deregister(this);
        super.onBackPressed();

    }

    /**
     * Populates seriesNameTextView and seriesReviewTextField with the data
     * retrieved from TheTVDB database.
     */
    private void populateView() {
        Bundle extras = this.getIntent().getExtras();

        if (extras != null) {
            this.seriesId = extras.getInt("series id");
            this.seriesName.setText(extras.getString("series name"));
        }
    }

    /**
     * Loads the description from database.
     */
    private void downloadDescription() {
        try {
            Series series = this.seriesProvider().getSeries(this.seriesId);
            series.register(this);

            this.seriesName.setText(series.name());
            this.seriesOverview.setText(series.overview());
            this.seriesStatus.setText(series.status().toString());
            this.seriesAirTime.setText(series.airTime());
            this.seriesAirDays.setText(series.airDay());
            this.seriesActors.setText(series.actors());
            this.seriesFirsAirDay.setText(series.airDate());
            this.seriesNetwork.setText(series.network());
            this.seriesGenre.setText(series.genres());
            this.seriesRuntime.setText(String.format(
                    this.getString(R.string.runtime_minutes_format), series.runtime()));

            final Episode nextToSee = series.nextEpisodeToSee();
            if (nextToSee != null) {
                this.nextToSee.setText(series.seasons().nextEpisodeToSee().name());
            } else {
                this.nextToSee.setText(R.string.up_to_date);
            }

            Bitmap bmp = this.seriesProvider().getPosterOf(series);
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
        this.dialog = ProgressDialog.show(SeriesDetailsActivity.this, "", this.getString(R.string.loading), true);

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

    //SeriesListener----------------------------------------------------------------------------------------------------

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        //TODO Update the 'progress' bar
        this.downloadDescription();
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        //TODO This behavior will depend on the user's settings (SharedPreference)
        this.downloadDescription();
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {
        //TODO This behavior will depend on the user's settings (SharedPreference)
    }

    @Override
    public void onMerge(Series series) {
        this.downloadDescription();
    }
}
