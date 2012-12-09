package mobi.myseries.application.backup;

import java.io.File;
import android.os.Environment;

import mobi.myseries.shared.FilesUtil;

//TODO extract an interface to the others backup services
public class SdcardBackup {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String BACKUP_FOLDER = "myseries_backup";
    private static final String BACKUP_FILE_NAME = "myseries.db";
    

    public String backupFilePath() {
        return backupFolder().getPath() + FILE_SEPARATOR + BACKUP_FILE_NAME;
    }
    
    private File backupFolder() {
        return FilesUtil.ensuredDirectory(sdcardrootDirectory().getPath() + FILE_SEPARATOR + BACKUP_FOLDER);
    }

    private File sdcardrootDirectory() {
        File externalFilesDirectory = Environment.getExternalStorageDirectory();

        if (externalFilesDirectory == null) {
            //TODO something
        }

        return externalFilesDirectory;
    }
}


