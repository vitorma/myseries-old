package mobi.myseries.gui.backup;

import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.DropboxBackup;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
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
import android.widget.Spinner;

public class BackupFragment extends Fragment {
    private Button backupButton;
    private CheckedTextView dropboxCheckbox;
    private CheckedTextView googleDriveCheckbox;
    private CheckedTextView sDcardCheckbox;
    private GoogleAccountManager accountManager;
    private Spinner gDriveAccountSpinner;

    private String account;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
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
                if(dropboxCheckbox.isChecked()) {
                    performDropboxBackup();
                }
                
            }
        });
    }

    private void setupDropboxCheckbox() {
        this.dropboxCheckbox = (CheckedTextView) this.findView(R.id.DropboxCheckbox);
        this.dropboxCheckbox.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dropboxCheckbox.toggle();
            }
        });
        
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
    private void setupGoogleDriveCheckbox() {
        this.googleDriveCheckbox = (CheckedTextView) this.findView(R.id.GoogleDriveCheckbox);
        this.googleDriveCheckbox.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                googleDriveCheckbox.toggle();
            }
        });
    }

    private void setupSDCardCheckbox() {
        this.sDcardCheckbox = (CheckedTextView) this.findView(R.id.SDCardCheckbox);
        this.sDcardCheckbox.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                sDcardCheckbox.toggle();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.backup_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.accountManager = new GoogleAccountManager(getActivity());
        this.setupViews();
    }

    private View findView(int resourceId) {
        return this.getView().findViewById(resourceId);
    }
    

    private void performDropboxBackup() {
    }

}
