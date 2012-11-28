package mobi.myseries.gui.settings.backup;

import mobi.myseries.R;
import mobi.myseries.R.id;
import mobi.myseries.R.layout;
import mobi.myseries.R.string;
import mobi.myseries.application.backup.sdcardBackup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.Button;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;

public class BackupActivity extends SherlockActivity {

    private TextView description;
    private TextView backupPath;
    private Button backupButton;
    private Button restoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup);
        setupActionBar();
        setupViews();
        setupBackupPathTV();
    }

    private void setupBackupPathTV() {
        this.backupPath.setText(String.format(this.getString(R.string.backup_folder_file_path), sdcardBackup.path()));
        
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

}
