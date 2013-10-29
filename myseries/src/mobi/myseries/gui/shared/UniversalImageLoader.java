package mobi.myseries.gui.shared;

import mobi.myseries.application.App;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class UniversalImageLoader {
    
    public static ImageLoader loader() {
        if(!ImageLoader.getInstance().isInited())
            init();
        return ImageLoader.getInstance();
    }

    private static void init() {
        ImageLoader.getInstance().init(imageLoaderConfiguration(App.context()));
    }

    private static ImageLoaderConfiguration imageLoaderConfiguration(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .resetViewBeforeLoading(true)
        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        return config;
    }
}
