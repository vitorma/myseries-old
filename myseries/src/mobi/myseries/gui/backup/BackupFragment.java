package mobi.myseries.gui.backup;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.backup.DriveBackup;
import mobi.myseries.application.backup.DropboxBackup;
import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.backup.SdcardBackup;
import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.Spinner;

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
    private DropboxHelper dropboxHelper = App.backupService()
            .getDropboxHelper();
    private String account;
    private BackupServiceListener backupListener;
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
        this.backupService = App.backupService().withHandler(new Handler());
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
                performDropboxBackup();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CloudBackupType.DRIVE.ordinal()
                && resultCode == Activity.RESULT_OK) {
            performGoogleDriveBackup();
        }
    }

    private void setupViews() {
        this.setupSDCardCheckbox();
        this.setupGoogleDriveCheckbox();
        this.setupDropboxCheckbox();
        this.setupBackupButton();
        this.setupGoogleDriveAccountSpinner();

    }

    private void setupBackupButton() {
        this.backupButton = (Button) this.findView(R.id.BackupButton);
        this.backupButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (dropboxCheckbox.isChecked()) {
                    performDropboxBackup();
                }
                if (googleDriveCheckbox.isChecked()) {
                    performGoogleDriveBackup();
                }
                if (sDcardCheckbox.isChecked()) {
                    performSDCardBackup();
                }

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
        this.backupListener = new BackupServiceListener() {
            @Override
            public void onBackupFailure(Exception e) {
                super.onBackupFailure(e);
                if (e instanceof UserRecoverableAuthIOException) {
                    requesUserPermissionToDrive(e);
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

    private void performBackup(final BackupMode backupMode) {
        backupService.doBackup(backupMode);
    }

    private void performGoogleDriveBackup() {
        performBackup(new DriveBackup(account));
    }

    private void performDropboxBackup() {
        performBackup(new DropboxBackup());
    }

    private void performSDCardBackup() {
        performBackup(new SdcardBackup());
    }

    private void linkDropboxAccount() {
        this.pendingBackup = CloudBackupType.DROPBOX;
        dropboxHelper.getApi().getSession();
        AndroidAuthSession session = dropboxHelper.getApi().getSession();
        session.startAuthentication(this.getActivity());
    }

    private void requesUserPermissionToDrive(Exception e) {
        this.pendingBackup = CloudBackupType.DRIVE;
        startActivityForResult(
                ((UserRecoverableAuthIOException) e).getIntent(),
                CloudBackupType.DRIVE.ordinal());
    }

}
