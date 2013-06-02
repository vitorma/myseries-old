package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.backup.BackupActivity;
import mobi.myseries.gui.preferencesactivity.PreferencesActivity;
import mobi.myseries.gui.shared.ToastBuilder;
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
        App.updateSeriesService().withHandler(new Handler());
        App.updateSeriesService().updateDataIfNeeded();
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

    // FIXME (Cleber) Menu should behave according to design guide, hiding most of the options when the side menu is open, for example.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(this.getString(R.string.menu_add)).setIcon(R.drawable.actionbar_add)
            .setIntent(AddSeriesActivity.newIntent(this))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(this.getString(R.string.menu_remove)).setIcon(R.drawable.actionbar_remove)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(this.getString(R.string.menu_update)).setIcon(R.drawable.actionbar_update)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(this.getString(R.string.menu_settings)).setIcon(R.drawable.actionbar_settings)
            .setIntent(PreferencesActivity.newIntent(this))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(this.getString(R.string.menu_backup_restore))
            .setIntent(BackupActivity.newIntent(this))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(this.getString(R.string.menu_update))) {
            this.runManualUpdate();
            return true;
        }

        if (item.getTitle().equals(this.getString(R.string.menu_remove))) {
            this.showRemoveDialog();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void runManualUpdate() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_update).build().show();
        } else {
            App.updateSeriesService().updateData();
        }
    }

    private void showRemoveDialog() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_remove).build().show();
        } else {
            new SeriesRemovalDialogFragment().show(this.getFragmentManager(), "removalDialog");
        }
    }
}
