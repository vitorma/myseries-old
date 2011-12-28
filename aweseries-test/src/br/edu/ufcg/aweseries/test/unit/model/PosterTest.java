/*
 *   PosterTest.java
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


package br.edu.ufcg.aweseries.test.unit.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import android.graphics.Bitmap;
import br.edu.ufcg.aweseries.model.Poster;
import br.edu.ufcg.aweseries.test.util.SampleBitmap;

public class PosterTest extends TestCase {

    private static final Bitmap posterImage = SampleBitmap.pixel;

    private Poster poster;

    @Override
    public void setUp() throws Exception {
        this.poster = new Poster(posterImage);
    }

    @Override
    public void tearDown() throws Exception {
        this.poster = null;
    }

    public void testConstructingWithNullImageThrowsException() {
        try {
            new Poster(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGetImage() {
        assertThat(this.poster.getImage(), notNullValue());
    }

    public void testToByteArrayReturnsImagesBytes() {
        assertThat(this.poster.toByteArray(), equalTo(bytesFrom(scaled(posterImage))));
    }

    private byte[] bytesFrom(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private Bitmap scaled(Bitmap bmp) {
        return Bitmap.createScaledBitmap(bmp, 102, 150, true);
    }

    public void testEquals() {
        Poster p1 = new Poster(posterImage);
        Poster p2 = new Poster(posterImage);
        Poster p3 = new Poster(posterImage);

        for (int i = 0; i < 1000; ++i) {
            assertThat(p1, equalTo(p1));
            assertThat(p1, equalTo(p2));
            assertThat(p1, not(equalTo(null)));

            assertThat(p1, equalTo(p2));
            assertThat(p2, equalTo(p3));
            assertThat(p1, equalTo(p3));
        }
    }

    public void testHashCode() {
        Poster p1 = new Poster(posterImage);
        Poster p2 = new Poster(posterImage);

        for (int i = 0; i < 1000; ++i) {
            assertThat(p1.hashCode(), equalTo(p1.hashCode()));
            assertThat(p1.hashCode(), equalTo(p2.hashCode()));
        }
    }
}
