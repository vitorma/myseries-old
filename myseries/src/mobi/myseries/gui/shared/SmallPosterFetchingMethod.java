package mobi.myseries.gui.shared;

import android.graphics.Bitmap;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;

public class SmallPosterFetchingMethod extends PosterFetchingMethod {

    public SmallPosterFetchingMethod(Series series, ImageService imageService) {
        super(series, imageService);
    }
    
    @Override
    public Bitmap loadBitmap() {
        return this.imageService.getSmallPosterOf(this.series);
    }

    @Override
    public Bitmap loadCachedBitmap() {
        return this.imageService.getCachedSmallPosterOf(series);
    }

}
