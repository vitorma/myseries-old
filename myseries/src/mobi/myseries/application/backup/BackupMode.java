package mobi.myseries.application.backup;

import java.io.File;

public interface BackupMode {
    void downloadBackupToFile(File backup) throws Exception;
    void backupDB(File backup) throws Exception;
}
