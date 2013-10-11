package mobi.myseries.gui.shared;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;

public abstract class PosterFetchingMethod implements AsyncImageLoader.BitmapFetchingMethod {

    protected final Series series;
    protected final ImageService imageService;

    public PosterFetchingMethod(Series series, ImageService imageService) {
        Validate.isNonNull(series, "series");
        Validate.isNonNull(imageService, "imageService");

        this.series = series;
        this.imageService = imageService;
    }

    @Override
    public abstract Bitmap loadBitmap();

    // equals and hashCode were generated based upon series

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((series == null) ? 0 : series.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PosterFetchingMethod other = (PosterFetchingMethod) obj;
        if (series == null) {
            if (other.series != null)
                return false;
        } else if (!series.equals(other.series))
            return false;
        return true;
    }
}
