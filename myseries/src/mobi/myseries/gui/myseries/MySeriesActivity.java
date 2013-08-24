package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.backup.BackupActivity;
import mobi.myseries.gui.shared.ToastBuilder;
import mobi.myseries.gui.update.UpdateActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class MySeriesActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MySeriesActivity.class);
    }

    private static final String ALREADY_CHECKED_FOR_UPDATE = "ALREADY_CHECKED_FOR_UPDATE";
    private boolean alreadyCheckedForUpdate = false;

    @Override
    protected void init(Bundle savedInstanceState) {
        final Handler handler = new Handler();

        App.updateSeriesService().withHandler(handler);
        App.followSeriesService().withHandler(handler);
        App.seriesSearch().withHandler(handler);

        if (savedInstanceState == null
                || (savedInstanceState != null && !savedInstanceState.getBoolean(ALREADY_CHECKED_FOR_UPDATE, false))) {
            App.updateSeriesService().updateDataIfNeeded();
        }
        this.alreadyCheckedForUpdate = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_CHECKED_FOR_UPDATE, this.alreadyCheckedForUpdate);
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.my_series);
    }

    @Override
    protected int layoutResource() {
        return R.layout.myseries;
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected Intent navigateUpIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getText(R.string.nav_shows);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.myseries, menu);

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
            case R.id.add:
                this.startActivity(AddSeriesActivity.newIntent(this));
                return true;
            case R.id.remove:
                this.showRemoveDialog();
                return true;
            case R.id.filter_episodes:
                this.showEpisodeFilterDialog();
                return true;
            case R.id.filter_series:
                this.showSeriesFilterDialog();
                return true;
            case R.id.sort:
                this.showSortDialog();
                return true;
            case R.id.update:
                this.startActivity(UpdateActivity.newIntent(this));
                return true;
            case R.id.backup_restore:
                this.startActivity(BackupActivity.newIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRemoveDialog() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_remove).build().show();
        } else {
            new SeriesRemovalDialogFragment().show(this.getFragmentManager(), "removalDialog");
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
        new EpisodeFilterDialogFragment().show(this.getFragmentManager(), "episodeFilterDialog");
    }

    private void showSortDialog() {
        new SeriesSortingDialogFragment().show(this.getFragmentManager(), "seriesSortingDialog");
    }
}
