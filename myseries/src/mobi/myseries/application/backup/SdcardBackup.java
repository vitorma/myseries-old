package mobi.myseries.application.backup;

import java.io.File;
import android.os.Environment;

import mobi.myseries.shared.FilesUtil;

//TODO extract an interface to the others backup services
public class SdcardBackup {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    private static final String BACKUP_FOLDER = "myseries_backup";
    private static final String BACKUP_FILE_NAME = "myseries.db";
    

    public String backupFilePath() throws ExternalStorageNotAvailableException {
        return backupFolder().getPath() + FILE_SEPARATOR + BACKUP_FILE_NAME;
    }
    
    public File backupFolder() throws ExternalStorageNotAvailableException {
        return FilesUtil.ensuredDirectory(sdcardrootDirectory().getPath() + FILE_SEPARATOR + BACKUP_FOLDER);
    }

    private File sdcardrootDirectory() throws ExternalStorageNotAvailableException {
        File externalFilesDirectory = Environment.getExternalStorageDirectory();

        if (externalFilesDirectory == null || !isSDcardMounted()) {
            throw new ExternalStorageNotAvailableException();
        }

        return externalFilesDirectory;
    }
    
    private boolean isSDcardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}


