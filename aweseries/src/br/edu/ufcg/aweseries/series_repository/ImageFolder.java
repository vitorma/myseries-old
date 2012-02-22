package br.edu.ufcg.aweseries.series_repository;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import br.edu.ufcg.aweseries.App;

public enum ImageFolder {
    SERIES_POSTERS, EPISODE_IMAGES;

    private static File ensuredDirectory(String path) {
        File directory = new File(path);

        try {
            if (!directory.exists()) {
                directory.mkdirs();
                File nomedia = new File(directory, ".nomedia");
                nomedia.createNewFile();
            }
        } catch (SecurityException e) {
            throw new RuntimeException("can't create the given directory: " + path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return directory;
    }

    private static String rootPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(System.getProperty("file.separator"));
        sb.append("Android");
        sb.append(System.getProperty("file.separator"));
        sb.append("data");
        sb.append(System.getProperty("file.separator"));
        sb.append(App.environment().context().getPackageName());
        sb.append(System.getProperty("file.separator")); 
        sb.append("files");
        
        return sb.toString();
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
