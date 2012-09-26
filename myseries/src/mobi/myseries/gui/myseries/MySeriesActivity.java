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
import mobi.myseries.application.ErrorServiceListener;
import mobi.myseries.application.FollowSeriesException;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.UpdateException;
import mobi.myseries.application.UpdateListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.UpdateMetadataUnavailableException;
import mobi.myseries.gui.myschedule.MyScheduleActivity;
import mobi.myseries.gui.seriessearch.SeriesSearchActivity;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder.ButtonOnClickListener;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder;
import mobi.myseries.gui.shared.RemovingSeriesDialogBuilder.OnRequestRemovalListener;
import mobi.myseries.gui.shared.ToastBuilder;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class MySeriesActivity extends SherlockFragmentActivity implements UpdateListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    //TODO Menu from xml
    //TODO Internationalized string
    private static final String SCHEDULE = "SCHEDULE";
    private static final String ADD = "ADD SERIES";
    private static final String REMOVE = "REMOVE SERIES";
    private static final String UPDATE = "UPDATE";
    private static final String SETTINGS = "SETTINGS";
    private static final String HELP = "HELP";

    private boolean updating = false;
    private StateHolder state;
    private ErrorServiceListener errorListener;

    private ToastBuilder toastBuilder;

    public MySeriesActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.myseries);
        setSupportProgressBarIndeterminateVisibility(false);

        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.my_series);

        setupErrorServiceListener();

        Object retained = getLastCustomNonConfigurationInstance();
        if ((retained != null) && (retained instanceof StateHolder)) {
            state = (StateHolder) retained;
            loadState();
        } else {
            state = new StateHolder();
        }

        updating = App.updateSeriesService().isUpdating();

        App.updateSeriesService().registerSeriesUpdateListener(this);

        setSupportProgressBarIndeterminateVisibility(updating);

        if (!updating) {
            App.updateSeriesService().updateDataIfNeeded();
        }
    }

    private void loadState() {

        if (state.isShowingDialog){
            state.dialog.show();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return state;
    }

    @Override
    protected void onStop() {
        super.onStop();
        App.errorService().deregisterListener(errorListener);
        if ((state.dialog != null) && state.dialog.isShowing()) {
            state.dialog.dismiss();
            state.isShowingDialog = true;
        } else {
            state.isShowingDialog = false;
        }

    }

    //Menu--------------------------------------------------------------------------------------------------------------

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

        //TODO add intent
        menu.add(SETTINGS)
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
            App.updateSeriesService().updateData();
        }

        if (item.getTitle().equals(REMOVE)) {
            showRemoveDialog();
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
                    new ToastBuilder(context).setMessage(R.string.no_series_selected_to_remove).build().show();
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

    @Override
    public boolean onSearchRequested() {
        showSearchActivity();
        return true;
    }

    @Override
    public void onUpdateStart() {
        Log.d(getClass().getName(), "update started");
        setSupportProgressBarIndeterminateVisibility(true);
        updating = true;
        toastBuilder().setMessage(R.string.update_started_message);
        toastBuilder().build().show();
    }

    @Override
    public void onUpdateFailure(Exception e) {
        Log.d(getClass().getName(), "update failure");
        setSupportProgressBarIndeterminateVisibility(false);
        updating = false;
    }

    @Override
    public void onUpdateSuccess() {
        Log.d(getClass().getName(), "update complete");
        setSupportProgressBarIndeterminateVisibility(false);
        updating = false;
        toastBuilder().setMessage(R.string.update_success_message);
        toastBuilder().build().show();
    }

    @Override
    public void onUpdateNotNecessary() {
        Log.d(getClass().getName(), "update not necessary yet");
        setSupportProgressBarIndeterminateVisibility(false);
        updating = false;
    }

    //Search------------------------------------------------------------------------------------------------------------

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchActivity.class);
        startActivity(intent);
    }

    private void setupErrorServiceListener() {
        errorListener = new ErrorServiceListener() {

            @Override
            public void onError(Exception e) {
                if (e instanceof FollowSeriesException) {
                    FollowSeriesException followException = ((FollowSeriesException) e);
                    Series series = followException.series();
                    FailureDialogBuilder dialogBuilder = new FailureDialogBuilder(
                            MySeriesActivity.this);
                    dialogBuilder.setTitle(R.string.add_failed_title);
                    if (followException.getCause() instanceof ConnectionFailedException) {
                        dialogBuilder.setMessage(String.format(MySeriesActivity.this
                                .getString(R.string.add_connection_failed_message), series.name()));

                    } else if (followException.getCause() instanceof SeriesNotFoundException) {
                        dialogBuilder.setMessage(String.format(MySeriesActivity.this
                                .getString(R.string.add_series_not_found), series.name()));

                    } else if (followException.getCause() instanceof ParsingFailedException) {
                        dialogBuilder.setMessage(String.format(MySeriesActivity.this
                                .getString(R.string.parsing_failed_message), series.name()));
                    }
                    Dialog dialog = dialogBuilder.build();
                    dialog.show();

                } else if (e instanceof UpdateException) {
                    Log.d(getClass().getName(), "Update Failure. Cause: "
                            + e.getCause().getClass().getName());

                    FailureDialogBuilder dialogBuilder = new FailureDialogBuilder(
                            MySeriesActivity.this);

                    dialogBuilder.setTitle(R.string.update_failed_title);

                    Throwable cause = e.getCause();

                    if (cause instanceof ConnectionFailedException) {
                        dialogBuilder.setMessage(R.string.update_connection_failed);
                        Log.d(getClass().getName(), "Update: connection failure");

                    } else if (cause instanceof ConnectionTimeoutException) {
                        dialogBuilder.setMessage(R.string.update_connection_timeout);
                        Log.d(getClass().getName(), "Update: connection timeout");

                    } else if (cause instanceof ParsingFailedException) {
                        dialogBuilder.setMessage(R.string.parsing_failed_message);
                        Log.d(getClass().getName(), "Update: parsing failed");

                    } else if (cause instanceof SeriesNotFoundException) {
                        dialogBuilder.setMessage(R.string.update_series_not_found);
                        Log.d(getClass().getName(), "Update: series not found");

                    } else if (cause instanceof UpdateMetadataUnavailableException) {
                        dialogBuilder.setMessage(R.string.update_metadata_unavailable);
                        Log.d(getClass().getName(), "Update: metadata unavailable");
                    }

                    Dialog dialog = dialogBuilder.build();
                    dialog.show();
                }
            }
        };
        App.errorService().registerListener(errorListener);
    }

    private ToastBuilder toastBuilder() {
        if (toastBuilder == null) {
            toastBuilder = new ToastBuilder(App.environment().context());
        }

        return toastBuilder;
    }

    private static class StateHolder {
        Dialog dialog;
        boolean isShowingDialog;

        public StateHolder() {
        }
    }
}
