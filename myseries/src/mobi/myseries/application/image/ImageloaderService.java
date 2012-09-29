package mobi.myseries.application.image;

import mobi.myseries.application.App;
import mobi.myseries.application.Environment;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.ImageDirectory;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.DecodingType;

public class ImageloaderService {
    private static final Environment ENVIRONMENT = App.environment();
    private static final ImageLoader imageLoader = ImageLoader.getInstance();

    public ImageloaderService() {
        ImageLoader imageLoader = ImageLoader.getInstance();

        // Create configuration for ImageLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ENVIRONMENT.context())
                .maxImageWidthForMemoryCache(800)
                .maxImageHeightForMemoryCache(800)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .denyCacheImageMultipleSizesInMemory()
                .offOutOfMemoryHandling()
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        // Initialize ImageLoader with created configuration. Do it once.
        imageLoader.init(config);
    }

    public void loadPoster(Series series, ImageLoadSupplicant suplicant) {
        /* TODO(cleber) extract what we can learn from this class to improve the quality of the images and then wipe it
         *  out.

        String filepath = "file://" + ImageDirectory.getPathForPoster(series.id()) ;

        // Creates display image options for custom display task
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                         .showImageForEmptyUrl(suplicant.getDefaultResource())
                                         .showStubImage(suplicant.getDefaultResource())
                                         .cacheInMemory()
                                         .cacheOnDisc()
                                         .decodingType(DecodingType.MEMORY_SAVING)
                                         .build();

        imageLoader.displayImage(filepath, suplicant.getImageView(), options);
        */
    }
}
