package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.episodes.EpisodeListFragment.EpisodeListFragmentListener;
import mobi.myseries.gui.episodes.EpisodePagerFragment.EpisodePagerFragmentListener;
import mobi.myseries.gui.series.SeriesActivity;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class EpisodesActivity extends BaseActivity
        implements OnNavigationListener, EpisodeListFragmentListener, EpisodePagerFragmentListener, OnSharedPreferenceChangeListener {

    private static final String EPISODE_LIST_FRAGMENT = "episodeListFragment";
    private static final String EPISODE_PAGER_FRAGMENT = "episodePagerFragment";
    private static final String EPISODE_SORTING_DIALOG_FRAGMENT = "episodeSortingDialogFragment";

    private static final String EXTRA_SHOW_LIST_FRAGMENT = "showEpisodeListFragment";
    private static final String EXTRA_SHOW_PAGER_FRAGMENT = "showEpisodePagerFragment";

    private static final int INVALID_EPISODE_NUMBER = -1;
    private static final int NATURAL_FIRST_POSITION = 0;

    public static Intent newIntent(Context context, int seriesId, int seasonNumber) {
        return newIntent(context, seriesId, seasonNumber, INVALID_EPISODE_NUMBER);
    }

    public static Intent newIntent(Context context, int seriesId, int seasonNumber, int episodeNumber) {
        Intent intent = new Intent(context, EpisodesActivity.class);

        intent.putExtra(Extra.SERIES_ID, seriesId);
        intent.putExtra(Extra.SEASON_NUMBER, seasonNumber);
        intent.putExtra(Extra.EPISODE_NUMBER, episodeNumber);

        return intent;
    }

    private Series series;
    private int seasonNumber;
    private int episodeNumber;
    private int sortMode;
    private boolean isShowingListFragment;
    private boolean isShowingPagerFragment;

    private SeasonSpinnerAdapter spinnerAdapter;

    private EpisodeListFragment listFragment;
    private EpisodePagerFragment pagerFragment;

    @Override
    protected void init(Bundle savedInstanceState) {
        this.extractExtras(savedInstanceState);
        this.setUpActionBarToFilterEpisodesBySeason();
        this.setUpFragments(savedInstanceState);
    }

    @Override
    protected CharSequence title() {
        return "";
    }

    @Override
    protected int layoutResource() {
        return R.layout.episodes_activity;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected Intent navigateUpIntent() {
        return SeriesActivity.newIntent(this, series.id()).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.season, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (this.isDrawerOpen()) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sort:
                this.showSortingDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortingDialog() {
        Season season = this.series.season(this.seasonNumber);

        if (season.episodes().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_episodes_to_sort).build().show();
        } else {
            new EpisodeSortingDialogFragment().show(this.getFragmentManager(), EPISODE_SORTING_DIALOG_FRAGMENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        App.preferences().forActivities().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        App.preferences().forActivities().deregister(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SERIES_ID, this.series.id());
        outState.putInt(Extra.SEASON_NUMBER, this.seasonNumber);
        outState.putInt(Extra.EPISODE_NUMBER, this.episodeNumber);
        outState.putInt(Extra.SORT_MODE, this.sortMode);

        outState.putBoolean(EXTRA_SHOW_LIST_FRAGMENT, this.isShowingListFragment);
        outState.putBoolean(EXTRA_SHOW_PAGER_FRAGMENT, this.isShowingPagerFragment);

        super.onSaveInstanceState(outState);
    }

    private void extractExtras(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            Bundle extras = this.getIntent().getExtras();

            this.series = App.seriesProvider().getSeries(extras.getInt(Extra.SERIES_ID));
            this.seasonNumber = extras.getInt(Extra.SEASON_NUMBER);
            this.episodeNumber = extras.getInt(Extra.EPISODE_NUMBER);
            this.sortMode = App.preferences().forEpisodes().sortMode();

            this.isShowingListFragment = this.isDualPane() || this.wasIntentedToShowList();
            this.isShowingPagerFragment = this.isDualPane() || !this.wasIntentedToShowList();
        } else {
            this.series = App.seriesProvider().getSeries(savedInstanceState.getInt(Extra.SERIES_ID));
            this.seasonNumber = savedInstanceState.getInt(Extra.SEASON_NUMBER);
            this.episodeNumber = savedInstanceState.getInt(Extra.EPISODE_NUMBER);
            this.sortMode = savedInstanceState.getInt(Extra.SORT_MODE);

            this.isShowingListFragment = savedInstanceState.getBoolean(EXTRA_SHOW_LIST_FRAGMENT);
            this.isShowingPagerFragment = savedInstanceState.getBoolean(EXTRA_SHOW_PAGER_FRAGMENT);
        }
    }

    private void setUpActionBarToFilterEpisodesBySeason() {
        this.spinnerAdapter = new SeasonSpinnerAdapter(this.series);

        this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        this.getActionBar().setListNavigationCallbacks(this.spinnerAdapter, this);
        this.getActionBar().setSelectedNavigationItem(this.series.seasons().positionOf(this.seasonNumber));
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        int newSeasonNumber = (int) itemId;

        if (this.seasonNumber == newSeasonNumber) { return true; }

        this.seasonNumber = newSeasonNumber;
        this.episodeNumber = this.firstEpisodeNumber();

        if (this.isShowingListFragment) {
            this.listFragment.update(this.series.season(this.seasonNumber), this.episodeNumber);
        } else {
            Log.d("EpisodesActivity", "List fragment is not shown");
        }

        if (this.isShowingPagerFragment) {
            this.pagerFragment.update(this.series.season(this.seasonNumber), this.episodeNumber);
        } else {
            Log.d("EpisodesActivity", "Pager fragment is not shown");
        }

        return true;
    }

    private void setUpFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();

            if (this.isShowingListFragment) {
                this.listFragment = EpisodeListFragment.newInstance(this.series.id(), this.seasonNumber, this.validEpisodeNumber());
                ft.add(this.listFragmentContainerId(), this.listFragment, EPISODE_LIST_FRAGMENT);
            }

            if (this.isShowingPagerFragment) {
                this.pagerFragment = EpisodePagerFragment.newInstance(this.series.id(), this.seasonNumber, this.validEpisodeNumber());
                ft.add(this.pagerFragmentContainerId(), this.pagerFragment, EPISODE_PAGER_FRAGMENT);
            }

            ft.commit();
        } else {
            this.listFragment = (EpisodeListFragment) this.getFragmentManager().findFragmentByTag(EPISODE_LIST_FRAGMENT);
            this.pagerFragment = (EpisodePagerFragment) this.getFragmentManager().findFragmentByTag(EPISODE_PAGER_FRAGMENT);
        }
    }

    @Override
    public void onSelectListItem(int position) {
        if (this.isShowingPagerFragment) {
            if (this.shouldSelect(position)) {
                this.episodeNumber = this.episodeNumber(position);
                this.pagerFragment.selectPage(position);
            }
        } else {
            this.isShowingListFragment = false;
            this.isShowingPagerFragment = true;

            this.pagerFragment = EpisodePagerFragment.newInstance(this.series.id(), this.seasonNumber, this.episodeNumber(position));

            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.replace(this.pagerFragmentContainerId(), this.pagerFragment, EPISODE_PAGER_FRAGMENT);
            ft.commit();
        }
    }

    @Override
    public boolean shouldHighlightSelectedItem() {
        return this.isDualPane();
    }

    @Override
    public void onSelectPage(int position) {
        if (this.isShowingListFragment && this.shouldSelect(position)) {
            this.episodeNumber = this.episodeNumber(position);
            this.listFragment.selectItem(position);
        }
    }

    private boolean shouldSelect(int position) {
        return !this.hasValidEpisodeNumber() || position != this.positionOfCurrentEpisode();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        int newSortMode = App.preferences().forEpisodes().sortMode();

        if (this.sortMode == newSortMode) { return; }

        this.sortMode = newSortMode;

        if (this.isShowingListFragment) {
            this.listFragment.update(this.series.season(this.seasonNumber));
        }

        if (this.isShowingPagerFragment) {
            this.pagerFragment.update(this.series.season(this.seasonNumber));
        }
    }

    @Override
    public void onBackPressed() {
        if (this.shouldGoBackToListFragment()) {
            this.isShowingListFragment = true;
            this.isShowingPagerFragment = false;

            this.listFragment = EpisodeListFragment.newInstance(this.series.id(), this.seasonNumber, this.validEpisodeNumber());

            FragmentTransaction ft = this.getFragmentManager().beginTransaction();
            ft.replace(this.listFragmentContainerId(), this.listFragment, EPISODE_LIST_FRAGMENT);
            ft.commit();
        } else {
            super.onBackPressed();
        }
    }

    private boolean shouldGoBackToListFragment() {
        return this.isShowingPagerFragment &&
               !this.isShowingListFragment &&
               this.wasIntentedToShowList();
    }

    private boolean wasIntentedToShowList() {
        return this.getIntent().getExtras().getInt(Extra.EPISODE_NUMBER) == INVALID_EPISODE_NUMBER;
    }

    public boolean isDualPane() {
        return this.getResources().getBoolean(R.bool.isTablet);
    }

    private int listFragmentContainerId() {
        return this.isDualPane() ? R.id.container_for_list : R.id.container;
    }

    private int pagerFragmentContainerId() {
        return this.isDualPane() ? R.id.container_for_pager : R.id.container;
    }

    private int validEpisodeNumber() {
        return this.hasValidEpisodeNumber() ?
               this.episodeNumber:
               this.firstEpisodeNumber();
    }

    private boolean hasValidEpisodeNumber() {
        return this.episodeNumber != INVALID_EPISODE_NUMBER;
    }

    private int positionOfCurrentEpisode() {
        if (!this.hasValidEpisodeNumber()) { return NATURAL_FIRST_POSITION; }

        Season currentSeason = this.series.season(this.seasonNumber);
        int naturalPosition = currentSeason.positionOf(this.episodeNumber);

        switch (this.sortMode) {
            case SortMode.NEWEST_FIRST:
                return currentSeason.numberOfEpisodes() - 1 - naturalPosition;
            case SortMode.OLDEST_FIRST:
            default:
                return naturalPosition;
        }
    }

    private int firstEpisodeNumber() {
        return this.episodeNumber(NATURAL_FIRST_POSITION);
    }

    private int episodeNumber(int position) {
        Season currentSeason = this.series.season(this.seasonNumber);
        int reversedPosition = currentSeason.numberOfEpisodes() - 1 - position;

        switch (this.sortMode) {
            case SortMode.NEWEST_FIRST:
                return currentSeason.episodeAt(reversedPosition).number();
            case SortMode.OLDEST_FIRST:
            default:
                return currentSeason.episodeAt(position).number();
        }
    }
}
