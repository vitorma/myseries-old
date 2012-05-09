/*
 *   EpisodeDetailsActivity.java
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

package mobi.myseries.gui.detail;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TitlePageIndicator;

public class EpisodeDetailsActivity extends SherlockFragmentActivity {
    private static final String SERIES_ID = "seriesId";
    private static final String SEASON_NUMBER = "seasonNumber";
    private static final String EPISODE_NUMBER = "episodeNumber";
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private int seriesId;
    private int seasonNumber;
    private int episodeNumber;

    private EpisodePagerAdapter adapter;
    private ViewPager pager;
    private TitlePageIndicator pageIndicator;

    public static Intent newIntent(Context context, int seriesId, int seasonNumber, int episodeNumber) {
        Intent intent = new Intent(context, EpisodeDetailsActivity.class);

        intent.putExtra(SERIES_ID, seriesId);
        intent.putExtra(SEASON_NUMBER, seasonNumber);
        intent.putExtra(EPISODE_NUMBER, episodeNumber);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.episode_view);

        Bundle extras = this.getIntent().getExtras();

        this.seriesId = extras.getInt(SERIES_ID);
        this.seasonNumber = extras.getInt(SEASON_NUMBER);
        this.episodeNumber = extras.getInt(EPISODE_NUMBER);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        List<Episode> episodes = series.episodes();

        this.adapter = new EpisodePagerAdapter(this, this.getSupportFragmentManager(), episodes);
        this.pager = (ViewPager) this.findViewById(R.id.pager);
        this.pager.setAdapter(this.adapter);
        this.pageIndicator = (TitlePageIndicator) this.findViewById(R.id.titles);
        this.pageIndicator.setViewPager(pager);

        Episode current = series.season(this.seasonNumber).episode(this.episodeNumber);
        this.pager.setCurrentItem(this.adapter.positionOf(current));

        ActionBar ab = this.getSupportActionBar();
        String ep = this.getString(R.string.episode_overview);
        ab.setTitle(ep + " " + series.name());
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = SeriesOverviewActivity.newIntent(this, this.seriesId);
                this.startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
