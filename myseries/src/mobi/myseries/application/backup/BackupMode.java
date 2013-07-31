package mobi.myseries.application.backup;

import java.io.File;

public interface BackupMode {
    public void downloadBackupToFile(File backup) throws Exception;
    public void backupDB(File backup) throws Exception;
    public String name();
}
