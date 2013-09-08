package mobi.myseries.application;

import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.image.AndroidImageServiceRepository;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesCache;
import mobi.myseries.domain.repository.series.SeriesDatabase;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ImageSource;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TheTVDB;
import mobi.myseries.domain.source.trakttv.AddSeriesSource;
import mobi.myseries.domain.source.trakttv.SearchSource;
import mobi.myseries.domain.source.trakttv.Trakt;
import mobi.myseries.domain.source.trakttv.TrendingSource;
import mobi.myseries.shared.Validate;
import android.content.Context;

//TODO (Cleber) Find a better name to this class
public class EnvironmentImpl implements Environment {
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    private static final String THE_TVDB_API_KEY = "6F2B5A871C96FB05";
    private static final String TRAKTTV_API_KEY = "2665c5546c888a02c4ceff0afccfa927";    // Replace this with your app key and secret assigned by Dropbox.
    private static String DROPBOX_APP_KEY = "16plq57cyv3mxdb";
    private static String DROPBOX_APP_SECRET = "5z6c5a0ku03kyjy";

    private TheTVDB theTVDB;
    private Trakt traktTv;
    private DropboxHelper dropboxHelper;
    private LocalizationProvider localizationProvider;
    private SeriesRepository seriesRepository;
    private ImageServiceRepository imageRepository;

    private Context context;

    public EnvironmentImpl(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;

        this.theTVDB = new TheTVDB(THE_TVDB_API_KEY);
        this.traktTv = new Trakt(TRAKTTV_API_KEY);
        this.dropboxHelper = new DropboxHelper(this.context, DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
        this.localizationProvider =  new AndroidLocalizationProvider();
        this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context));
        this.imageRepository = new AndroidImageServiceRepository(this.context);
    }

    @Override
    public Context context() {
        return this.context;
    }

    @Override
    public SeriesSource seriesSource() {
        return this.theTVDB;
    }

    @Override
    public TrendingSource trendingSource() {
        return this.traktTv;
    }

    @Override
    public SearchSource searchSource() {
        return this.traktTv;
    }

    @Override
    public AddSeriesSource addSeriesSource() {
        return this.traktTv;
    }

    @Override
    public ImageSource imageSource() {
        return this.theTVDB;
    }

    @Override
    public DropboxHelper dropboxHelper() {
        return this.dropboxHelper;
    }

    @Override
    public LocalizationProvider localizationProvider() {
        return this.localizationProvider;
    }

    @Override
    public SeriesRepository seriesRepository() {
        return this.seriesRepository;
    }

    @Override
    public ImageServiceRepository imageRepository() {
        return this.imageRepository;
    }
}
