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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import mobi.myseries.application.LocalizationProvider;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private SeriesFollowingListener seriesFollowingListener;
    private ErrorService errorService;

    @Before
    public void setUp() {
        this.seriesSource = mock(SeriesSource.class);
        this.seriesRepository = mock(SeriesRepository.class);
        this.errorService = mock(ErrorService.class);

        this.localizationProvider = mock(LocalizationProvider.class);
        when(this.localizationProvider.language()).thenReturn("en");

        this.imageService = PowerMockito.mock(ImageService.class);

        this.seriesFollowingListener = mock(SeriesFollowingListener.class);

        this.followSeriesService = new FollowSeriesService(this.seriesSource,
                                                           this.seriesRepository,
                                                           this.localizationProvider,
                                                           this.imageService,
                                                           this.errorService,
                                                           false);

        this.followSeriesService.registerSeriesFollowingListener(this.seriesFollowingListener);
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
                                this.imageService,
                                this.errorService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullRepositories() {
        new FollowSeriesService(this.seriesSource,
                                null,
                                this.localizationProvider,
                                this.imageService,
                                this.errorService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullLocalizationProviders() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                null,
                                this.imageService,
                                this.errorService);
    }

    @Test(expected=IllegalArgumentException.class)
    public void itShouldNotAcceptNullImageService() {
        new FollowSeriesService(this.seriesSource,
                                this.seriesRepository,
                                this.localizationProvider,
                                null,
                                this.errorService);
    }

    // Follow

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
        when(this.seriesSource.fetchSeries(anyInt(), anyString()))
            .thenThrow(seriesNotFound);

        Series seriesToBeFollowed = mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        assertThat(this.followSeriesService.follows(seriesToBeFollowed), is(false));

        verify(this.seriesFollowingListener, only()).onFollowingFailure(seriesToBeFollowed, seriesNotFound);
    }

    @Test
    public void errorsMustBeDetectedAfterSuccessfulFollowing() throws ParsingFailedException,
                                                                      ConnectionFailedException,
                                                                      SeriesNotFoundException, 
                                                                      ConnectionTimeoutException {
        Series seriesToBeFollowed = mock(Series.class);
        Exception seriesNotFound = new SeriesNotFoundException();

        when(this.seriesSource.fetchSeries(anyInt(), anyString()))
            .thenReturn(seriesToBeFollowed)
            .thenThrow(seriesNotFound);

        Series seriesToBeFollowed2 = mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        this.followSeriesService.follow(seriesToBeFollowed2);

        assertThat(this.followSeriesService.follows(seriesToBeFollowed2), is(false));

        verify(this.seriesFollowingListener, new Times(1)).onFollowingFailure(seriesToBeFollowed2, seriesNotFound);
        verify(this.seriesFollowingListener, new Times(1)).onFollowing(seriesToBeFollowed);
    }

    @Test
    public void followedSeriesMustBeSaved() throws ParsingFailedException,
                                                   ConnectionFailedException,
                                                   SeriesNotFoundException, 
                                                   ConnectionTimeoutException {
        Series seriesToBeFollowed = mock(Series.class);

        doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(anyInt(), anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        verify(this.seriesRepository).insert(seriesToBeFollowed);
    }

    @Test
    public void seriesFollowingListenersMustBeNotifiedOfFollow() throws ParsingFailedException,
                                                                        ConnectionFailedException,
                                                                        SeriesNotFoundException, 
                                                                        ConnectionTimeoutException {
        Series seriesToBeFollowed = mock(Series.class);

        doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(anyInt(), anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed);
    }

    @Test
    public void whenASeriesIsFollowedTwiceItMustBeNotifiedEachTime() throws ParsingFailedException,
                                                                            ConnectionFailedException,
                                                                            SeriesNotFoundException, 
                                                                            ConnectionTimeoutException {
        Series searchResultSeries = mock(Series.class);
        Series seriesToBeFollowed1 = mock(Series.class);
        Series seriesToBeFollowed2 = mock(Series.class);
        

        when(this.seriesSource.fetchSeries(anyInt(), anyString()))
            .thenReturn(seriesToBeFollowed1,
                        seriesToBeFollowed2);

        this.followSeriesService.follow(searchResultSeries);
        this.followSeriesService.follow(searchResultSeries);

        verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed1);
        verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed2);
    }

    // Stop Following

    @Test
    public void whenStopFollowingTheSeriesShouldBeRemovedFromTheRepository() {
        Series followedSeries = mock(Series.class);

        this.followSeriesService.stopFollowing(followedSeries);

        verify(this.seriesRepository).delete(followedSeries);
    }

    @Test
    public void whenStopFollowingListenersMustBeNotified() {
        Series followedSeries = mock(Series.class);

        this.followSeriesService.stopFollowing(followedSeries);

        verify(this.seriesFollowingListener).onStopFollowing(followedSeries);
    }

    // Follows

    @Test
    public void followsShouldReturnWhetherASeriesIsInRepositoryOrNot() {
        Series followedSeries = mock(Series.class);
        Series notFollowedSeries = mock(Series.class);

        doReturn(true).when(this.seriesRepository).contains(followedSeries);
        doReturn(false).when(this.seriesRepository).contains(notFollowedSeries);

        assertThat(this.followSeriesService.follows(followedSeries), is(true));
        assertThat(this.followSeriesService.follows(notFollowedSeries), is(false));
    }

    // WipeFollowedSeries

    @Test
    public void seriesRepositoryShouldBeClearedAfterAWipe() {
        this.followSeriesService.wipeFollowedSeries();
        verify(this.seriesRepository).clear();
    }

    @Test
    public void seriesFollowingListenersMustBeNotifiedAfterAWipe() throws ParsingFailedException,
                                                                          ConnectionFailedException,
                                                                          SeriesNotFoundException {
        Series series1 = mock(Series.class);
        Series series2 = mock(Series.class);

        doReturn(Arrays.asList(series1, series2)).when(this.seriesRepository).getAll();

        this.followSeriesService.wipeFollowedSeries();

        verify(this.seriesFollowingListener).onStopFollowing(series1);
        verify(this.seriesFollowingListener).onStopFollowing(series2);
    }

    // Observer

    @Test(expected=IllegalArgumentException.class)
    public void cannotRegisterNullFollowingListeners() {
        this.followSeriesService.registerSeriesFollowingListener(null);
    }
}