package mobi.myseries.gui.shared;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import android.graphics.Bitmap;

public class NormalPosterFetchingMethod extends PosterFetchingMethod {

    public NormalPosterFetchingMethod(Series series, ImageService imageService) {
        super(series, imageService);
    }

    @Override
    public Bitmap loadBitmap() {
        return this.imageService.getPosterOf(this.series);
    }

    @Override
    public Bitmap loadCachedBitmap() {
        return this.imageService.getCachedPosterOf(this.series);
    }

}
