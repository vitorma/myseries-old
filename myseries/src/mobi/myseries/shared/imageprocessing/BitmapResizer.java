package mobi.myseries.shared.imageprocessing;

import mobi.myseries.shared.Validate;
import android.graphics.Bitmap;
import android.graphics.Color;

public class BitmapResizer {
    final Bitmap bitmap;

    public BitmapResizer(Bitmap bitmap) {
        Validate.isNonNull(bitmap, "bitmap");
        this.bitmap = bitmap;
    }

    public Bitmap toSize(int targetWidth, int targetHeight) {
        Validate.isNonNegative(targetWidth, "targetWidth");
        Validate.isNonNegative(targetHeight, "targetHeight");

        Bitmap destiny = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);

        float scaleFactor =
                Math.min((float) this.bitmap.getWidth() / targetWidth,
                        (float) this.bitmap.getHeight() / targetHeight);

        for (int y = 0; y < targetHeight; ++y) {
            for (int x = 0; x < targetWidth; ++x) {

                int sourceX = clamp(0, (int) (scaleFactor * x), this.bitmap.getWidth() - 1);
                int sourceY = clamp(0, (int) (scaleFactor * y), this.bitmap.getHeight() - 1);

                double dx = x - (sourceX * (1 / scaleFactor));
                double dy = y - (sourceY * (1 / scaleFactor));

                double red = 0;
                double green = 0;
                double blue = 0;
                for (int m = -2; m < 2; ++m) {
                    for (int n = -2; n < 2; ++n) {
                        int currentX = clamp(0, sourceX + n, bitmap.getWidth());
                        int currentY = clamp(0, sourceY + m, bitmap.getHeight());

                        red +=
                                ((Color.red(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy
                                        - n));
                        green +=
                                ((Color.green(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy
                                        - n));
                        blue +=
                                ((Color.blue(bitmap.getPixel(currentX, currentY))) * r(m - dx) * r(dy
                                        - n));
                    }
                }

                destiny.setPixel(x, y, Color.argb(0xff, (int) red, (int) green, (int) blue));
            }
        }

        return destiny;
    }

    public int clamp(int lo, int value, int hi) {
        return (value < lo) ? (lo) : ((value > hi) ? (hi) : (value));
    }

    public double r(double x) {
        return ((1.0 / 6.0) * (((Math.pow(p(x + 2.), 3.) - (4. * Math.pow(p(x + 1.), 3.))) + (6. * Math
                .pow(p(x), 3.))) - (4. * Math.pow(p(x - 1.), 3.))));
    }

    public double p(double x) {
        return (x < 0.) ? (0.) : (x);
    }
}
