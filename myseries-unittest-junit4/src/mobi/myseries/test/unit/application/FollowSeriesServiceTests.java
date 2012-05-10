package mobi.myseries.test.unit.application;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;

import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bitmap.class, ImageProvider.class})
public class FollowSeriesServiceTests {

    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private FollowSeriesService followSeriesService;
    private LocalizationProvider localizationProvider;
    private ImageProvider imageProvider;

    private SeriesFollowingListener seriesFollowingListener;

    @Before
    public void setUp() {
        this.seriesSource = mock(SeriesSource.class);
        this.seriesRepository = mock(SeriesRepository.class);

        this.localizationProvider = mock(LocalizationProvider.class);
        when(this.localizationProvider.language()).thenReturn("en");

        this.imageProvider = PowerMockito.mock(ImageProvider.class);

        this.seriesFollowingListener = mock(SeriesFollowingListener.class);

        this.followSeriesService = new FollowSeriesService(this.seriesSource,
                                                           this.seriesRepository,
                                                           this.localizationProvider,
                                                           this.imageProvider,
                                                           false);

        this.followSeriesService.addFollowingSeriesListener(this.seriesFollowingListener);
    }

    @After
    public void tearDown() {
        this.seriesSource = null;
        this.seriesRepository = null;
        this.seriesFollowingListener = null;

        this.followSeriesService = null;
    }

    // Constructor

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullSources() {
        new FollowSeriesService(null,
                                this.seriesRepository,
                                this.localizationProvider,
                                this.imageProvider);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullRepositories() {
        new FollowSeriesService(this.seriesSource,
                                null,
                                this.localizationProvider,
                                this.imageProvider);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullLocalizationProviders() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                null,
                                this.imageProvider);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullImageProvider() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                this.localizationProvider,
                                null);
    }

    // Follow

    @Test(expected=IllegalArgumentException.class)
    public void cannotFollowNullSeries() {
        this.followSeriesService.follow(null);
    }

    @Test
    public void shouldNotFollowNonexistentSeries() throws ParsingFailedException,
                                                          ConnectionFailedException,
                                                          SeriesNotFoundException {
        when(this.seriesSource.fetchSeries(anyInt(), anyString()))
            .thenThrow(new SeriesNotFoundException());

        Series seriesToBeFollowed = mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        assertThat(this.followSeriesService.follows(seriesToBeFollowed), is(false));

        verifyZeroInteractions(this.seriesFollowingListener);
    }

    // Unfollow

    @Test(expected=IllegalArgumentException.class)
    public void cannotUnfollowNullSeries() {
        this.followSeriesService.unfollow(null);
    }

    // Follows

    @Test(expected=IllegalArgumentException.class)
    public void cannotCheckIfNullSeriesIsFollowed() {
        this.followSeriesService.follows(null);
    }

    // WipeFollowedSeries

    // Observer

    @Test(expected=IllegalArgumentException.class)
    public void cannotRegisterNullFollowingListeners() {
        this.followSeriesService.addFollowingSeriesListener(null);
    }

}
