package br.edu.ufcg.aweseries.test.unit.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;
import android.graphics.Bitmap;
import br.edu.ufcg.aweseries.model.Poster;
import br.edu.ufcg.aweseries.test.util.SampleBitmap;

public class PosterTest extends TestCase {

    private static final Bitmap posterImage = SampleBitmap.pixel;

    private Poster poster;

    public void setUp() throws Exception {
        this.poster = new Poster(posterImage);
    }

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
        assertThat(this.poster.getImage(), equalTo(posterImage));
    }

    public void testToByteArrayReturnsImagesBytes() {
        assertThat(this.poster.toByteArray(), equalTo(SampleBitmap.pixelBytes));
    }

    public void testEquals() {
        Poster p1 = new Poster(SampleBitmap.pixel);
        Poster p2 = new Poster(SampleBitmap.pixel);
        Poster p3 = new Poster(SampleBitmap.pixel);

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
        Poster p1 = new Poster(SampleBitmap.pixel);
        Poster p2 = new Poster(SampleBitmap.pixel);

        for (int i = 0; i < 1000; ++i) {
            assertThat(p1.hashCode(), equalTo(p1.hashCode()));
            assertThat(p1.hashCode(), equalTo(p2.hashCode()));
        }
    }

}
