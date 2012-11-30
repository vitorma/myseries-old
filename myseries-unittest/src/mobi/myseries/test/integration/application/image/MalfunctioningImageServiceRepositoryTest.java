package mobi.myseries.test.integration.application.image;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.test.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.InstrumentationTestCase;

public abstract class MalfunctioningImageServiceRepositoryTest extends InstrumentationTestCase {
    private static final int TEST_SERIES_ID = -1;
    private static final String TEST_SERIES_NAME = "Test Series";
    private static final int TEST_EPISODE_ID = -1;
    private static final String TEST_EPISODE_NAME = "Test Episode";

    private Bitmap testImage;
    private Series testSeries;
    private Episode testEpisode;
    
    private ImageServiceRepository imageRepository;

    protected abstract ImageServiceRepository newFailingImageServiceRepository();

    public void setUp() {
        this.testEpisode = mock(Episode.class);
        when(this.testEpisode.id()).thenReturn(TEST_EPISODE_ID);
        when(this.testEpisode.name()).thenReturn(TEST_EPISODE_NAME);

        this.testSeries = mock(Series.class);
        when(this.testSeries.id()).thenReturn(TEST_SERIES_ID);
        when(this.testSeries.name()).thenReturn(TEST_SERIES_NAME);
        when(this.testSeries.episodes()).thenReturn(Arrays.asList(this.testEpisode));

        this.testImage = BitmapFactory.decodeResource(
                this.getInstrumentation().getContext().getResources(),
                R.drawable.icon);

        this.imageRepository = this.newFailingImageServiceRepository();
    }

    public void tearDown() {
        this.imageRepository.deleteAllImagesOf(testSeries);

        this.imageRepository = null;
        this.testImage = null;
        this.testSeries = null;
        this.testEpisode = null;
    }

    public void testItMustNotThrowAnyExceptionOnGettingSeriesPosters() {
        this.imageRepository.getPosterOf(this.testSeries);
    }

    public void testItMustNotThrowAnyExceptionOnGettingEpisodeImages() {
        this.imageRepository.getImageOf(this.testEpisode);
    }

    public void testItMustNotThrowAnyExceptionOnSavingSeriesPosters() {
        this.imageRepository.saveSeriesPoster(this.testSeries, this.testImage);
    }

    public void testItMustNotThrowAnyExceptionOnSavingNullSeriesPosters() {
        this.imageRepository.saveSeriesPoster(this.testSeries, null);
    }

    public void testItMustNotThrowAnyExceptionOnSavingEpisodeImages() {
        this.imageRepository.saveEpisodeImage(this.testEpisode, this.testImage);
    }

    public void testItMustNotThrowAnyExceptionOnSavingNullEpisodeImages() {
        this.imageRepository.saveEpisodeImage(this.testEpisode, null);
    }

    public void testItMustNotThrowAnyExceptionOnDeletingImages() {
        this.imageRepository.deleteAllImagesOf(this.testSeries);
    }
}
