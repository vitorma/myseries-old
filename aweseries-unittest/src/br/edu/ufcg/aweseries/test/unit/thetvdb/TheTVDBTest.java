/*
 *   TheTVDBTest.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.test.unit.thetvdb;

import static org.mockito.Mockito.mock;
import junit.framework.Assert;

import org.junit.Test;

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

    // getFullSeries ------------------------------------------------------------------------------

    @Test
    public void testGettingNullFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        Assert.assertNull("TheTVDB should return a null Series for a null seriesId",
                theTVBD.getSeries(null));
    }

    @Test
    public void testGettingBlankFullSeries() {
        StreamFactory streamFactoryMock = mock(StreamFactory.class);

        TheTVDB theTVBD = new TheTVDB(streamFactoryMock);
        Assert.assertNull("TheTVDB should return a null Series for a blank seriesId",
                theTVBD.getSeries("   \t \t "));
    }
}
