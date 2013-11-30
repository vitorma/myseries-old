package mobi.myseries.gui.backup;

import java.io.File;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.backup.BaseBackupListener;
import mobi.myseries.application.backup.DriveBackup;
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.application.backup.exception.GoogleDriveException;
import mobi.myseries.application.notification.DeterminateProgressNotification;
import mobi.myseries.application.notification.IndeterminateProgressNotification;
import mobi.myseries.application.notification.Notification;
import mobi.myseries.application.notification.NotificationDispatcher;
import mobi.myseries.application.notification.TextOnlyNotification;
import mobi.myseries.gui.episodes.EpisodeSortingDialogFragment;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class RestoreFragment extends Fragment {

    enum CloudBackupType { DRIVE, DROPBOX}
    private Button restoreButton;
    private GoogleAccountManager accountManager;
    private Spinner gDriveAccountSpinner;
    private DropboxHelper dropboxHelper = App.backupService().dropboxHelper();
    private String account;
    
    private ProgressBar restoreProgressBar;
    private TextView restoreStatusTextView;
    
    private BackupListener restoreListener;
    private CloudBackupType pendingRestore;
    private BackupService backupService;
    private RadioGroup restoreModeRadioGroup;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.restore_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.backupService = App.backupService();
        this.accountManager = new GoogleAccountManager(getActivity());
        this.setupViews();
        this.setupRestoreListener();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        if(pendingRestore == CloudBackupType.DROPBOX) {
            boolean resumeSucess = dropboxHelper.onResume();
            if(resumeSucess) {
                pendingRestore = null;
                performDropboxRestore();
            }
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CloudBackupType.DRIVE.ordinal() && resultCode == Activity.RESULT_OK) {
            performGoogleDriveRestore();
        }
    }

    private void setupViews() {
        this.setupRadioButtonGroup();
        this.setupRestoreButton();
        this.setupGoogleDriveAccountSpinner();
        this.setupProgressBar();
    }
    
    private void setupProgressBar() {
        this.restoreProgressBar =
                (ProgressBar) this.findView(R.id.RestoreProgressBar);
        this.restoreStatusTextView =
                (TextView) this.findView(R.id.RestoreStatusMessage);
    }

    private void setupRadioButtonGroup() {
        this.restoreModeRadioGroup = (RadioGroup) this.findView(R.id.RestoreModeRadioGroup);
        
    }

    private void setupRestoreButton() {
        this.restoreButton = (Button) this.findView(R.id.RestoreButton);
        this.restoreButton.setOnClickListener(onClickListener);
    }

    private void setupGoogleDriveAccountSpinner() {
        gDriveAccountSpinner = (Spinner) this.findView(R.id.GoogleAccountSpinner);
        ArrayAdapter<String> spinnerAccountAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        for (Account a : this.accountManager.getAccounts()) {
            spinnerAccountAdapter.add(a.name);
        }
        this.gDriveAccountSpinner.setAdapter(spinnerAccountAdapter);
        this.gDriveAccountSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                account = (String) arg0.getItemAtPosition(arg2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
        });
    }

    private void setupRestoreListener() {
        this.restoreListener = new BaseBackupListener() {
            @Override
            public void onRestoreFailure(BackupMode mode, Exception e) {
                super.onRestoreFailure(mode, e);
                if (e instanceof GoogleDriveException 
                    && (e.getCause() instanceof UserRecoverableAuthIOException)) {
                    requesUserPermissionToDrive((UserRecoverableAuthIOException) e.getCause());
                } else if (e instanceof DropboxUnlinkedException) {
                    linkDropboxAccount();
                }
                restoreButton.setText(R.string.restore);
                restoreButton.setOnClickListener(onClickListener);
            }
            @Override
            public void onRestoreCompleted(BackupMode mode) {
                super.onRestoreCompleted(mode);
                restoreButton.setText(R.string.restore);
                restoreButton.setOnClickListener(onClickListener);
            }

            @Override
            public void onRestoreRunning(BackupMode mode) {
                super.onRestoreRunning(mode);
                restoreButton.setText(R.string.cancel);
                restoreButton.setOnClickListener(runningOnClickListener);
            }
            
            @Override
            public void onRestoreCancelled() {
                super.onRestoreCancelled();
                restoreButton.setText(R.string.restore);
                restoreButton.setOnClickListener(onClickListener);
                restoreProgressBar.setIndeterminate(false);
                restoreProgressBar.setMax(0);
                restoreProgressBar.setProgress(0);
                restoreStatusTextView.setText(R.string.restore_cancelled);
            }
            @Override
            public void onRestoreProgress(int current, int total) {
                super.onRestoreProgress(current, total);
                restoreProgressBar.setIndeterminate(false);
                restoreProgressBar.setMax(total);
                restoreProgressBar.setProgress(current);
                restoreStatusTextView.setText(R.string.restore_downloading_series_message);
            }
            @Override
            public void onRestorePosterDownloadProgress(int current, int total) {
                super.onRestorePosterDownloadProgress(current, total);
                restoreProgressBar.setIndeterminate(false);
                restoreProgressBar.setMax(total);
                restoreProgressBar.setProgress(current);
                restoreStatusTextView.setText(R.string.restore_downloading_posters_message);
            }
        };
        App.backupService().register(restoreListener);
    }

    private View findView(int resourceId) {
        return this.getView().findViewById(resourceId);
    }
    
    private void performRestore(final BackupMode backupMode){
                backupService.restoreBackup(backupMode);
    }

    private void performGoogleDriveRestore() {
        performRestore(new DriveBackup(account));
    }

    private void performDropboxRestore() {
        performRestore(new DropboxBackup());
    }
    
    private void performSDCardRestore() {
        new FileChooserDialogFragment().show(this.getFragmentManager(), "FileChooserDialogFragment");
    }

    private void linkDropboxAccount() {
        this.pendingRestore = CloudBackupType.DROPBOX;
        dropboxHelper.getApi().getSession();
        AndroidAuthSession session = dropboxHelper.getApi().getSession();
        session.startAuthentication(this.getActivity());
    }

    private void requesUserPermissionToDrive(Exception e) {
        this.pendingRestore = CloudBackupType.DRIVE;
        startActivityForResult(
                ((UserRecoverableAuthIOException) e).getIntent(),
                CloudBackupType.DRIVE.ordinal());
    }
    
    private final NotificationDispatcher restoreNotificationDispatcher = new NotificationDispatcher() {

        @Override
        public void notifyTextOnlyNotification(TextOnlyNotification notification) {
            restoreProgressBar.setIndeterminate(false);
            restoreProgressBar.setMax(0);
            restoreProgressBar.setProgress(0);

            restoreStatusTextView.setText(notification.message());
        }

        @Override
        public void notifyIndeterminateProgressNotification(
                IndeterminateProgressNotification notification) {
            restoreProgressBar.setIndeterminate(true);
            restoreStatusTextView.setText(notification.message());
        }

 
        @Override
        public void cancel(Notification notification) {
            restoreProgressBar.setIndeterminate(false);
            restoreProgressBar.setMax(0);
            restoreProgressBar.setProgress(0);
            restoreStatusTextView.setText("");
        }

        @Override
        public void notifyDeterminateProgressNotification(
                DeterminateProgressNotification notification) {
            // TODO Auto-generated method stub
            
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        App.notificationService().setRestoreNotificationDispatcher(this.restoreNotificationDispatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.notificationService().removeRestoreNotificationDispatcher(this.restoreNotificationDispatcher);
    }

    private OnClickListener runningOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            App.backupService().cancelCurrentRestore();
        }
    };
    
    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (restoreModeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.SDCardRadioButton:
                performSDCardRestore();
                break;
            case R.id.GoogleDriveRadioButton:
                performGoogleDriveRestore();
                break;
            case R.id.DropboxRadioButton:
                performDropboxRestore();
                break;
            }
        }
    };
}
