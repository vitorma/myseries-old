package mobi.myseries.gui.mystatistics;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.shared.ToastBuilder;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public class MyStatisticsActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MyStatisticsActivity.class);
    }

    @Override
    protected void init() {
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected int layoutResource() {
        return R.layout.mystatistics;
    }

    @Override
    protected CharSequence title() {
        return this.getString(R.string.my_statistics);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getString(R.string.nav_statistics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.mystatistics, menu);

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
        switch (item.getItemId()) {
        case R.id.filter_episodes:
            this.showEpisodeFilterDialog();
            return true;
        case R.id.filter_series:
            this.showSeriesFilterDialog();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void showSeriesFilterDialog() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_filter).build().show();
        } else {
            new SeriesFilterDialogFragment().show(this.getFragmentManager(), "seriesFilterDialog");
        }
    }

    private void showEpisodeFilterDialog() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_episodes_to_count).build().show();
        } else {
            new EpisodeFilterDialogFragment()
                .show(this.getFragmentManager(), "episodeFilterDialog");
        }
    }
}
