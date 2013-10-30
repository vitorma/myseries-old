package mobi.myseries.test.integration.application.image;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.test.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public abstract class ImageServiceRepositoryTest extends InstrumentationTestCase {

    private static final long MAXIMUM_TIME_FOR_ASYNC_OPERATIONS = 5000;  // milliseconds

    private static final int TEST_SERIES_ID = -1;
    private static final String TEST_SERIES_NAME = "Test Series";
    private static final long TEST_EPISODE_ID = -1;
    private static final String TEST_EPISODE_NAME = "Test Episode";

    private Bitmap testImage;
    private Series testSeries;
    private Episode testEpisode;

    private ImageServiceRepository imageRepository;

    protected abstract ImageServiceRepository newImageServiceRepository();

    public void setUp() {
        this.testEpisode = mock(Episode.class);
        when(this.testEpisode.id()).thenReturn(TEST_EPISODE_ID);
        when(this.testEpisode.title()).thenReturn(TEST_EPISODE_NAME);

        this.testSeries = mock(Series.class);
        when(this.testSeries.id()).thenReturn(TEST_SERIES_ID);
        when(this.testSeries.name()).thenReturn(TEST_SERIES_NAME);
        when(this.testSeries.episodes()).thenReturn(Arrays.asList(this.testEpisode));

        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.imageRepository = this.newImageServiceRepository();

        this.imageRepository.deleteAllImagesOf(testSeries);
    }

    public void tearDown() {
        this.imageRepository.deleteAllImagesOf(testSeries);

        this.imageRepository = null;
        this.testImage = null;
        this.testSeries = null;
        this.testEpisode = null;
    }

    /* Series poster */

    public void testSavingAPosterOfANullSeriesThrowsException() {
        try {
            this.imageRepository.saveSeriesPoster(null, this.testImage);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testItIsPossibleToSaveANullPoster() {
        this.imageRepository.saveSeriesPoster(this.testSeries, null);
    }

    public void testGettingAPosterOfANullSeriesThrowsException() {
        try {
            this.imageRepository.getPosterOf(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testGettingANotSavedPosterReturnsNull() {
        assertThat(this.imageRepository.getPosterOf(this.testSeries), nullValue());
    }

    public void testGettingASavedPosterMustReturnAValidPoster() {
        this.imageRepository.saveSeriesPoster(this.testSeries, this.testImage);

        // TODO(Gabriel) Find a better way to compare the bitmaps
        assertThat(this.imageRepository.getPosterOf(this.testSeries), not(nullValue()));
    }

    /* Delete images of series */

    public void testDeletingImagesOfNullSeriesThrowsException() {
        try {
            this.imageRepository.deleteAllImagesOf(null);
            fail("Should have thrown an IllegalArgumentException");
        } catch (IllegalArgumentException e) {}
    }

    public void testItMayDeleteAllImagesOfASeriesWithoutAnySavedImage() {
        this.imageRepository.deleteAllImagesOf(this.testSeries);
    }

    public void testTheSavedPosterOfASeriesShouldBeDeletedAfterDeletingAllImagesOfTheSeries() {
        this.imageRepository.saveSeriesPoster(this.testSeries, this.testImage);

        this.imageRepository.deleteAllImagesOf(this.testSeries);
        waitForAsyncOperations();
        assertThat(this.imageRepository.getPosterOf(this.testSeries), nullValue());
    }

    /* Test tools */

    private static void waitForAsyncOperations() {
        try {
            Thread.sleep(MAXIMUM_TIME_FOR_ASYNC_OPERATIONS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
