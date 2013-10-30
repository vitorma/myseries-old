package mobi.myseries.gui.shared;

import mobi.myseries.application.App;
import android.content.Context;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class UniversalImageLoader {

    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; //10 MB

    public static ImageLoader loader() {
        if(!ImageLoader.getInstance().isInited())
            init();
        return ImageLoader.getInstance();
    }
    
    public static DisplayImageOptions.Builder defaultDisplayBuilder() {
        return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .resetViewBeforeLoading(true);
    }
    

    private static void init() {
        ImageLoader.getInstance().init(imageLoaderConfiguration(App.context()));
    }

    private static ImageLoaderConfiguration imageLoaderConfiguration(Context context) {
        DisplayImageOptions defaultOptions = defaultDisplayBuilder().build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .discCacheSize(DISK_CACHE_SIZE)
        .build();
        return config;
    }
}
