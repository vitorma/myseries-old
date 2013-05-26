package mobi.myseries.application.backup;

import java.io.File;

public interface BackupMode {
    File getBackup() throws Exception;
    void backupDB(File backup) throws Exception;
}
