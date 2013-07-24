package mobi.myseries.application.backup;

import java.io.File;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupMode;

public class DropboxBackup implements BackupMode {
    final static private String DEFAULT_PATH = "";
    private DropboxHelper dropboxHelper;
    
    public DropboxBackup() {
        this.dropboxHelper = App.backupService().getDropboxHelper();
    }
    @Override
    public void downloadBackupToFile(File backup) throws Exception {
        this.dropboxHelper.downloadFile(backup.getName(), backup);
    }

    @Override
    public void backupDB(File backup) throws Exception {
        this.dropboxHelper.uploadFile(backup, DEFAULT_PATH + backup.getName());
    }
}
