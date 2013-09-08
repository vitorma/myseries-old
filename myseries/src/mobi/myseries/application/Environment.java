package mobi.myseries.application;

import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.trakttv.AddSeriesSource;
import mobi.myseries.domain.source.trakttv.SearchSource;
import mobi.myseries.domain.source.trakttv.TrendingSource;
import android.content.Context;

public interface Environment {
    public Context context();
    @Deprecated
    public SeriesSource seriesSource();
    public SearchSource searchSource();
    public TrendingSource trendingSource();
    public AddSeriesSource addSeriesSource();
    @Deprecated
    public ImageSource imageSource();
    public DropboxHelper dropboxHelper();
    public LocalizationProvider localizationProvider();
    public SeriesRepository seriesRepository();
    public ImageServiceRepository imageRepository();
}
