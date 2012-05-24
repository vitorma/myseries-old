package mobi.myseries.application;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.DecodingType;

public class ImageloaderService {
    private static final Environment ENVIRONMENT = App.environment();
    private static final ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;

    public ImageloaderService() {
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Create configuration for ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                ENVIRONMENT.context()).maxImageWidthForMemoryCache(800)
                .maxImageHeightForMemoryCache(800).threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .denyCacheImageMultipleSizesInMemory().offOutOfMemoryHandling()
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();
        // Initialize ImageLoader with created configuration. Do it once.
        imageLoader.init(config);

    }

    public void load(String path, ImageLoadSupplicant suplicant) {
        String filepath = "file://" + path;
        
        // Creates display image options for custom display task
        options = new DisplayImageOptions.Builder()
                                         .showImageForEmptyUrl(suplicant.getDefaultResource())
                                         .showStubImage(suplicant.getDefaultResource())
                                         .cacheInMemory()
                                         .cacheOnDisc()
                                         .decodingType(DecodingType.MEMORY_SAVING)
                                         .build();
        
        imageLoader.displayImage(filepath, suplicant.getImageView(), options);
        
    }
}
