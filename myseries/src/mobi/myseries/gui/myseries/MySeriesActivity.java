/*
 *   MySeriesActivity.java
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

package mobi.myseries.gui.myseries;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
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

            final Context context = this;
            final Collection<Series> followedSeries = App.seriesProvider().followedSeries();

            if (followedSeries.isEmpty()) {
                new ToastBuilder(context).setMessage(R.string.no_series_to_update).build().show();
                return super.onMenuItemSelected(featureId, item);
            }

            App.updateSeriesService().updateData();
        }

        if (item.getTitle().equals(this.getString(R.string.menu_remove))) {
            this.showRemoveDialog();
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void showRemoveDialog() {
        if (App.seriesProvider().followedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_remove).build().show();
        } else {
            new SeriesRemovalDialogFragment().show(this.getFragmentManager(), "removalDialog");
        }
    }
}
