/*
 *   SampleBitmap.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

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
