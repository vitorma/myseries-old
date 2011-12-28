/*
 *   TheTVDBTest.java
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


package br.edu.ufcg.aweseries.test.unit.thetvdb;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import br.edu.ufcg.aweseries.SeriesNotFoundException;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TheTVDBTest {

    // Constructor --------------------------------------------------------------------------------

    @Test(expected = IllegalArgumentException.class)
    public void testNullApiKey() {
        new TheTVDB((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStreamFactory() {
        new TheTVDB((StreamFactory) null);
    }

    // TODO: test everything

    @Test(expected=IllegalArgumentException.class)
    public void testGettingNullFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);

        theTVBD.fetchSeries(null, "en");
    }

    @Test(expected=SeriesNotFoundException.class)
    public void testGettingBlankFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        theTVBD.fetchSeries("   \t \t ", "en");
    }
}
