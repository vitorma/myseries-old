package mobi.myseries.shared.imageprocessing;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public final class BitmapResizer {
    Bitmap bitmap;

    public BitmapResizer(Bitmap bitmap) {
        Validate.isNonNull(bitmap, "bitmap");
        this.bitmap = bitmap;
    }

    public Bitmap toSize(int targetWidth, int targetHeight) {
        Validate.isNonNegative(targetWidth, "targetWidth");
        Validate.isNonNegative(targetHeight, "targetHeight");

        Bitmap target = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        int scaleFactor = Math.min(this.bitmap.getWidth() * 1024 / targetWidth,
            this.bitmap.getHeight() * 1024 / targetHeight);

        int red, green, blue;
        int x, y, m, n;
        int sourceX, sourceY, currentX, currentY;
        int rfactor;

        for (y = 0; y < targetHeight; ++y) {
            for (x = 0; x < targetWidth; ++x) {

                sourceX = ((scaleFactor * x) / 1024);
                sourceY = ((scaleFactor * y) / 1024);

                red = 0;
                green = 0;
                blue = 0;
                for (m = -2; m < 2; ++m) {
                    for (n = -2; n < 2; ++n) {
                        currentX = clamp(0, sourceX + n, bitmap.getWidth());
                        currentY = clamp(0, sourceY + m, bitmap.getHeight());

                        rfactor = (r(m) * r(-n));

                        red += ((Color.red(bitmap.getPixel(currentX, currentY))) * rfactor);
                        green += ((Color.green(bitmap.getPixel(currentX, currentY))) * rfactor);
                        blue += ((Color.blue(bitmap.getPixel(currentX, currentY))) * rfactor);
                    }
                }

                target.setPixel(x, y, Color.argb(0xff, red / 36, green / 36, blue / 36));
            }
        }

        return target;
    }

    private int clamp(int lo, int value, int hi) {
        return (value < lo) ? (lo) : ((value > hi) ? (hi) : (value));
    }

    private int r(int x) {
        return ((((1 * ppow3((x + 2)) - (4 * ppow3((x + 1)))) + (6 * ppow3((x)))) - (4 * ppow3((x - 1)))));
    }

    private int ppow3(int x) {
        return x <= 0 ? 0 : x * x * x;
    }
}
