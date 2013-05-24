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

package mobi.myseries.gui.backup;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.DriveBackup;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.shared.MessageLauncher;
import android.accounts.Account;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class BackupActivity extends Activity implements BackupListener {

    private Spinner gDriveAccountSpinner;
    private Button gDriveBackupButton;
    private Button gDriveRestoreButton;
    private TextView SDCardLocationTextView;
    private Button SDCardBackupButton;
    private Button SDCardRestoreButton;

    private GoogleAccountManager accountManager;
    private String currentAccount;

    private BackupMode currentMode;
    private int STATE;
    private MessageLauncher messageLauncher;

    private static final int STATE_RESTORING = 0;
    private static final int STATE_BACKUPING = 1;

    private static final int REQUEST_AUTHORIZATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.messageLauncher = new MessageLauncher(this);

        App.backupService().register(this);
        this.accountManager = new GoogleAccountManager(this);

        this.setContentView(R.layout.backup);
        this.setResult(Activity.RESULT_CANCELED);
        this.setupActionBar();
        this.setupViews();
        this.setUpSDCardLocationTextView();
        this.setupSdCardBackupButton();
        this.setupSDCardRestoreButton();
        this.setupGoogleDriveAccountSpinner();
        this.setupGoogleDriveBackupButton();
        this.setupGoogleDriveRestoreButton();

    }


    private void setupActionBar() {
        ActionBar actionBar = this.getActionBar();

        actionBar.setTitle(R.string.backup_restore);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setupViews() {
        this.SDCardLocationTextView = (TextView) this.findViewById(R.id.sd_card_location_text_view);
        this.SDCardBackupButton = (Button) this.findViewById(R.id.sd_card_backup_button);
        this.SDCardRestoreButton = (Button) this.findViewById(R.id.sd_card_restore_button);
        this.gDriveAccountSpinner = (Spinner) this
                .findViewById(R.id.account_spinner);
        this.gDriveBackupButton = (Button) this
                .findViewById(R.id.google_drive_backup_button);
        this.gDriveRestoreButton = (Button) this
                .findViewById(R.id.google_drive_restore_button);
    }

    private void setupSDCardRestoreButton() {
        this.SDCardRestoreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupActivity.this.restoreBackup(new SdcardBackup());
            }
        });

    }

    private void setupSdCardBackupButton() {
        this.SDCardBackupButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BackupActivity.this.doBackup(new SdcardBackup());
            }
        });

    }

    private void setUpSDCardLocationTextView() {
        this.SDCardLocationTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupGoogleDriveAccountSpinner() {
        ArrayAdapter<String> spinnerAccountAdapter = new ArrayAdapter<String>(
                this, R.layout.sherlock_spinner_item);
        for (Account a : this.accountManager.getAccounts()) {
            spinnerAccountAdapter.add(a.name);
        }
        this.gDriveAccountSpinner.setAdapter(spinnerAccountAdapter);

        this.gDriveAccountSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        String selectedAccount = (String) arg0
                                .getItemAtPosition(arg2);
                        //saveGoogleAccount(selectedAccount);
                        BackupActivity.this.currentAccount = selectedAccount;

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void setupGoogleDriveBackupButton() {
        this.gDriveBackupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BackupActivity.this.doBackup(new DriveBackup(BackupActivity.this.currentAccount));
            }
        });
    }

    private void setupGoogleDriveRestoreButton() {
        this.gDriveRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupActivity.this.restoreBackup(new DriveBackup(BackupActivity.this.currentAccount));
            }
        });
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
        Intent intent = new Intent(context, BackupActivity.class);

        return intent;
    }

//    private void saveGoogleAccount(String selectedAccount) {
//        backupSettingsProviderFor(this).putGoogleDriveAccount(selectedAccount);
//    }
//
//    private Account getPreferenceAccount() {
//        return accountManager.getAccountByName(backupSettingsProviderFor(this)
//                .googleDriveAccount());
//    }
//
//    private BackupPreferences backupSettingsProviderFor(Context context) {
//        return new BackupPreferences(context);
//    }

    @Override
    public void onBackupSucess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackupFailure(Exception e) {
        if (e instanceof UserRecoverableAuthIOException) {
            this.startActivityForResult(((UserRecoverableAuthIOException) e).getIntent(), REQUEST_AUTHORIZATION);
        } else {
            e.printStackTrace();
        }

    }

    @Override
    public void onRestoreSucess() {


    }

    @Override
    public void onRestoreFailure(Exception e) {
        if (e instanceof UserRecoverableAuthIOException) {
            this.startActivityForResult(((UserRecoverableAuthIOException) e).getIntent(), REQUEST_AUTHORIZATION);
        }
        e.printStackTrace();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        case REQUEST_AUTHORIZATION:
            if (resultCode == Activity.RESULT_OK) {
                if(this.STATE == STATE_BACKUPING) {
                    this.doBackup(this.currentMode);
                }
                if(this.STATE == STATE_RESTORING) {
                    this.restoreBackup(this.currentMode);
                }
            }
        }
    }

    private void doBackup(BackupMode backupMode) {
        this.currentMode = backupMode;
        this.STATE = STATE_BACKUPING;
        App.backupService().doBackup(backupMode);
    }

    private void restoreBackup(BackupMode backupMode) {
        this.currentMode = backupMode;
        this.STATE = STATE_RESTORING;
        App.backupService().restoreBackup(backupMode);
    }
}
