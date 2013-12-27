package mobi.myseries.gui.library;

import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BaseBackupListener;
import mobi.myseries.application.backup.DriveBackup;
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.exception.GoogleDriveException;
import mobi.myseries.application.features.product.Feature;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.addseries.AddSeriesActivity;
import mobi.myseries.gui.help.AboutActivity;
import mobi.myseries.gui.settings.SettingsActivity;
import mobi.myseries.gui.shared.ToastBuilder;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class LibraryActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, LibraryActivity.class);
    }

    private static final String ALREADY_CHECKED_FOR_UPDATE = "ALREADY_CHECKED_FOR_UPDATE";
    private boolean alreadyCheckedForUpdate = false;

    @Override
    protected void init(Bundle savedInstanceState) {
        //FIXME (Cleber) This boolean expression is always being evaluated as true. =/
        //                    savedInstanceState == null
        //                The onSaveInstanceState method isn't always called when an activity is being placed in the background.
        //
        //                From the official documentation:
        //                The onSaveInstanceState method is called before an activity may be killed so that when it comes back some time in
        //                the future it can restore its state. For example, if activity B is launched in front of activity A, and at
        //                some point activity A is killed to reclaim resources, activity A will have a chance to save the current state of
        //                its user interface via this method so that when the user returns to activity A, the state of the user interface
        //                can be restored via onCreate(Bundle) or onRestoreInstanceState(Bundle).
        if (savedInstanceState == null || !savedInstanceState.getBoolean(ALREADY_CHECKED_FOR_UPDATE, false)) {
            App.updateSeriesService().updateDataIfNeeded();
        }

        this.alreadyCheckedForUpdate = true;

        setupBackupListener();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALREADY_CHECKED_FOR_UPDATE, this.alreadyCheckedForUpdate);
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.ab_title_library);
    }

    @Override
    protected int layoutResource() {
        return R.layout.myseries;
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getText(R.string.nav_library);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.myseries, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        menu.findItem(R.id.backup_restore).setVisible(App.features().isVisible(Feature.CLOUD_BACKUP));

        if (this.isDrawerOpen()) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.add:
                this.startActivity(AddSeriesActivity.newIntent(this));
                return true;
            case R.id.remove:
                this.showRemoveDialog();
                return true;
            case R.id.filter_episodes:
                this.showEpisodeFilterDialog();
                return true;
            case R.id.filter_series:
                this.showSeriesFilterDialog();
                return true;
            case R.id.sort:
                this.showSortDialog();
                return true;
            case R.id.update:
                this.runManualUpdate();
                return true;
            case R.id.backup_restore:
                if (App.features().isVisible(Feature.CLOUD_BACKUP)) {
                    new BackupDialogFragment().show(this.getFragmentManager(), "backupDialog");
                    return true;
                }
            case R.id.settings:
                this.startActivity(SettingsActivity.newIntent(this));
                return true;
            case R.id.about:
                this.startActivity(AboutActivity.newIntent(this));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRemoveDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_remove).build().show();
        } else {
            new SeriesRemovalDialogFragment().show(this.getFragmentManager(), "removalDialog");
        }
    }

    private void showSeriesFilterDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_filter).build().show();
        } else {
            new SeriesFilterDialogFragment().show(this.getFragmentManager(), "seriesFilterDialog");
        }
    }

    private void showEpisodeFilterDialog() {
        new EpisodeFilterDialogFragment().show(this.getFragmentManager(), "episodeFilterDialog");
    }

    private void showSortDialog() {
        new SeriesSortingDialogFragment().show(this.getFragmentManager(), "seriesSortingDialog");
    }

    private void runManualUpdate() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_update).build().show();
        } else {
            App.updateSeriesService().updateData();
        }
    }

    private static final int DRIVE_BACKUP = 1;
    private static final int DRIVE_RESTORE = 2;
    private static final int DROPBOX_BACKUP = 3;
    private static final int DROPBOX_RESTORE = 4;
    private BackupListener backupListener;
    private int pendingOperation;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DRIVE_BACKUP && resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            App.backupService().doBackup(new DriveBackup(accountName));
      }
        if (requestCode == DRIVE_RESTORE && resultCode == Activity.RESULT_OK) {
            String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            new RestoreProgressDialogFragment().show(getFragmentManager(), "RestoreProgressDialog");
            App.backupService().restoreBackup(new DriveBackup(accountName));
      }
    }

    private void setupBackupListener() {
        this.backupListener = new BaseBackupListener() {
            @Override
            public void onBackupFailure(BackupMode mode, Exception e) {
                super.onBackupFailure(mode, e);
                if (e instanceof GoogleDriveException 
                    && (e.getCause() instanceof UserRecoverableAuthIOException)) {
                    requesUserPermissionToDrive((UserRecoverableAuthIOException) e.getCause(), DRIVE_BACKUP);
                } else if (e instanceof DropboxUnlinkedException) {
                    linkDropboxAccount(DROPBOX_BACKUP);
                }
            }

            @Override
            public void onRestoreFailure(BackupMode mode, Exception e) {
                super.onRestoreFailure(mode, e);
                if (e instanceof GoogleDriveException 
                        && (e.getCause() instanceof UserRecoverableAuthIOException)) {
                        requesUserPermissionToDrive((UserRecoverableAuthIOException) e.getCause(), DRIVE_RESTORE);
                    } else if (e instanceof DropboxUnlinkedException) {
                        linkDropboxAccount(DROPBOX_RESTORE);
                    }
            }
        };
        App.backupService().register(backupListener);
    }

    private void requesUserPermissionToDrive(UserRecoverableAuthIOException e, int operationCode) {
        startActivityForResult(
                e.getIntent(),
                operationCode);
    }

    private void linkDropboxAccount(int operationCode) {
        this.pendingOperation = operationCode;
        App.backupService().dropboxHelper().getApi().getSession().startAuthentication(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pendingOperation == DROPBOX_BACKUP) {
            boolean resumeSucess = App.backupService().dropboxHelper().onResume();
            if (resumeSucess) {
                pendingOperation = 0;
                App.backupService().doBackup(new DropboxBackup());
            }
        }
        if (pendingOperation == DROPBOX_RESTORE) {
            boolean resumeSucess = App.backupService().dropboxHelper().onResume();
            if (resumeSucess) {
                pendingOperation = 0;
                App.backupService().restoreBackup(new DropboxBackup());
            }
        }
    }
}
