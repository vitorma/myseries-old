package mobi.myseries.gui.settings.backup;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupService;
import mobi.myseries.application.backup.SdcardBackup;
import mobi.myseries.gui.shared.MessageLauncher;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;

public class BackupActivity extends SherlockActivity {
    private static final BackupService BACKUP_SERVICE = App.backupService(); 
    private TextView description;
    private TextView backupPath;
    private Button backupButton;
    private Button restoreButton;
    private MessageLauncher messageLauncher;
    private StateHolder state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);
        setupActionBar();
        setupViews();
        setupBackupPathTV();
        setupButtons();

        Object retained = getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            state = (StateHolder) retained;
        } else {
            state = new StateHolder();
            this.setupMessageLauncher();
        }
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        
        actionBar.setTitle(R.string.backup_restore);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
    }

    private void setupViews() {
        this.description = (TextView) this.findViewById(R.id.backupDescription);
        this.backupPath = (TextView) this.findViewById(R.id.backup_file_path);
        this.backupButton = (Button) this.findViewById(R.id.backupButton);
        this.restoreButton = (Button) this.findViewById(R.id.restoreButton);
    }

    private void setupBackupPathTV() {
        this.backupPath.setText(String.format(this.getString(R.string.backup_folder_file_path), BACKUP_SERVICE.sdCardPath()));
        
    }

    private void setupButtons() {
        this.backupButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BACKUP_SERVICE.doBackup();
            }
        });
        
        this.restoreButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BACKUP_SERVICE.restoreBackup();
                
            }
        });
    }
    
    private void setupMessageLauncher() {
        this.messageLauncher = new MessageLauncher(this);
        state.messageLauncher = this.messageLauncher;
    }

    private void loadState() {
        this.messageLauncher = state.messageLauncher;
        this.messageLauncher.loadState();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return state;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.messageLauncher.onStop();
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
    
    private static class StateHolder {
        MessageLauncher messageLauncher;
    }

}
