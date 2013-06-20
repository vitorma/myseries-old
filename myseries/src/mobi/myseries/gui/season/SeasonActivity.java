package mobi.myseries.gui.season;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.episode.EpisodeActivity;
import mobi.myseries.gui.episode.EpisodeFragment;
import mobi.myseries.gui.season.SeasonFragment.OnEpisodeSelectedListener;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class SeasonActivity extends BaseActivity implements OnEpisodeSelectedListener {

    public static Intent newIntent(Context context, int seriesId, int seasonNumber) {
        Intent intent = new Intent(context, SeasonActivity.class);

        intent.putExtra(Extra.SERIES_ID, seriesId);
        intent.putExtra(Extra.SEASON_NUMBER, seasonNumber);

        return intent;
    }

    private int seriesId;
    private int seasonNumber;
    private String title;

    @Override
    protected void init(Bundle savedInstanceState) {
        Bundle extras = this.getIntent().getExtras();

        this.seriesId = extras.getInt(Extra.SERIES_ID);
        this.seasonNumber = extras.getInt(Extra.SEASON_NUMBER);

        Series series = App.seriesProvider().getSeries(this.seriesId);

        if (series == null) {
            this.finish();
            return;
        }

        this.title = series.name();

        //TODO Subtitle for some activities
//        String format = this.getString(R.string.season_number_format_ext);
//        String subtitle = String.format(format, this.seasonNumber);
//        this.getActionBar().setSubtitle(subtitle);

        if (savedInstanceState != null) {
            return;
        }

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        SeasonFragment fragment = SeasonFragment.newInstance(this.seriesId, this.seasonNumber);
        ft.add(R.id.container_list, fragment, "seasonFragment");

        if (this.isDualPane()) {
            int episodeNumber = App.seriesProvider().getSeries(this.seriesId).season(this.seasonNumber).episodeAt(0).number();
            EpisodeFragment fragment2 = EpisodeFragment.newInstance(this.seriesId, this.seasonNumber, episodeNumber);
            ft.add(R.id.container_details, fragment2, "episodeFragment");
        }

        ft.commit();
    }

    public boolean isDualPane() {
        return this.getResources().getBoolean(R.bool.isTablet);
    }

    @Override
    protected CharSequence title() {
        return this.title;
    }

    @Override
    protected int layoutResource() {
        return R.layout.season;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
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
                this.showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSortDialog() {
        Season s = App.seriesProvider().getSeries(this.seriesId).season(this.seasonNumber);

        if (s.episodes().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_episodes_to_sort).build().show();
        } else {
            new EpisodeSortingDialogFragment().show(this.getFragmentManager(), "episodeSortingDialog");
        }
    }

    @Override
    public void onSelected(Episode e) {
        if (this.isDualPane()) {
            EpisodeFragment ef = (EpisodeFragment) this.getFragmentManager().findFragmentByTag("episodeFragment");
            ef.updateWith(e);
        } else {
            Intent intent = EpisodeActivity.newIntent(this, e.seriesId(), e.seasonNumber(), e.number());
            this.startActivity(intent);
        }
    }
}
