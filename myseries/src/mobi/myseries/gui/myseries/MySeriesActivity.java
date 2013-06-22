package mobi.myseries.gui.myseries;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.backup.BackupActivity;
import mobi.myseries.gui.shared.ToastBuilder;
import mobi.myseries.gui.update.UpdateActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MySeriesActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MySeriesActivity.class);
    }

    /* FIXME (Cleber)
     * Check the behavior of updateDataIfNeeded.
     * Maybe would be needed save a boolean with onSaveInstanceState. Such method is better than onRetainNonConfigurationInstance,
     * because this one only works with rotations, not if the activity goes to the back stack. */

    @Override
    protected void init() {
        final Handler handler = new Handler();
        App.updateSeriesService().withHandler(handler);
        App.updateSeriesService().updateDataIfNeeded();
        App.followSeriesService().withHandler(handler);
        App.seriesSearch().withHandler(handler);
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
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_episodes_to_count).build().show();
        } else {
            new EpisodeFilterDialogFragment().show(this.getFragmentManager(), "episodeFilterDialog");
        }
    }

    private void showSortDialog() {
        Collection<Boolean> seriesToShow = App.preferences().forMySeries().seriesToShow().values();

        if (!seriesToShow.contains(true)) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_sort).build().show();
        } else {
            new SeriesSortingDialogFragment().show(this.getFragmentManager(), "seriesSortingDialog");
        }
    }
}
