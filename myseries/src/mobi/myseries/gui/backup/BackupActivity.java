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
import mobi.myseries.application.backup.BaseBackupListener;
import mobi.myseries.application.backup.DriveBackup;
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.TabDefinition;
import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

public class BackupActivity extends TabActivity{

    private static final int BACKUP_TAB = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    protected void init(Bundle savedInstanceState) { /* There's nothing to initialize */ }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.backup_restore);
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.backup, new BackupFragment()),
            new TabDefinition(R.string.restore, new RestoreFragment())
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return BACKUP_TAB;
    }
}
