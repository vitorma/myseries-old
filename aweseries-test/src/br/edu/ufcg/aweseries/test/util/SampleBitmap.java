package br.edu.ufcg.aweseries.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class SampleBitmap {

    private static final String base64EncodedPixel
    = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAAXNSR0IArs4c6QAAAAlwSFlzAAAL"
        + "EwAACxMBAJqcGAAAAAd0SU1FB9sKDgAdHPUvGY0AAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRo"
        + "IEdJTVBXgQ4XAAAADElEQVQI12P4//8/AAX+Av7czFnnAAAAAElFTkSuQmCC";

    public static final Bitmap pixel
            = BitmapFactory.decodeStream(
                    new ByteArrayInputStream(Base64.decode(base64EncodedPixel, Base64.DEFAULT)));

    public static final byte[] pixelBytes;
    static {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pixel.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        pixelBytes = outputStream.toByteArray();
    }

}
