package mobi.myseries.gui.library;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.features.Feature;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.backup.BackupActivity;
import mobi.myseries.gui.shared.ToastBuilder;
import mobi.myseries.gui.update.UpdateActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class LibraryActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, LibraryActivity.class);
    }

    private static final String ALREADY_CHECKED_FOR_UPDATE = "ALREADY_CHECKED_FOR_UPDATE";
    private boolean alreadyCheckedForUpdate = false;

    @Override
    protected void init(Bundle savedInstanceState) {
        //FIXME (Cleber) This boolean expression is always being evaluated as true. =/
        //                    savedInstanceState == null
        //                The onSaveInstanceState method isn't always called when an activity is being placed in the background.
        //
        //                From the official documentation:
        //                The onSaveInstanceState method is called before an activity may be killed so that when it comes back some time in
        //                the future it can restore its state. For example, if activity B is launched in front of activity A, and at
        //                some point activity A is killed to reclaim resources, activity A will have a chance to save the current state of
        //                its user interface via this method so that when the user returns to activity A, the state of the user interface
        //                can be restored via onCreate(Bundle) or onRestoreInstanceState(Bundle).
        if (savedInstanceState == null || !savedInstanceState.getBoolean(ALREADY_CHECKED_FOR_UPDATE, false)) {
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
        return this.getText(R.string.ab_title_library);
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
        return this.getText(R.string.nav_library);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.myseries, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.backup_restore).setVisible(App.features().isVisible(Feature.BACKUP));

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
                this.runManualUpdate();
                return true;
            case R.id.updates:
                this.startActivity(UpdateActivity.newIntent(this));
                return true;
            case R.id.backup_restore:
                if (App.features().isVisible(Feature.BACKUP)) {
                    this.startActivity(BackupActivity.newIntent(this));
                    return true;
                }
            case R.id.settings:
                //XXX Implement me
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRemoveDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_remove).build().show();
        } else {
            new SeriesRemovalDialogFragment().show(this.getFragmentManager(), "removalDialog");
        }
    }

    private void showSeriesFilterDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
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

    private void runManualUpdate() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_update).build().show();
        } else {
            App.updateSeriesService().updateData();
        }
    }
}
