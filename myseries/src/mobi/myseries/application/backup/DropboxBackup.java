package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileNotFoundException;

import com.dropbox.client2.exception.DropboxException;

import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.domain.source.ConnectionFailedException;

public class DropboxBackup implements BackupMode {
    final static private String DEFAULT_PATH = "";
    private DropboxHelper dropboxHelper;
    
    public DropboxBackup() {
        this.dropboxHelper = App.backupService().getDropboxHelper();
    }
    @Override
    public void downloadBackupToFile(File backup) throws FileNotFoundException, DropboxException, ConnectionFailedException {
        this.dropboxHelper.downloadFile(backup.getName(), backup);
    }

    @Override
    public void backupDB(File backup) throws FileNotFoundException, DropboxException, ConnectionFailedException {
        this.dropboxHelper.uploadFile(backup, DEFAULT_PATH + backup.getName());
    }
    @Override
    public String name() {
        return "Dropbox";
    }
}
