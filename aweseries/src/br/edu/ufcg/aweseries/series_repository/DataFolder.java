package br.edu.ufcg.aweseries.series_repository;

import java.io.File;

import br.edu.ufcg.aweseries.App;
import android.os.Environment;

public enum DataFolder {
    SERIES_POSTERS, EPISODE_IMAGES;

    private static File getOrMakeDirectory(String path) {
        File directory = new File(path);

        try {
            if (!directory.exists()) {
                directory.mkdirs();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("can't create the given directory: " + path);
        }

        return directory;
    }

    private static String getRootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + System.getProperty("file.separator") + "Android"
                + System.getProperty("file.separator") + "data"
                + System.getProperty("file.separator")
                + App.environment().context().getPackageName()
                + System.getProperty("file.separator") + "files";
    }

    private static File getRootDirectory() {
        return DataFolder.getOrMakeDirectory(DataFolder.getRootPath());
    }

    public String getPath() {
        return DataFolder.getRootPath() + System.getProperty("file.separator")
                + this.name().toLowerCase();
    }

    public File getDirectory() {
        DataFolder.getRootDirectory();
        return DataFolder.getOrMakeDirectory(this.getPath());
    }
}
