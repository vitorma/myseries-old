package br.edu.ufcg.aweseries.series_repository;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.series_repository.exceptions.ExternalStorageNotAvailableException;

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
            throw new RuntimeException("can't write/read the on given directory: " + path);
        }

        return directory;
    }

    private static String rootPath() {
        return rootDirectory().getPath();
    }

    private static File rootDirectory() {
        if(!isAvaliable())
            throw new ExternalStorageNotAvailableException();
        //TODO create a shared preference to select internal or external storage
        //if(App.environment().context().getSharedPreferences("STORAGE_MODE", 0).equals(EXTERNAL))
        return App.environment().context().getExternalFilesDir(null);
        //return App.environment().context().getFilesDir();
    }

    public String path() {
        return ImageFolder.rootPath() + System.getProperty("file.separator") + this.name().toLowerCase();
    }

    public File directory() {
        return ImageFolder.ensuredDirectory(this.path());
    }
    
    private static boolean isAvaliable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
