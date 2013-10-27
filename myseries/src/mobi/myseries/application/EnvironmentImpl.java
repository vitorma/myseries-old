package mobi.myseries.application;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import mobi.myseries.application.backup.DropboxHelper;
import mobi.myseries.application.image.AndroidImageServiceRepository;
import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.repository.series.SeriesCache;
import mobi.myseries.domain.repository.series.SeriesDatabase;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.Trakt;
import mobi.myseries.domain.source.TraktApi;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;

//TODO (Cleber) Find a better name to this class
public class EnvironmentImpl implements Environment {
    // Note that this is a really insecure way to do this, and you shouldn't
    // ship code which contains your key & secret in such an obvious way.
    // Obfuscation is good.
    private static final String TRAKTTV_API_KEY = "2665c5546c888a02c4ceff0afccfa927";
    // Replace this with your app key and secret assigned by Dropbox.
    private static final String DROPBOX_APP_KEY = "16plq57cyv3mxdb";
    private static final String DROPBOX_APP_SECRET = "5z6c5a0ku03kyjy";

    private final TraktApi traktApi;
    private final DropboxHelper dropboxHelper;
    private final LocalizationProvider localizationProvider;
    private final SeriesRepository seriesRepository;
    private final ImageServiceRepository imageRepository;

    private final Context context;
    private final Communications communications;

    public EnvironmentImpl(Context context) {
        Validate.isNonNull(context, "context");

        this.context = context;
        this.communications = new CommunicationsImpl(this.context);
        //this.communications = new OfflineCommunicationsImpl();

        this.traktApi = new Trakt(this.communications, TRAKTTV_API_KEY);
        this.dropboxHelper = new DropboxHelper(this.context, this.communications, DROPBOX_APP_KEY, DROPBOX_APP_SECRET);
        this.localizationProvider =  new AndroidLocalizationProvider();
        this.seriesRepository = new SeriesCache(new SeriesDatabase(this.context));
        this.imageRepository = new AndroidImageServiceRepository(this.context);
        ImageLoader.getInstance().init(imageLoaderConfiguration(this.context));
    }

    @Override
    public Context context() {
        return this.context;
    }

    @Override
    public Communications communications() {
        return this.communications;
    }

    @Override
    public TraktApi traktApi() {
        return this.traktApi;
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
    
    private ImageLoaderConfiguration imageLoaderConfiguration(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .resetViewBeforeLoading(true)
        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .threadPoolSize(1)
        .memoryCache(new WeakMemoryCache())
        .build();
        return config;
    }
}
