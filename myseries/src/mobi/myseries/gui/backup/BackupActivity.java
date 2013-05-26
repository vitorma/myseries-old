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
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.DropboxHelper;
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

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class BackupActivity extends Activity implements BackupListener {

    enum Operation {
        BACKUPING, RESTORING
    }

    enum Event {
        DRIVE_AUTHORIZATION, DROPBOX_AUTHORIZATION
    }

    private Spinner gDriveAccountSpinner;
    private Button gDriveBackupButton;
    private Button gDriveRestoreButton;
    private TextView SDCardLocationTextView;
    private Button SDCardBackupButton;
    private Button SDCardRestoreButton;
    private Button dropboxBackupButton;
    private Button dropboxRestoreButton;

    private GoogleAccountManager accountManager;
    private String currentAccount;

    private BackupMode currentMode;
    private Event event;
    private Operation operation;
    private MessageLauncher messageLauncher;
    private DropboxHelper dropbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.messageLauncher = new MessageLauncher(this);

        App.backupService().register(this);
        this.dropbox = App.backupService().getDropboxHelper();
        this.accountManager = new GoogleAccountManager(this);

        this.setContentView(R.layout.backup);
        this.setResult(Activity.RESULT_CANCELED);
        this.setupActionBar();
        this.setupViews();
        this.setupSdCardBackupButton();
        this.setupSDCardRestoreButton();
        this.setupGoogleDriveAccountSpinner();
        this.setupGoogleDriveBackupButton();
        this.setupGoogleDriveRestoreButton();
        this.setupDropboxBackupButton();
        this.setupDropboxRestoreButton();

    }

    private void setupActionBar() {
        ActionBar actionBar = this.getActionBar();

        actionBar.setTitle(R.string.backup_restore);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setupViews() {
        this.SDCardBackupButton = (Button) this
                .findViewById(R.id.sd_card_backup_button);
        this.SDCardRestoreButton = (Button) this
                .findViewById(R.id.sd_card_restore_button);
        this.gDriveAccountSpinner = (Spinner) this
                .findViewById(R.id.account_spinner);
        this.gDriveBackupButton = (Button) this
                .findViewById(R.id.google_drive_backup_button);
        this.gDriveRestoreButton = (Button) this
                .findViewById(R.id.google_drive_restore_button);
        this.dropboxBackupButton = (Button) this
                .findViewById(R.id.dropbox_backup_button);
        this.dropboxRestoreButton = (Button) this
                .findViewById(R.id.dropbox_restore_button);
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
                        // saveGoogleAccount(selectedAccount);
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
                BackupActivity.this.doBackup(new DriveBackup(currentAccount));
            }
        });
    }

    private void setupGoogleDriveRestoreButton() {
        this.gDriveRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupActivity.this.restoreBackup(new DriveBackup(currentAccount));
            }
        });
    }
    
    private void setupDropboxRestoreButton() {
        this.dropboxRestoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BackupActivity.this.restoreBackup(new DropboxBackup());
            }
        });
    }

    private void setupDropboxBackupButton() {
        this.dropboxBackupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                BackupActivity.this.doBackup(new DropboxBackup());
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

    @Override
    public void onBackupSucess() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onBackupFailure(Exception e) {
        if (e instanceof UserRecoverableAuthIOException) {
            this.startActivityForResult(
                    ((UserRecoverableAuthIOException) e).getIntent(),
                    Event.DRIVE_AUTHORIZATION.ordinal());
        } else if (e instanceof DropboxUnlinkedException) {
            this.linkDropboxAccount();
        } else {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Event.DRIVE_AUTHORIZATION.ordinal()) {
            if (resultCode == Activity.RESULT_OK) {
                this.resumeOperation();
            }
        }
    }

    private void linkDropboxAccount() {
        dropbox.getApi().getSession();
        AndroidAuthSession session = dropbox.getApi().getSession();
        this.event = Event.DROPBOX_AUTHORIZATION;
        session.startAuthentication(BackupActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (event == Event.DROPBOX_AUTHORIZATION) {
            dropbox.onResume();
            this.resumeOperation();
            this.event = null;
        }
    }

    private void resumeOperation() {
        if (this.operation == Operation.BACKUPING) {
            this.doBackup(currentMode);
        } else if (this.operation == Operation.RESTORING) {
            this.restoreBackup(currentMode);
        }
    }

    @Override
    public void onRestoreSucess() {
    }

    @Override
    public void onRestoreFailure(Exception e) {
        if (e instanceof UserRecoverableAuthIOException) {
            requestDropboxUserPermission(e);
        } else if (e instanceof DropboxUnlinkedException) {
            this.linkDropboxAccount();
        }
    }

    private void requestDropboxUserPermission(Exception e) {
        this.event = Event.DRIVE_AUTHORIZATION;
        this.startActivity(((UserRecoverableAuthIOException) e).getIntent());
    }

    private void doBackup(BackupMode backupMode) {
        this.currentMode = backupMode;
        this.operation = Operation.BACKUPING;
        App.backupService().doBackup(backupMode);
    }

    private void restoreBackup(BackupMode backupMode) {
        this.currentMode = backupMode;
        this.operation = Operation.RESTORING;
        App.backupService().restoreBackup(backupMode);
    }
}
