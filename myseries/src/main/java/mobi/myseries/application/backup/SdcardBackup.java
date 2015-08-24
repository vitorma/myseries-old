package mobi.myseries.application.backup;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import mobi.myseries.application.backup.exception.ExternalStorageNotAvailableException;
import mobi.myseries.application.backup.exception.SDcardException;
import mobi.myseries.shared.FilesUtil;

public class SdcardBackup implements BackupMode {
    private static final String FILE_SEPARATOR = System
            .getProperty("file.separator");

    private static final String BACKUP_FOLDER = "myseries_backup";

    private File backupFile;

    public SdcardBackup(File backupFile) {
        this.backupFile = backupFile;
    }
    
    public SdcardBackup() {}

    public String backupFilePath() throws ExternalStorageNotAvailableException {
        String fileName = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss'.bkp'").format(new Date());
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
    public void downloadBackupToFile(File backup) throws ExternalStorageNotAvailableException, SDcardException {
        try {
            FilesUtil.copy(backupFile, backup);
        } catch (IOException e) {
           throw new SDcardException(e);
        }
    }

    @Override
    public void backupFile(File backup) throws IOException, ExternalStorageNotAvailableException {
        FilesUtil.copy(backup, new File(backupFilePath()));
    }

    @Override
    public String name() {
        return "SD card";
    }

    public static File getDefaultFolder() {
        return new File(Environment.getExternalStorageDirectory().getPath()
                + FILE_SEPARATOR + BACKUP_FOLDER);
    }

    public static boolean hasBackupFiles() {
        return FilesUtil.listFilesOfDirectory(getDefaultFolder(), "bkp").length > 0;
    }
}
