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
import java.util.HashMap;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferencesactivity.PreferencesActivity;
import mobi.myseries.gui.seriessearch.SeriesSearchActivity;
import mobi.myseries.gui.shared.BackupDialogBuilder;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.ButtonOnClickListener;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder.OnRequestRemovalListener;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class MySeriesActivity extends SherlockFragmentActivity implements UpdateListener {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    //TODO (Reul) Refresh after a successful update
    //TODO (Reul) Refresh after removing all series
    //TODO Menu from xml
    //TODO Internationalized string
    private static final String SCHEDULE = "SCHEDULE";
    private static final String ADD = "ADD SERIES";
    private static final String REMOVE = "REMOVE SERIES";
    private static final String UPDATE = "UPDATE";
    private static final String SETTINGS = "SETTINGS";
    private static final String BACKUP_RESTORE = "BACKUP/RESTORE";
    private static final String HELP = "HELP";

    private StateHolder state;

    private MessageLauncher messageLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.myseries);
        this.setSupportProgressBarIndeterminateVisibility(false);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.my_series);

        App.updateSeriesService().register(this);
        App.updateSeriesService().withHandler(new Handler());

        Object retained = this.getLastCustomNonConfigurationInstance();
        if ((retained != null) && (retained instanceof StateHolder)) {
            this.state = (StateHolder) retained;
            this.messageLauncher = this.state.messageLauncher;
        } else {
            this.state = new StateHolder();
            this.messageLauncher = new MessageLauncher(this);
            this.state.messageLauncher = this.messageLauncher;
            this.launchAutomaticUpdate();
        }
    }

    private void launchAutomaticUpdate() {
        new Thread() {
            @Override
            public void run() {
                App.updateSeriesService().updateDataIfNeeded();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadState();
        this.setSupportProgressBarIndeterminateVisibility(App.updateSeriesService().isUpdating());
    }

    private void loadState() {
        this.messageLauncher.loadState();
        if (this.state.isShowingDialog){
            this.state.dialog.show();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
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

    //Menu------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(SCHEDULE)
        .setIntent(MyScheduleActivity.newIntent(this, ScheduleMode.NEXT))
        .setIcon(R.drawable.actionbar_calendar)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(ADD)
        .setIcon(R.drawable.actionbar_add)
        .setIntent(new Intent(this, SeriesSearchActivity.class))
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(REMOVE)
        .setIcon(R.drawable.actionbar_remove)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(UPDATE)
        .setIcon(R.drawable.actionbar_update)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        menu.add(SETTINGS)
        .setIcon(R.drawable.actionbar_settings)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        
        menu.add(BACKUP_RESTORE)
        .setIcon(R.drawable.actionbar_settings)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        //TODO add intent
        menu.add(HELP)
        .setIcon(R.drawable.actionbar_help)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().equals(UPDATE)) {

            final Context context = this;
            final Collection<Series> followedSeries = SERIES_PROVIDER.followedSeries();

            if (followedSeries.isEmpty()) {
                new ToastBuilder(context).setMessage(R.string.no_series_to_update).build().show();
                return super.onMenuItemSelected(featureId, item);
            }


            new Thread() {
                @Override
                public void run() {
                    App.updateSeriesService().updateData();
                }
            }.start();
        }

        if (item.getTitle().equals(REMOVE)) {
            this.showRemoveDialog();
        }

        if (item.getTitle().equals(SETTINGS)) {
            this.showSettingsActivity();
        }
        
        if(item.getTitle().equals(BACKUP_RESTORE)) {
            this.showBackupDialog();
        }

        return super.onMenuItemSelected(featureId, item);
    }


    private void showRemoveDialog() {
        final Context context = this;
        final HashMap<Series, Boolean> removalOptions = new HashMap<Series, Boolean>();
        final Collection<Series> followedSeries = SERIES_PROVIDER.followedSeries();

        if (followedSeries.isEmpty()) {
            new ToastBuilder(context).setMessage(R.string.no_series_to_remove).build().show();
            return;
        }

        for (Series s : followedSeries) {
            removalOptions.put(s, false);
        }

        new RemovingSeriesDialogBuilder(this)
        .setDefaultRemovalOptions(removalOptions)
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
                    new ToastBuilder(context)
                            .setMessage(R.string.no_series_selected_to_remove).build()
                            .show();
                    return;
                }

                new ConfirmationDialogBuilder(context)
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.cannot_be_undone)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new ButtonOnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        App.followSeriesService().stopFollowingAll(allSeriesToRemove);
                        Preferences.removeEntriesRelatedToAllSeries(allSeriesToRemove);

                        dialog.dismiss();
                    }
                })
                .build()
                .show();
            }
        })
        .build()
        .show();
    }

    private void showBackupDialog() {
        final BackupDialogBuilder dialogBuilder = new BackupDialogBuilder(this);
        dialogBuilder.setBackupFolder(App.backupService().sdCardPath());
        dialogBuilder.setBackupButtonListener(new ButtonOnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                new ConfirmationDialogBuilder(dialogBuilder.context())
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.overwrite_backup)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new ButtonOnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        App.backupService().doBackup();
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
                
            }
        });
        dialogBuilder.setRestoreButtonListener(new ButtonOnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                new ConfirmationDialogBuilder(dialogBuilder.context())
                .setTitle(R.string.are_you_sure)
                .setMessage(R.string.actual_following_series_will_be_replaced)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.yes, new ButtonOnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        App.backupService().restoreBackup();
                        dialog.dismiss();
                    }
                })
                .build()
                .show();
                
            }
        });
        dialogBuilder.build().show();
        
    }
    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    @Override
    public void onUpdateStart() {
        Log.d(this.getClass().getName(), "update started");
        this.setSupportProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void onUpdateFailure(Exception e) {
        Log.d(this.getClass().getName(), "update failure");
        this.setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onUpdateSuccess() {
        Log.d(this.getClass().getName(), "update complete");
        this.setSupportProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void onUpdateNotNecessary() {
        Log.d(this.getClass().getName(), "update not necessary yet");
        this.setSupportProgressBarIndeterminateVisibility(false);
    }

    //Search----------------------------------------------------------------------------------------

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchActivity.class);
        this.startActivity(intent);
    }

    private static class StateHolder {
        Dialog dialog;
        boolean isShowingDialog;
        MessageLauncher messageLauncher;
    }


    //Settings--------------------------------------------------------------------------------------
    private void showSettingsActivity() {
        this.startActivity(PreferencesActivity.newIntent(this));
    }

}
