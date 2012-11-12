/*
 *   SettingsActivity.java
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

package mobi.myseries.gui.settings;

import mobi.myseries.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockActivity {
    public static final String PREFERENCES_NAME = "mobi.myseries.gui.settings.MySeriesPreferences";
    public static final String ENABLE_AUTOMATIC_UPDATES_KEY = "automaticUpdateEnabled_";
    public static final String ENABLED_UPDATES_ON_DATAPLAN_KEY = "updateOnDataPlanEnabled_";
    private RadioGroup automaticUpdatesRadioGroup;
    private Button cancelButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.settings);
        this.setResult(Activity.RESULT_CANCELED);
        this.setupActionBar();
        this.setupViews();
        this.loadSettings();
        this.setUpCancelButton();
        this.setUpSaveButton();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        actionBar.setTitle(R.string.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setupViews() {
        this.automaticUpdatesRadioGroup =
                (RadioGroup) this.findViewById(R.id.automaticUpdatesRadioGroup);
    }

    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (!settings.getBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, true)) {
            this.automaticUpdatesRadioGroup.check(R.id.doNotUpdateRadioButton);

        } else if (settings.getBoolean(ENABLED_UPDATES_ON_DATAPLAN_KEY, false)) {
            this.automaticUpdatesRadioGroup.check(R.id.wifiOrDataPlanRadioButton);

        } else {
            this.automaticUpdatesRadioGroup.check(R.id.wifiOnlyRadioButton);

        }
    }

    private void setUpCancelButton() {
        this.cancelButton = (Button) this.findViewById(R.id.cancelButton);
        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();

            }
        });
    }

    private void setUpSaveButton() {
        this.saveButton = (Button) this.findViewById(R.id.saveButton);

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.save();

            }
        });
    }

    protected void save() {
        this.saveSettings();
        this.finishOk();
    }

    private void saveSettings() {

        SharedPreferences settings =
                this.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);

        switch (this.automaticUpdatesRadioGroup.getCheckedRadioButtonId()) {
        case R.id.doNotUpdateRadioButton:
            settings
                    .edit()
                    .putBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, false)
                    .commit();

            break;

        case R.id.wifiOnlyRadioButton:
            settings
                    .edit()
                    .putBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, true)
                    .putBoolean(ENABLED_UPDATES_ON_DATAPLAN_KEY, false)
                    .commit();
            break;
        case R.id.wifiOrDataPlanRadioButton:
            settings
                    .edit()
                    .putBoolean(ENABLE_AUTOMATIC_UPDATES_KEY, true)
                    .putBoolean(ENABLED_UPDATES_ON_DATAPLAN_KEY, true)
                    .commit();
            break;
        }
    }

    private void finishOk() {
        this.setResult(Activity.RESULT_OK);
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);

        return intent;
    }
}
