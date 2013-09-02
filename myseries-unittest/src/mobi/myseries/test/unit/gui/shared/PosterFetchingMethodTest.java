package mobi.myseries.test.unit.gui.shared;

import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import android.test.InstrumentationTestCase;

import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.PosterFetchingMethod;

public class PosterFetchingMethodTest extends InstrumentationTestCase {

    // Arguments validation

    public void testItShouldNotAcceptNullSeries() {

        try {
            ImageService imageService = mock(ImageService.class);
            new PosterFetchingMethod(null, imageService);

            fail("Exception not thrown.");
        } catch (IllegalArgumentException e) {}
    }

    public void testItShouldNotAcceptNullImageService() {
        try {
            Series series = mock(Series.class);
            new PosterFetchingMethod(series, null);

            fail("Exception not thrown.");
        } catch (IllegalArgumentException e) {}
    }

    // Behaviour

    public void testItShouldRetrieveThePosterOfTheSeriesFromTheService() {
        Series series = mock(Series.class);
        ImageService imageService = mock(ImageService.class);

        new PosterFetchingMethod(series, imageService).loadBitmap();

        verify(imageService).getPosterOf(series);
    }

    // Equals and HashCode

    public void testTwoPosterFetchingMethodsAreEqualWhenTheirSeriesAreEqual() {
        Series series = mock(Series.class);
        ImageService imageService = mock(ImageService.class);

        PosterFetchingMethod first = new PosterFetchingMethod(series, imageService);
        PosterFetchingMethod second = new PosterFetchingMethod(series, imageService);

        assertThat(first, equalTo(second));
    }

    public void testTwoPosterFetchingMethodsHashCodesAreEqualWhenTheirSeriesAreEqual() {
        Series series = mock(Series.class);
        ImageService imageService = mock(ImageService.class);

        PosterFetchingMethod first = new PosterFetchingMethod(series, imageService);
        PosterFetchingMethod second = new PosterFetchingMethod(series, imageService);

        assertThat(first.hashCode(), equalTo(second.hashCode()));
    }

    public void testAPosterFetchingMethodIsNeverEqualToNull() {
        Series series = mock(Series.class);
        ImageService imageService = mock(ImageService.class);

        PosterFetchingMethod first = new PosterFetchingMethod(series, imageService);

        assertThat(first, not(equalTo(null)));
    }

    public void testTwoPosterFetchingMethodsAreNotEqualIfTheirSeriesAreNotEqual() {
        Series firstSeries = mock(Series.class);
        Series secondSeries = mock(Series.class);
        ImageService imageService = mock(ImageService.class);

        PosterFetchingMethod first = new PosterFetchingMethod(firstSeries, imageService);
        PosterFetchingMethod second = new PosterFetchingMethod(secondSeries, imageService);

        assertThat(first, not(equalTo(second)));
    }
}