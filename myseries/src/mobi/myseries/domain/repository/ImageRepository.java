package mobi.myseries.domain.repository;

import android.graphics.Bitmap;

public interface ImageRepository {
    public void insertSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void insertEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void updateSeriesPoster(int seriesId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void updateEpisodeImage(int episodeId, Bitmap file) throws ExternalStorageNotAvailableException;
    public void deleteSeriesPoster(int seriesId) throws ExternalStorageNotAvailableException;
    public void deleteEpisodeImage(int episodeId) throws ExternalStorageNotAvailableException;
    public Bitmap getSeriesPoster(int seriesId) throws ExternalStorageNotAvailableException;
    public Bitmap getEpisodeImage(int episodeId) throws ExternalStorageNotAvailableException;
}
