/*
 *   FollowSeriesServiceTests.java
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

package mobi.myseries.test.unit.application.follow;

import java.util.Arrays;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.application.error.ErrorService;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Bitmap.class, ImageService.class})
public class FollowSeriesServiceTests {
    private SeriesSource seriesSource;
    private SeriesRepository seriesRepository;
    private FollowSeriesService followSeriesService;
    private LocalizationProvider localizationProvider;
    private ImageService imageService;
    private ErrorService errorService;
    private BroadcastService broadcastService;

    private SeriesFollowingListener seriesFollowingListener;

    @Before
    public void setUp() {
        this.seriesSource = Mockito.mock(SeriesSource.class);
        this.seriesRepository = Mockito.mock(SeriesRepository.class);
        this.errorService = Mockito.mock(ErrorService.class);
        this.broadcastService = Mockito.mock(BroadcastService.class);

        this.localizationProvider = Mockito.mock(LocalizationProvider.class);
        Mockito.when(this.localizationProvider.language()).thenReturn("en");

        this.imageService = PowerMockito.mock(ImageService.class);

        this.seriesFollowingListener = Mockito.mock(SeriesFollowingListener.class);

        this.followSeriesService = new FollowSeriesService(this.seriesSource,
                                                           this.seriesRepository,
                                                           this.localizationProvider,
                                                           this.imageService,
                                                           this.errorService,
                                                           this.broadcastService,
                                                           false);

        this.followSeriesService.register(this.seriesFollowingListener);
    }

    @After
    public void tearDown() {
        this.seriesSource = null;
        this.seriesRepository = null;
        this.seriesFollowingListener = null;

        this.followSeriesService = null;
    }

    /* Constructor */

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullSources() {
        new FollowSeriesService(null,
                                this.seriesRepository,
                                this.localizationProvider,
                                this.imageService,
                                this.errorService,
                                this.broadcastService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullRepositories() {
        new FollowSeriesService(this.seriesSource,
                                null,
                                this.localizationProvider,
                                this.imageService,
                                this.errorService,
                                this.broadcastService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullLocalizationProviders() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                null,
                                this.imageService,
                                this.errorService,
                                this.broadcastService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullImageService() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                this.localizationProvider,
                                null,
                                this.errorService,
                                this.broadcastService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullErrorService() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                this.localizationProvider,
                                this.imageService,
                                null,
                                this.broadcastService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullBroadcastService() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                this.localizationProvider,
                                this.imageService,
                                this.errorService,
                                null);
    }

    /* Follow */

    @Test(expected=IllegalArgumentException.class)
    public void cannotFollowNullSeries() {
        this.followSeriesService.follow(null);
    }

    @Test
    public void shouldNotFollowNonexistentSeries() throws ParsingFailedException,
                                                          ConnectionFailedException,
                                                          SeriesNotFoundException,
                                                          ConnectionTimeoutException {
        Exception seriesNotFound = new SeriesNotFoundException();
        Mockito.when(this.seriesSource.fetchSeries(Matchers.anyInt(), Matchers.anyString()))
            .thenThrow(seriesNotFound);

        Series seriesToBeFollowed = Mockito.mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        Assert.assertThat(this.followSeriesService.follows(seriesToBeFollowed), CoreMatchers.is(false));

        Mockito.verify(this.seriesFollowingListener, Mockito.only()).onFollowingFailure(seriesToBeFollowed, seriesNotFound);
    }

    @Test @Ignore //TODO: Fix it
    public void errorsMustBeDetectedAfterSuccessfulFollowing() throws ParsingFailedException,
                                                                      ConnectionFailedException,
                                                                      SeriesNotFoundException,
                                                                      ConnectionTimeoutException {
        Series seriesToBeFollowed = Mockito.mock(Series.class);
        Exception seriesNotFound = new SeriesNotFoundException();

        Mockito.when(this.seriesSource.fetchSeries(Matchers.anyInt(), Matchers.anyString()))
            .thenReturn(seriesToBeFollowed)
            .thenThrow(seriesNotFound);

        Series seriesToBeFollowed2 = Mockito.mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        this.followSeriesService.follow(seriesToBeFollowed2);

        Assert.assertThat(this.followSeriesService.follows(seriesToBeFollowed2), CoreMatchers.is(false));

        Mockito.verify(this.seriesFollowingListener, new Times(1)).onFollowingFailure(seriesToBeFollowed2, seriesNotFound);
        Mockito.verify(this.seriesFollowingListener, new Times(1)).onFollowing(seriesToBeFollowed);
    }

    @Test @Ignore //TODO: Fix it
    public void followedSeriesMustBeSaved() throws ParsingFailedException,
                                                   ConnectionFailedException,
                                                   SeriesNotFoundException,
                                                   ConnectionTimeoutException {
        Series seriesToBeFollowed = Mockito.mock(Series.class);

        Mockito.doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(Matchers.anyInt(), Matchers.anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        Mockito.verify(this.seriesRepository).insert(seriesToBeFollowed);
    }

    @Test @Ignore //TODO: Fix it
    public void seriesFollowingListenersMustBeNotifiedOfFollow() throws ParsingFailedException,
                                                                        ConnectionFailedException,
                                                                        SeriesNotFoundException,
                                                                        ConnectionTimeoutException {
        Series seriesToBeFollowed = Mockito.mock(Series.class);

        Mockito.doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(Matchers.anyInt(), Matchers.anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        Mockito.verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed);
    }

    @Test @Ignore //TODO: Fix it
    public void whenASeriesIsFollowedTwiceItMustBeNotifiedEachTime() throws ParsingFailedException,
                                                                            ConnectionFailedException,
                                                                            SeriesNotFoundException,
                                                                            ConnectionTimeoutException {
        Series searchResultSeries = Mockito.mock(Series.class);
        Series seriesToBeFollowed1 = Mockito.mock(Series.class);
        Series seriesToBeFollowed2 = Mockito.mock(Series.class);


        Mockito.when(this.seriesSource.fetchSeries(Matchers.anyInt(), Matchers.anyString()))
            .thenReturn(seriesToBeFollowed1,
                        seriesToBeFollowed2);

        this.followSeriesService.follow(searchResultSeries);
        this.followSeriesService.follow(searchResultSeries);

        Mockito.verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed1);
        Mockito.verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed2);
    }

    // Stop Following

    @Test
    public void whenStopFollowingTheSeriesShouldBeRemovedFromTheRepository() {
        Series followedSeries = Mockito.mock(Series.class);

        this.followSeriesService.stopFollowing(followedSeries);

        Mockito.verify(this.seriesRepository).delete(followedSeries);
    }

    @Test
    public void whenStopFollowingListenersMustBeNotified() {
        Series followedSeries = Mockito.mock(Series.class);

        this.followSeriesService.stopFollowing(followedSeries);

        Mockito.verify(this.seriesFollowingListener).onStopFollowing(followedSeries);
    }

    // Follows

    @Test
    public void followsShouldReturnWhetherASeriesIsInRepositoryOrNot() {
        Series followedSeries = Mockito.mock(Series.class);
        Series notFollowedSeries = Mockito.mock(Series.class);

        Mockito.doReturn(true).when(this.seriesRepository).contains(followedSeries);
        Mockito.doReturn(false).when(this.seriesRepository).contains(notFollowedSeries);

        Assert.assertThat(this.followSeriesService.follows(followedSeries), CoreMatchers.is(true));
        Assert.assertThat(this.followSeriesService.follows(notFollowedSeries), CoreMatchers.is(false));
    }

    // WipeFollowedSeries

    @Test
    public void seriesRepositoryShouldBeClearedAfterAWipe() {
        this.followSeriesService.wipeFollowedSeries();
        Mockito.verify(this.seriesRepository).clear();
    }

    @Test
    public void seriesFollowingListenersMustBeNotifiedAfterAWipe() throws ParsingFailedException,
                                                                          ConnectionFailedException,
                                                                          SeriesNotFoundException {
        Series series1 = Mockito.mock(Series.class);
        Series series2 = Mockito.mock(Series.class);

        Mockito.doReturn(Arrays.asList(series1, series2)).when(this.seriesRepository).getAll();

        this.followSeriesService.wipeFollowedSeries();

        Mockito.verify(this.seriesFollowingListener).onStopFollowing(series1);
        Mockito.verify(this.seriesFollowingListener).onStopFollowing(series2);
    }

    // Observer

    @Test(expected=IllegalArgumentException.class)
    public void cannotRegisterNullFollowingListeners() {
        this.followSeriesService.register(null);
    }
}
