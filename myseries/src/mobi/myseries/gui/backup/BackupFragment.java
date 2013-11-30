package mobi.myseries.gui.backup;

import java.util.Collection;
import java.util.LinkedList;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

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
import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

public class BackupFragment extends Fragment {

    enum CloudBackupType {
        DRIVE, DROPBOX
    }

    private Button backupButton;
    private CheckedTextView dropboxCheckbox;
    private CheckedTextView googleDriveCheckbox;
    private CheckedTextView sDcardCheckbox;
    private GoogleAccountManager accountManager;
    private Spinner gDriveAccountSpinner;

    private ProgressBar backupProgressBar;
    private TextView backupStatusTextView;

    private DropboxHelper dropboxHelper = App.backupService()
            .dropboxHelper();
    private String account;
    private BackupListener backupListener;
    private CloudBackupType pendingBackup;
    private BackupService backupService;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.backup_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.backupService = App.backupService();
        this.accountManager = new GoogleAccountManager(getActivity());
        this.setupViews();
        this.setupBackupListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (pendingBackup == CloudBackupType.DROPBOX) {
            boolean resumeSucess = dropboxHelper.onResume();
            if (resumeSucess) {
                pendingBackup = null;
                backupService.doBackup(new DropboxBackup());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CloudBackupType.DRIVE.ordinal()
                && resultCode == Activity.RESULT_OK) {
            backupService.doBackup(new DriveBackup(account));
        }
    }

    private void setupViews() {
        this.setupSDCardCheckbox();
        this.setupGoogleDriveCheckbox();
        this.setupDropboxCheckbox();
        this.setupBackupButton();
        this.setupGoogleDriveAccountSpinner();
        this.setupProgressBar();

    }

    private void setupProgressBar() {
        this.backupProgressBar =
                (ProgressBar) this.findView(R.id.BackupProgressBar);
        this.backupStatusTextView =
                (TextView) this.findView(R.id.BackupStatusMessage);
        
    }

    private void setupBackupButton() {
        this.backupButton = (Button) this.findView(R.id.BackupButton);
        this.backupButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Collection<BackupMode> backups = new LinkedList<BackupMode>();
                if (sDcardCheckbox.isChecked()) {
                    backups.add(new SdcardBackup());
                }
                if (dropboxCheckbox.isChecked()) {
                    backups.add(new DropboxBackup());
                }
                if (googleDriveCheckbox.isChecked()) {
                    backups.add(new DriveBackup(account));
                }
                backupService.doBackup(backups);

            }
        });
    }

    private void setupDropboxCheckbox() {
        this.dropboxCheckbox = (CheckedTextView) this
                .findView(R.id.DropboxCheckbox);
        this.dropboxCheckbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dropboxCheckbox.toggle();
            }
        });

    }

    private void setupGoogleDriveAccountSpinner() {
        gDriveAccountSpinner = (Spinner) this
                .findView(R.id.GoogleAccountSpinner);
        ArrayAdapter<String> spinnerAccountAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_item);
        for (Account a : this.accountManager.getAccounts()) {
            spinnerAccountAdapter.add(a.name);
        }
        this.gDriveAccountSpinner.setAdapter(spinnerAccountAdapter);
        this.gDriveAccountSpinner
                .setOnItemSelectedListener(new OnItemSelectedListener() {

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

    private void setupGoogleDriveCheckbox() {
        this.googleDriveCheckbox = (CheckedTextView) this
                .findView(R.id.GoogleDriveCheckbox);
        this.googleDriveCheckbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                googleDriveCheckbox.toggle();
            }
        });
    }

    private void setupSDCardCheckbox() {
        this.sDcardCheckbox = (CheckedTextView) this
                .findView(R.id.SDCardCheckbox);
        this.sDcardCheckbox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sDcardCheckbox.toggle();
            }
        });
    }

    private void setupBackupListener() {
        this.backupListener = new BaseBackupListener() {
            @Override
            public void onBackupFailure(BackupMode mode, Exception e) {
                super.onBackupFailure(mode, e);
                if (e instanceof GoogleDriveException 
                    && (e.getCause() instanceof UserRecoverableAuthIOException)) {
                    requesUserPermissionToDrive((UserRecoverableAuthIOException) e.getCause());
                } else if (e instanceof DropboxUnlinkedException) {
                    linkDropboxAccount();
                }
            }
        };
        App.backupService().register(backupListener);
    }

    private View findView(int resourceId) {
        return this.getView().findViewById(resourceId);
    }

    private void linkDropboxAccount() {
        this.pendingBackup = CloudBackupType.DROPBOX;
        dropboxHelper.getApi().getSession();
        AndroidAuthSession session = dropboxHelper.getApi().getSession();
        session.startAuthentication(this.getActivity());
    }

    private void requesUserPermissionToDrive(UserRecoverableAuthIOException e) {
        this.pendingBackup = CloudBackupType.DRIVE;
        startActivityForResult(
                e.getIntent(),
                CloudBackupType.DRIVE.ordinal());
    }
    
    private final NotificationDispatcher backupNotificationDispatcher = new NotificationDispatcher() {

        @Override
        public void notifyTextOnlyNotification(TextOnlyNotification notification) {
            backupProgressBar.setIndeterminate(false);
            backupProgressBar.setMax(0);
            backupProgressBar.setProgress(0);

            backupStatusTextView.setText(notification.message());
        }

        @Override
        public void notifyIndeterminateProgressNotification(
                IndeterminateProgressNotification notification) {
            backupProgressBar.setIndeterminate(true);
            backupStatusTextView.setText(notification.message());
        }

 
        @Override
        public void cancel(Notification notification) {
            backupProgressBar.setIndeterminate(false);
            backupProgressBar.setMax(0);
            backupProgressBar.setProgress(0);
            backupStatusTextView.setText("");
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
        App.notificationService().setBackupNotificationDispatcher(this.backupNotificationDispatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
        App.notificationService().removeBackupNotificationDispatcher(this.backupNotificationDispatcher);
    }
}
