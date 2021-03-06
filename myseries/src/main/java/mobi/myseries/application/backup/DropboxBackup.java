package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileNotFoundException;

import com.dropbox.client2.exception.DropboxException;

import mobi.myseries.application.App;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.backup.BackupMode;

public class DropboxBackup implements BackupMode {
    final static private String DEFAULT_PATH = "";
    private DropboxHelper dropboxHelper;
    
    public DropboxBackup() {
        this.dropboxHelper = App.backupService().dropboxHelper();
    }
    @Override
    public void downloadBackupToFile(File backup) throws FileNotFoundException, DropboxException, ConnectionFailedException {
        this.dropboxHelper.downloadFile(backup.getName(), backup);
    }

    @Override
    public void backupFile(File backup) throws DropboxException, ConnectionFailedException, FileNotFoundException {
        this.dropboxHelper.uploadFile(backup, DEFAULT_PATH + backup.getName());
    }
    @Override
    public String name() {
        return "Dropbox";
    }
}
