package br.edu.ufcg.aweseries.series_repository;

import java.io.File;

import android.os.Environment;
import br.edu.ufcg.aweseries.App;

public enum ImageFolder {
    SERIES_POSTERS, EPISODE_IMAGES;

    private static File ensuredDirectory(String path) {
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

    private static String rootPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + System.getProperty("file.separator") + "Android"
                + System.getProperty("file.separator") + "data"
                + System.getProperty("file.separator")
                + App.environment().context().getPackageName()
                + System.getProperty("file.separator") + "files";
    }

    private static File rootDirectory() {
        return ImageFolder.ensuredDirectory(ImageFolder.rootPath());
    }

    public String path() {
        return ImageFolder.rootPath() + System.getProperty("file.separator") + this.name().toLowerCase();
    }

    public File directory() {
        ImageFolder.rootDirectory();
        return ImageFolder.ensuredDirectory(this.path());
    }
}
