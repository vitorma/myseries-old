package mobi.myseries.application.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import android.os.Environment;

import mobi.myseries.application.App;

//TODO extract an interface to the others backup services
public class sdcardBackup {
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    //TODO this should not be hardcoded, discover a better way to do it
    private static final String DATABASE_NAME = "myseries_db";
    private static final String BACKUP_FOLDER = "myseries_backup";
    private static final String BACKUP_FILE_NAME = "myseries.db";
    
    public void exportDB() {
        try {
            this.copyFile(new FileInputStream(dbPath()), new FileOutputStream(backupFilePath()));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String backupFilePath() {
        return backupFolder().getPath() + FILE_SEPARATOR + BACKUP_FILE_NAME;
    }
    
    private static File backupFolder() {
        return ensuredDirectory(sdcardrootDirectory().getPath() + FILE_SEPARATOR + BACKUP_FOLDER);
    }
    
    private static File ensuredDirectory(String path) {
        File directory = new File(path);

        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (SecurityException e) {
            //TODO something
        }

        return directory;
    }

    private static File sdcardrootDirectory() {
        File externalFilesDirectory = Environment.getExternalStorageDirectory();

        if (externalFilesDirectory == null) {
            //TODO something
        }

        return externalFilesDirectory;
    }

    private File dbPath(){
        return App.context().getDatabasePath(DATABASE_NAME);
    }

    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    public static String path() {
        return backupFilePath();
    }
}


