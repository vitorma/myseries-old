package mobi.myseries.test.integration.application.image;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import junit.framework.TestCase;

import mobi.myseries.application.image.ImageServiceRepository;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;

public abstract class MalfunctioningImageServiceRepositoryTest extends TestCase {
    private static final int TEST_SERIES_ID = -1;
    private static final String TEST_SERIES_NAME = "Test Series";
    private static final int TEST_EPISODE_ID = -1;
    private static final String TEST_EPISODE_NAME = "Test Episode";
    
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
        
        this.imageRepository = this.newFailingImageServiceRepository();
    }

    public void tearDown() {
        this.imageRepository.deleteAllImagesOf(testSeries);

        this.imageRepository = null;
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
        this.imageRepository.saveSeriesPoster(this.testSeries, null);
    }

    public void testItMustNotThrowAnyExceptionOnSavingEpisodeImages() {
        this.imageRepository.saveEpisodeImage(this.testEpisode, null);
    }

    public void testItMustNotThrowAnyExceptionOnDeletingImages() {
        this.imageRepository.deleteAllImagesOf(this.testSeries);
    }
}
