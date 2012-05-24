package mobi.myseries.application;

import android.graphics.Bitmap;

public interface ImageLoadListener {
    
    public void onLoaded(Bitmap image, int seriesId);

}
