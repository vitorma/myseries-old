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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayOutputStream;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.graphics.Bitmap;
import br.edu.ufcg.aweseries.model.Poster;
import br.edu.ufcg.aweseries.test.util.SampleBitmap;

public class PosterTest extends TestCase {

	private static final Bitmap POSTER_IMAGE = SampleBitmap.pixel;

	//Auxiliary---------------------------------------------------------------------------------------------------------

	private static byte[] bytesFrom(Bitmap bmp) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
		return outputStream.toByteArray();
	}

	private static Bitmap scaled(Bitmap bmp) {
		return Bitmap.createScaledBitmap(bmp, 102, 150, true);
	}

	//Test--------------------------------------------------------------------------------------------------------------

	public void testConstructingAPosterWithNullImageCausesIllegalArgumentException() {
		try {
			new Poster(null);
			fail("Should have thrown an IllegalArgumentException");
		} catch (IllegalArgumentException e) {}
	}

	public void testImageIsNotNull() {
		assertThat(new Poster(POSTER_IMAGE), notNullValue());
	}

	public void testToByteArrayReturnsTheBytesOfTheImage() {
		assertThat(new Poster(POSTER_IMAGE).toByteArray(), equalTo(bytesFrom(scaled(POSTER_IMAGE))));
	}

	public void testEquals() {
		Poster p1 = new Poster(POSTER_IMAGE);
		Poster p2 = new Poster(POSTER_IMAGE);
		Poster p3 = new Poster(POSTER_IMAGE);

		//equals is consistent
		for (int i = 0; i < 10; ++i) {

			//equals returns false for null object
			Assert.assertFalse(p1.equals(null));

			//equals is reflexive
			Assert.assertTrue(p1.equals(p1));

			//equals is symmetric
			Assert.assertTrue(p1.equals(p2));
			Assert.assertTrue(p2.equals(p1));

			//equals is transitive
			Assert.assertTrue(p2.equals(p3));
			Assert.assertTrue(p1.equals(p3));
		}
	}

	public void testHashCode() {
		Poster p1 = new Poster(POSTER_IMAGE);
		Poster p2 = new Poster(POSTER_IMAGE);

		//hashCode is consistent
		for (int i = 0; i < 10; ++i) {

			//equal objects have the same hashCode
			Assert.assertEquals(p1.hashCode(), p2.hashCode());
		}
	}
}
