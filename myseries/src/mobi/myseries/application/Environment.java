package mobi.myseries.application;

import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.trakttv.TraktApi;
import android.content.Context;

public interface Environment {
    public Context context();
    public TraktApi traktApi();
    public DropboxHelper dropboxHelper();
    public LocalizationProvider localizationProvider();
    public SeriesRepository seriesRepository();
    public ImageServiceRepository imageRepository();
}
