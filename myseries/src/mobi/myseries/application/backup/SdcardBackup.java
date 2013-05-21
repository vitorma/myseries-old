package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;

import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import mobi.myseries.shared.FilesUtil;

//TODO extract an interface to the others backup services
public class SdcardBackup implements BackupMode {
    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    private static final String BACKUP_FOLDER = "myseries_backup";
    private static final String BACKUP_DB_FILE = "myseries.db";
    private static final String BACKUP_PREF = "prefs.json";

    public String backupFilePath() throws ExternalStorageNotAvailableException {
        return backupFolder().getPath() + FILE_SEPARATOR + BACKUP_DB_FILE;
    }

    public File backupFolder() throws ExternalStorageNotAvailableException {
        return FilesUtil.ensuredDirectory(sdcardrootDirectory().getPath()
                + FILE_SEPARATOR + BACKUP_FOLDER);
    }

    private File sdcardrootDirectory()
            throws ExternalStorageNotAvailableException {
        File externalFilesDirectory = Environment.getExternalStorageDirectory();

        if (externalFilesDirectory == null || !isSDcardMounted()) {
            throw new ExternalStorageNotAvailableException();
        }
        return externalFilesDirectory;
    }

    private boolean isSDcardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    @Override
    public File getBackup() throws Exception {
        return new File(backupFilePath());
    }

    @Override
    public void backupDB(File backup) throws Exception {
        FilesUtil.copy(backup, new File(backupFilePath()));

    }

    @Override
    public void backupPreferences(SharedPreferences preferences)
            throws Exception {
        // TODO Auto-generated method stub
        
    }
}
