package mobi.myseries.shared.imageprocessing;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public final class BitmapResizer {
    final Bitmap bitmap;

    public BitmapResizer(Bitmap bitmap) {
        Validate.isNonNull(bitmap, "bitmap");
        this.bitmap = bitmap;
    }

    public Bitmap toSize(int targetWidth, int targetHeight) {
        Validate.isNonNegative(targetWidth, "targetWidth");
        Validate.isNonNegative(targetHeight, "targetHeight");

        //TODO (Cleber) This method is currently very expensive.
        //              Due to it, add a series can take more than one minute.
        //              Improve it and uncomment next lines.
        
        long start = System.currentTimeMillis();

        final Bitmap destiny = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        final float scaleFactor = Math.min((float) this.bitmap.getWidth() / targetWidth,
                        (float) this.bitmap.getHeight() / targetHeight);
        
        float dx, dy, red, green, blue;
        int x, y, m, n;
        int sourceX, sourceY, currentX, currentY;
        

        for (y = 0; y < targetHeight; ++y) {
            for (x = 0; x < targetWidth; ++x) {

                sourceX = clamp(0, (int) (scaleFactor * x), this.bitmap.getWidth() - 1);
                sourceY = clamp(0, (int) (scaleFactor * y), this.bitmap.getHeight() - 1);

                dx = x - (sourceX * (1 / scaleFactor));
                dy = y - (sourceY * (1 / scaleFactor));

                red = 0;
                green = 0;
                blue = 0;
                for (m = -2; m < 2; ++m) {
                    for (n = -2; n < 2; ++n) {
                        currentX = clamp(0, sourceX + n, bitmap.getWidth());
                        currentY = clamp(0, sourceY + m, bitmap.getHeight());

                        red += ((Color.red(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy - n));
                        green += ((Color.green(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy - n));
                        blue += ((Color.blue(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy - n));
                    }
                }

                destiny.setPixel(x, y, Color.argb(0xff, (int) red, (int) green, (int) blue));
            }
        }
        
        long end = System.currentTimeMillis();
        
        Log.i(getClass().getName(), "Resize time: " + (end - start) / 1000.0);

        //Bitmap destiny = Bitmap.createScaledBitmap(this.bitmap, targetWidth, targetHeight, false);
        return destiny;
    }

    private int clamp(int lo, int value, int hi) {
        return (value < lo) ? (lo) : ((value > hi) ? (hi) : (value));
    }

    private float r(float x) {
        return ((1.0f / 6.0f) * 
                (((1 * ppow3((x + 2)) - (4 * ppow3((x + 1)))) + (6 * ppow3((x)))) - (4 * ppow3((x - 1)))));
    }
    
    private float ppow3(float x) {
        return x < 0.01 ? 0: x * x * x;
    }
}
