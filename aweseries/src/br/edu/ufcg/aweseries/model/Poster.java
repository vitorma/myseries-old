package br.edu.ufcg.aweseries.model;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.graphics.Bitmap;

public class Poster {

    private static final int POSTER_WIDTH = 102; //px
    private static final int POSTER_HEIGHT = 150; //px

    private Bitmap image;

    public Poster(Bitmap image) {
        if (image == null) {
            throw new IllegalArgumentException("image should not be null");
        }

        this.image = Bitmap.createScaledBitmap(image, POSTER_WIDTH, POSTER_HEIGHT, true);
    }

    public Bitmap getImage() {
        return this.image;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.getImage().compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    @Override
    public int hashCode() {
        return this.getImage().getRowBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Poster)) {
            return false;
        }

        // Why all this stuff?
        // http://stackoverflow.com/questions/6120439/comparing-bitmap-images-in-android

        Bitmap bitmap1 = this.getImage();
        Bitmap bitmap2 = ((Poster) o).getImage();

        ByteBuffer buffer1 = ByteBuffer.allocate(bitmap1.getHeight() * bitmap1.getRowBytes());
        bitmap1.copyPixelsToBuffer(buffer1);

        ByteBuffer buffer2 = ByteBuffer.allocate(bitmap2.getHeight() * bitmap2.getRowBytes());
        bitmap2.copyPixelsToBuffer(buffer2);

        return Arrays.equals(buffer1.array(), buffer2.array());
    }
}
