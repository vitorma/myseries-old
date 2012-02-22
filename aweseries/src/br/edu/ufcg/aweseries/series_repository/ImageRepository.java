package br.edu.ufcg.aweseries.series_repository;

import android.graphics.Bitmap;

public interface ImageRepository {

    public void insertSeriesPoster(int seriesId, Bitmap file);

    public void insertEpisodeImage(int episodeId, Bitmap file);

    public void updateSeriesPoster(int seriesId, Bitmap file);

    public void updateEpisodeImage(int episodeId, Bitmap file);

    public void deleteSeriesPoster(int seriesId);

    public void deleteEpisodeImage(int episodeId);

    public Bitmap getSeriesPoster(int seriesId);

    public Bitmap getEpisodeImage(int episodeId);

}
