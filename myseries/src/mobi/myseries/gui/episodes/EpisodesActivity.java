/*
 *   EpisodesActivity.java
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

package mobi.myseries.gui.episodes;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.shared.MessageLauncher;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

public class EpisodesActivity extends BaseActivity {

    public static interface Extra {
        public String SERIES_ID = "seriesId";
        public String SEASON_NUMBER = "seasonNumber";
        public String EPISODE_NUMBER = "episodeNumber";
    }

    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    private int seriesId;
    private int seasonNumber;
    private int episodeNumber;
    private String title;

    private EpisodePagerAdapter adapter;
    private ViewPager pager;

    private StateHolder state;
    private MessageLauncher messageLauncher;

    public static Intent newIntent(Context context, int seriesId, int seasonNumber, int episodeNumber) {
        Intent intent = new Intent(context, EpisodesActivity.class);

        intent.putExtra(Extra.SERIES_ID, seriesId);
        intent.putExtra(Extra.SEASON_NUMBER, seasonNumber);
        intent.putExtra(Extra.EPISODE_NUMBER, episodeNumber);

        return intent;
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadState();
    }

    private void loadState() {
        this.messageLauncher.loadState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.messageLauncher.onStop();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this.state;
    }

    /* STATE HOLDER */
    private static class StateHolder {
        MessageLauncher messageLauncher;
    }

    @Override
    protected int layoutResource() {
        return R.layout.activity_base_paged;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected void init() {
        Bundle extras = this.getIntent().getExtras();

        this.seriesId = extras.getInt(Extra.SERIES_ID);
        this.seasonNumber = extras.getInt(Extra.SEASON_NUMBER);
        this.episodeNumber = extras.getInt(Extra.EPISODE_NUMBER);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        if (series == null) {
            this.finish();
            return;
        }

        this.title = series.name();

        List<Episode> episodes = series.episodes();

        this.adapter = new EpisodePagerAdapter(this, this.getFragmentManager(), episodes);
        this.pager = (ViewPager) this.findViewById(R.id.pager);
        this.pager.setAdapter(this.adapter);

        Episode current = series.season(this.seasonNumber).episode(this.episodeNumber);
        this.pager.setCurrentItem(this.adapter.positionOf(current));

        Object retained = this.getLastNonConfigurationInstance();
        if ((retained != null) && (retained instanceof StateHolder)) {
            this.state = (StateHolder) retained;
            this.messageLauncher = this.state.messageLauncher;
        } else {
            this.state = new StateHolder();
            this.messageLauncher = new MessageLauncher(this);
            this.state.messageLauncher = this.messageLauncher;
        }
    }

    @Override
    protected CharSequence title() {
        return this.title;
    }
}
