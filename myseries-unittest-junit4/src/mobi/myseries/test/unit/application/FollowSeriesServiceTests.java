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

package mobi.myseries.test.unit.application;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;

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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.graphics.Bitmap;

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

    @Test
    public void errorsMustBeDetectedAfterSuccessfulFollowing() throws ParsingFailedException,
                                                                      ConnectionFailedException,
                                                                      SeriesNotFoundException {
        Series seriesToBeFollowed = mock(Series.class);

        when(this.seriesSource.fetchSeries(anyInt(), anyString()))
            .thenReturn(seriesToBeFollowed)
            .thenThrow(new SeriesNotFoundException());

        Series seriesToBeFollowed2 = mock(Series.class);

        this.followSeriesService.follow(seriesToBeFollowed);
        this.followSeriesService.follow(seriesToBeFollowed2);

        assertThat(this.followSeriesService.follows(seriesToBeFollowed2), is(false));

        verify(this.seriesFollowingListener, only()).onFollowing(seriesToBeFollowed);
    }

    @Test
    public void followedSeriesMustBeSaved() throws ParsingFailedException,
                                                   ConnectionFailedException,
                                                   SeriesNotFoundException {
        Series seriesToBeFollowed = mock(Series.class);

        doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(anyInt(), anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        verify(this.seriesRepository).insert(seriesToBeFollowed);
    }

    @Test
    public void seriesFollowingListenersMustBeNotifiedOfFollow() throws ParsingFailedException,
                                                                        ConnectionFailedException,
                                                                        SeriesNotFoundException {
        Series seriesToBeFollowed = mock(Series.class);

        doReturn(seriesToBeFollowed).when(this.seriesSource).fetchSeries(anyInt(), anyString());

        this.followSeriesService.follow(seriesToBeFollowed);

        verify(this.seriesFollowingListener).onFollowing(seriesToBeFollowed);
    }

    @Test
    public void whenASeriesIsFollowedTwiceItMustBeNotifiedEachTime() throws ParsingFailedException,
                                                                            ConnectionFailedException,
                                                                            SeriesNotFoundException {
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

    @Test(expected=IllegalArgumentException.class)
    public void cannotStopFollowingNullSeries() {
        this.followSeriesService.stopFollowing(null);
    }

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

        verify(this.seriesFollowingListener).onUnfollowing(followedSeries);
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

        verify(this.seriesFollowingListener).onUnfollowing(series1);
        verify(this.seriesFollowingListener).onUnfollowing(series2);
    }

    // Observer

    @Test(expected=IllegalArgumentException.class)
    public void cannotRegisterNullFollowingListeners() {
        this.followSeriesService.registerSeriesFollowingListener(null);
    }
}
