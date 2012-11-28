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
import mobi.myseries.application.SettingsProvider;
import mobi.myseries.gui.settings.backup.BackupActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class SettingsActivity extends SherlockActivity {
    private RadioGroup automaticUpdatesRadioGroup;
    private Button cancelButton;
    private Button saveButton;
    private TextView backupTextView;

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
        this.setupBackupTVListener();
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
        this.backupTextView = (TextView) this.findViewById(R.id.backupRestoreText);
    }

    private void setupBackupTVListener() {
        OnClickListener cl = new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showBackupActivity();
            }
        };
        this.backupTextView.setOnClickListener(cl);
        
    }

    private void showBackupActivity() {
        this.startActivity(BackupActivity.newIntent(this));
        
    }

    private void loadSettings() {
        SettingsProvider settings = settingsProviderFor(this);

        if (!settings.updateAutomatically()) {
            this.automaticUpdatesRadioGroup.check(R.id.doNotUpdateRadioButton);

        } else if (settings.updateOnDataPlan()) {
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
        switch (this.automaticUpdatesRadioGroup.getCheckedRadioButtonId()) {
        case R.id.doNotUpdateRadioButton:
            settingsProviderFor(this).putUpdateAutomatically(false);
            break;

        case R.id.wifiOnlyRadioButton:
            settingsProviderFor(this).putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(false);

            break;

        case R.id.wifiOrDataPlanRadioButton:
            settingsProviderFor(this).putUpdateAutomatically(true)
                    .putUpdateOnDataPlan(true);

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

    private SettingsProvider settingsProviderFor(Context context) {
        return new SettingsProvider(context);
    }
}
