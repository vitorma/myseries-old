package mobi.myseries.application.backup;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import mobi.myseries.application.backup.exception.ExternalStorageNotAvailableException;
import mobi.myseries.shared.FilesUtil;

public class SdcardBackup implements BackupMode {
    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    private static final String BACKUP_FOLDER = "myseries_backup";

    public String backupFilePath(String fileName) throws ExternalStorageNotAvailableException {
        return backupFolder().getPath() + FILE_SEPARATOR + fileName;
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
    public void downloadBackupToFile(File backup) throws IOException, ExternalStorageNotAvailableException {
        FilesUtil.copy(new File(backupFilePath(backup.getName())), backup);
    }

    @Override
    public void backupDB(File backup) throws IOException, ExternalStorageNotAvailableException {
        FilesUtil.copy(backup, new File(backupFilePath(backup.getName())));
    }

    @Override
    public String name() {
        return "SD card";
    }
}
