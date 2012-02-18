package br.edu.ufcg.aweseries.series_repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

//TODO Create interface
public class ImageRepository {
    private String seriesPostersFolder;
    private String episodeImagesFolder;

    private ImageRepository() {
        this.seriesPostersFolder = ImageFolder.SERIES_POSTERS.directory().getAbsolutePath();
        this.episodeImagesFolder = ImageFolder.EPISODE_IMAGES.directory().getAbsolutePath();
    }

    public void insertSeriesPoster(int seriesId, Bitmap file) {
        //TODO Implement;
    }

    public void insertEpisodeImage(int episodeId, Bitmap file) {
        //TODO Implement;
    }

    public void updateSeriesPoster(int seriesId, Bitmap file) {
        //TODO Implement;
    }

    public void updateEpisodeImage(int episodeId, Bitmap file) {
        //TODO Implement;
    }

    public void deleteSeriesPoster(int seriesId, Bitmap file) {
        //TODO Implement;
    }

    public void deleteEpisodeImage(int episodeId, Bitmap file) {
        //TODO Implement;
    }

    public Bitmap getSeriesPoster(int seriesId){
        return BitmapFactory.decodeFile(this.seriesPostersFolder + System.getProperty("file.separator") + seriesId);
    }

    public Bitmap getEpisodeImage(int episodeId){
        return BitmapFactory.decodeFile(this.episodeImagesFolder + System.getProperty("file.separator") + episodeId);
    }
}
