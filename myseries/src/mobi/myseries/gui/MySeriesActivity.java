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

package mobi.myseries.gui;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.UpdateListener;
import mobi.myseries.gui.schedule.MyScheduleActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class MySeriesActivity extends SherlockFragmentActivity implements UpdateListener {
    //TODO Menu from xml
    private static final String SCHEDULE = "SCHEDULE";
    private static final String SEARCH = "SEARCH";
    private static final String UPDATE = "UPDATE";
    private static final String SETTINGS = "SETTINGS";
    private static final String HELP = "HELP";

    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();

    private SeriesListFragment seriesListFragment;
    private SeriesCoverFlowFragment seriesCoverFlowFragment;

    public MySeriesActivity() {
        seriesProvider.addListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.my_series_layout);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.my_series);

        if (savedInstanceState == null) {
            this.seriesListFragment = new SeriesListFragment();
            this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.series_list_fragment, this.seriesListFragment);

            this.seriesCoverFlowFragment = new SeriesCoverFlowFragment();
            this.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.series_coverflow_fragment, this.seriesCoverFlowFragment);
        }
    }

    //Menu--------------------------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(SCHEDULE)
            .setIntent(new Intent(this, MyScheduleActivity.class))
            .setIcon(R.drawable.actionbar_calendar)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(SEARCH)
            .setIcon(R.drawable.actionbar_search)
            .setIntent(new Intent(this, SeriesSearchActivity.class))
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(UPDATE)
            .setIcon(R.drawable.actionbar_update)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        
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
            this.updateMenuItem = item;
            seriesProvider.updateData();
        }
        
        return super.onMenuItemSelected(featureId, item);
    }
    

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addSeriesMenuItem:
//                this.showSearchActivity();
//                return true;
//            case R.id.updateMenuItem:
//                seriesProvider.updateData();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    //Update------------------------------------------------------------------------------------------------------------
    
    //private UpdateNotificationLauncher updateNotificationLauncher;
    private MenuItem updateMenuItem;
    
    @Override
    public void onUpdateStart() {        
        ImageView spinner = new ImageView(this);
        spinner.setImageResource(R.drawable.actionbar_spinner);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.clockwise_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        this.updateMenuItem.setActionView(spinner);
        spinner.startAnimation(rotation);
    }
    
    @Override
    public void onUpdateFailure() {
        Toast.makeText(this, R.string.update_failure_notification_message, 5).show();
        this.updateMenuItem.getActionView().setAnimation(null);
        this.updateMenuItem.setActionView(null);
    }
    
    @Override
    public void onUpdateSuccess() {
        this.updateMenuItem.getActionView().setAnimation(null);
        this.updateMenuItem.setActionView(null);
    }
    
    //Search------------------------------------------------------------------------------------------------------------

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchActivity.class);
        this.startActivity(intent);
    }
}
