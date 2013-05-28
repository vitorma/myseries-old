package mobi.myseries.application.backup;

import java.io.File;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupMode;

import android.content.Context;

public class DropboxBackup implements BackupMode {
    final static private String DEFAULT_PATH = "";
    private static final String DATABASE_FILE_NAME = "myseries.db";
    private static Context context = App.context();
    private DropboxHelper dropboxHelper;
    
    public DropboxBackup() {
        this.dropboxHelper = App.backupService().getDropboxHelper();
    }
    @Override
    public File getBackup() throws Exception {
        File cachedFile = new File(context.getCacheDir(), DATABASE_FILE_NAME);
        return this.dropboxHelper.downloadFile(DATABASE_FILE_NAME, cachedFile);
    }

    @Override
    public void backupDB(File backup) throws Exception {
        this.dropboxHelper.uploadFile(backup, DEFAULT_PATH + DATABASE_FILE_NAME);
    }
}
