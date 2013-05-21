package mobi.myseries.application.backup;

import java.io.File;

import android.content.SharedPreferences;

public interface BackupMode {
    
    File getBackup() throws Exception;
    void backupDB(File backup) throws Exception;
    void backupPreferences(SharedPreferences preferences)
            throws Exception;

}
