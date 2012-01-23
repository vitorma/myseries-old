/*
 *   Poster.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package br.edu.ufcg.aweseries.model;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

import android.graphics.Bitmap;
import br.edu.ufcg.aweseries.util.Validate;

public class Poster {
    private static final int POSTER_WIDTH_IN_PIXELS = 102;
    private static final int POSTER_HEIGHT_IN_PIXELS = 150;

    private Bitmap image;

    public Poster(Bitmap image) {
        Validate.isNonNull(image, "image should be non-null");

        this.image = Bitmap.createScaledBitmap(image, POSTER_WIDTH_IN_PIXELS, POSTER_HEIGHT_IN_PIXELS, true);
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
        if (!(o instanceof Poster))
            return false;

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
