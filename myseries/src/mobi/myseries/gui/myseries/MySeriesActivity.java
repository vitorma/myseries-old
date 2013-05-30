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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.backup.BackupActivity;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferencesactivity.PreferencesActivity;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.DialogButtonOnClickListener;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder.OnRequestRemovalListener;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.ToastBuilder;
import mobi.myseries.gui.shared.TopActivity;
import net.simonvt.menudrawer.MenuDrawer;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;

public class MySeriesActivity extends TopActivity {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    // TODO (Reul) Refresh after a successful update
    // TODO (Reul) Refresh after removing all series
    // TODO Menu from xml

    private StateHolder state;

    private MessageLauncher messageLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.myseries);

        ActionBar ab = this.getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(R.string.my_series);

        App.updateSeriesService().withHandler(new Handler());

        Object retained = this.getLastNonConfigurationInstance();
        if ((retained != null) && (retained instanceof StateHolder)) {
            this.state = (StateHolder) retained;
            this.messageLauncher = this.state.messageLauncher;
        } else {
            this.state = new StateHolder();
            this.messageLauncher = new MessageLauncher(this);
            this.state.messageLauncher = this.messageLauncher;
            this.launchAutomaticUpdate();
        }

        this.getMenu()
            .setTouchMode(
                this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE
                    ? MenuDrawer.TOUCH_MODE_BEZEL
                    : MenuDrawer.TOUCH_MODE_FULLSCREEN);
    }

    private void launchAutomaticUpdate() {
        App.updateSeriesService().updateDataIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadState();
    }

    private void loadState() {
        this.messageLauncher.loadState();
        if (this.state.isShowingDialog) {
            this.state.dialog.show();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this.state;
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.messageLauncher.onStop();
        if ((this.state.dialog != null) && this.state.dialog.isShowing()) {
            this.state.dialog.dismiss();
            this.state.isShowingDialog = true;
        } else {
            this.state.isShowingDialog = false;
        }
    }

    // Menu----------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(this.getString(R.string.menu_add)).setIcon(R.drawable.actionbar_add)
            .setIntent(new Intent(this, AddSeriesActivity.class))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(this.getString(R.string.menu_remove)).setIcon(R.drawable.actionbar_remove)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(this.getString(R.string.menu_update)).setIcon(R.drawable.actionbar_update)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(this.getString(R.string.menu_settings)).setIcon(R.drawable.actionbar_settings)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(this.getString(R.string.menu_backup_restore)).setShowAsAction(
            MenuItem.SHOW_AS_ACTION_NEVER);

        // TODO add intent
        // menu.add(getString(R.string.menu_help))
        // .setIcon(R.drawable.actionbar_help)
        // .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(this.getString(R.string.menu_update))) {

            final Context context = this;
            final Collection<Series> followedSeries = MySeriesActivity.SERIES_PROVIDER
                .followedSeries();

            if (followedSeries.isEmpty()) {
                new ToastBuilder(context).setMessage(R.string.no_series_to_update).build().show();
                return super.onMenuItemSelected(featureId, item);
            }

            App.updateSeriesService().updateData();
        }

        if (item.getTitle().equals(this.getString(R.string.menu_remove))) {
            this.showRemoveDialog();
        }

        if (item.getTitle().equals(this.getString(R.string.menu_settings))) {
            this.showSettingsActivity();
        }
        if (item.getTitle().equals(this.getString(R.string.menu_backup_restore))) {
            this.showBackupActivity();
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void showRemoveDialog() {
        final Context context = this;
        final SortedMap<Series, Boolean> removalOptions = new TreeMap<Series, Boolean>(
            new SeriesComparator());
        final Collection<Series> followedSeries = MySeriesActivity.SERIES_PROVIDER.followedSeries();

        if (followedSeries.isEmpty()) {
            new ToastBuilder(context).setMessage(R.string.no_series_to_remove).build().show();
            return;
        }

        for (Series s : followedSeries) {
            removalOptions.put(s, false);
        }

        new RemovingSeriesDialogBuilder(this).setDefaultRemovalOptions(removalOptions)
            .setOnRequestRemovalListener(new OnRequestRemovalListener() {
                @Override
                public void onRequestRemoval() {
                    final List<Series> allSeriesToRemove = new ArrayList<Series>();

                    for (Series s : removalOptions.keySet()) {
                        if (removalOptions.get(s)) {
                            allSeriesToRemove.add(s);
                        }
                    }

                    if (allSeriesToRemove.isEmpty()) {
                        new ToastBuilder(context).setMessage(R.string.no_series_selected_to_remove)
                            .build().show();
                        return;
                    }

                    new ConfirmationDialogBuilder(context).setTitle(R.string.are_you_sure)
                        .setMessage(R.string.cannot_be_undone).setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogButtonOnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                App.followSeriesService().stopFollowingAll(allSeriesToRemove);
                                Preferences.removeEntriesRelatedToAllSeries(allSeriesToRemove);

                                dialog.dismiss();
                            }
                        }).build().show();
                }
            }).build().show();
    }

    // private void showBackupDialog() {
    // try {
    // final BackupDialogBuilder dialogBuilder = new BackupDialogBuilder(this);
    // String folderPath = App.backupService().backupFolderPath();
    // dialogBuilder.setBackupFolder(folderPath);
    // dialogBuilder.setBackupButtonListener(new ButtonOnClickListener() {
    // @Override
    // public void onClick(Dialog dialog) {
    // new ConfirmationDialogBuilder(dialogBuilder.context())
    // .setTitle(R.string.are_you_sure)
    // .setMessage(R.string.overwrite_backup)
    // .setNegativeButton(R.string.no, null)
    // .setPositiveButton(R.string.yes, new ButtonOnClickListener() {
    // @Override
    // public void onClick(Dialog dialog) {
    // App.backupService().doBackup();
    // dialog.dismiss();
    // }
    // })
    // .build()
    // .show();
    // }
    // });
    // dialogBuilder.setRestoreButtonListener(new ButtonOnClickListener() {
    // @Override
    // public void onClick(Dialog dialog) {
    // new ConfirmationDialogBuilder(dialogBuilder.context())
    // .setTitle(R.string.are_you_sure)
    // .setMessage(R.string.actual_following_series_will_be_replaced)
    // .setNegativeButton(R.string.no, null)
    // .setPositiveButton(R.string.yes, new ButtonOnClickListener() {
    // @Override
    // public void onClick(Dialog dialog) {
    // App.backupService().restoreBackup();
    // dialog.dismiss();
    // }
    // })
    // .build()
    // .show();
    // }
    // });
    // dialogBuilder.build().show();
    // } catch (ExternalStorageNotAvailableException e) {
    // new FailureDialogBuilder(this)
    // .setTitle(R.string.external_storage_not_available)
    // .setMessage(R.string.backup_storage_failure)
    // .build().show();
    // }
    //
    // }
    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    // Search--------------------------------------------------------------------------------------

    private void showSearchActivity() {
        final Intent intent = new Intent(this, AddSeriesActivity.class);
        this.startActivity(intent);
    }

    private static class StateHolder {
        Dialog dialog;
        boolean isShowingDialog;
        MessageLauncher messageLauncher;
    }

    // Settings------------------------------------------------------------------------------------
    private void showSettingsActivity() {
        this.startActivity(PreferencesActivity.newIntent(this));
    }

    // Backup--------------------------------------------------------------------------------------
    private void showBackupActivity() {
        this.startActivity(BackupActivity.newIntent(this));
    }
}
