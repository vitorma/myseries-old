package mobi.myseries.gui.shared;

import mobi.myseries.application.App;
import mobi.myseries.shared.Validate;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class UniversalImageLoader {
    private static final String URI_DRAWABLE_PREFIX = "drawable://";
    private static final String URI_FILE_PREFIX = "file://";
    private static final String URI_HTTP_PREFIX = "http://";

    private static final int DISK_CACHE_SIZE = (int) (Runtime.getRuntime().maxMemory());

    public static ImageLoader loader() {
        if(!ImageLoader.getInstance().isInited())
            init();
        return ImageLoader.getInstance();
    }
    
    public static DisplayImageOptions.Builder defaultDisplayBuilder() {
        return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .resetViewBeforeLoading(true)
        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
        .bitmapConfig(Bitmap.Config.RGB_565);
    }
    

    private static void init() {
        ImageLoader.getInstance().init(imageLoaderConfiguration(App.context()));
    }

    private static ImageLoaderConfiguration imageLoaderConfiguration(Context context) {
        DisplayImageOptions defaultOptions = defaultDisplayBuilder().build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .discCacheSize(DISK_CACHE_SIZE)
        .writeDebugLogs()
        .build();
        return config;
    }

    public static String drawableURI(int drawableId) {
        return URI_DRAWABLE_PREFIX + drawableId;
    }

    public static String fileURI(String filePath) {
        Validate.isNonNull(filePath, "ImageLoader file path");

        if(filePath.startsWith(URI_FILE_PREFIX))
            return filePath;

        return URI_FILE_PREFIX + filePath;
    }

    public static String httpURI(String httpPath) {
        Validate.isNonNull(httpPath, "ImageLoader http path");

        if(httpPath.startsWith(URI_HTTP_PREFIX))
            return httpPath;

        return URI_HTTP_PREFIX + httpPath;
    }
}
