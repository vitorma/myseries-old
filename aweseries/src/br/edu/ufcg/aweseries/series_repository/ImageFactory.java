package br.edu.ufcg.aweseries.series_repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageFactory {
    
    enum ImageFormat {
        POSTER, EPISODE
    }

    private static ImageFactory instance;

    private String seriesPosterFolder;
    private String episodeImagesFolder;

    private ImageFactory() {
        this.seriesPosterFolder = DataFolder.SERIES_POSTERS.getDirectory().getAbsolutePath(); 
        this.episodeImagesFolder = DataFolder.EPISODE_IMAGES.getDirectory().getAbsolutePath();

    }

    public static synchronized ImageFactory getInstance() {
        if (instance == null) {
            instance = new ImageFactory();
        }
        return instance;
    }
    
    private Bitmap formatImage(Bitmap image, ImageFormat format) {
        return null;
    }
    
    public Bitmap getSeriesPoster(String fileName){
        return BitmapFactory.decodeFile(this.seriesPosterFolder + System.getProperty("file.separator") + fileName);
    }
    
    public Bitmap getEpisodeImage(String fileName){
        return BitmapFactory.decodeFile(this.episodeImagesFolder + System.getProperty("file.separator") + fileName);
    }

}
