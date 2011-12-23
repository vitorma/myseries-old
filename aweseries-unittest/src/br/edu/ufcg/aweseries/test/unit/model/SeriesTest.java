/*
 *   SeriesTest.java
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

package br.edu.ufcg.aweseries.test.unit.model;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesTest {
    private Series series1;
    private Series series2;
    private Series series3;
    private Series series4;

    @Before
    public final void setUp() {
        this.series1 = new Series.Builder().withId("id 1")
                                           .withName("series 1")
                                           .withStatus("status 1")
                                           .withAirsDay("airs day 1")
                                           .withAirsTime("airs time 1")
                                           .withFirstAired("first aired 1")
                                           .withRuntime("runtime 1")
                                           .withNetwork("network 1")
                                           .withOverview("overview 1")
                                           .withGenres("genres 1")
                                           .withActors("actors 1")
                                           .build();

        this.series2 = new Series("id 1", "series 2");
        this.series3 = new Series("id 1", "series");
        this.series4 = new Series("id 4", "series");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSeriesWithNullId() {
        new Series(null, "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSeriesWithBlankId() {
        new Series("  ", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSeriesWithNullName() {
        new Series("id", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSeriesWithBlankName() {
        new Series("id", " ");
    }

    @Test
    public final void testSeries() {
        Assert.assertNotNull(this.series1.getId());
        Assert.assertNotNull(this.series1.getName());
    }

    @Test
    public final void testGetId() {
        Assert.assertEquals("id 1", this.series1.getId());
    }

    @Test
    public final void testGetName() {
        Assert.assertEquals("series 1", this.series1.getName());
    }

    @Test
    public final void testGetStatus() {
        Assert.assertEquals("status 1", this.series1.getStatus());
    }

    @Test
    public final void testGetAirsDay() {
        Assert.assertEquals("airs day 1", this.series1.getAirsDay());
    }

    @Test
    public final void testGetAirsTime() {
        Assert.assertEquals("airs time 1", this.series1.getAirsTime());
    }

    @Test
    public final void testGetFirstAired() {
        Assert.assertEquals("first aired 1", this.series1.getFirstAired());
    }

    @Test
    public final void testGetRuntime() {
        Assert.assertEquals("runtime 1", this.series1.getRuntime());
    }

    @Test
    public final void testGetNetwork() {
        Assert.assertEquals("network 1", this.series1.getNetwork());
    }

    @Test
    public final void testGetOverview() {
        Assert.assertEquals("overview 1", this.series1.getOverview());
    }

    @Test
    public final void testGetGenres() {
        Assert.assertEquals("genres 1", this.series1.getGenres());
    }

    @Test
    public final void testGetActors() {
        Assert.assertEquals("actors 1", this.series1.getActors());
    }

// TODO This test should be migrated to work with the builder
    @Ignore
    @Test
    public final void testGetPoster() {
        Assert.assertNotNull(this.series1.getPoster());
    }

    @Test
    public final void testGetSeasons() {
        Assert.assertNotNull(this.series1.getSeasons());
    }

// TODO All these commented tests should be migrated to work with the builder

//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullStatus() {
//        this.series2.setStatus(null);
//    }
//
//    @Test
//    public final void testSetStatus() {
//        Assert.assertNull(this.series2.getStatus());
//        this.series2.setStatus("status 2");
//        Assert.assertEquals("status 2", this.series2.getStatus());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullAirsDay() {
//        this.series2.setAirsDay(null);
//    }
//
//    @Test
//    public final void testSetAirsDay() {
//        Assert.assertNull(this.series2.getAirsDay());
//        this.series2.setAirsDay("airs day 2");
//        Assert.assertEquals("airs day 2", this.series2.getAirsDay());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullAirsTime() {
//        this.series2.setAirsTime(null);
//    }
//
//    @Test
//    public final void testSetAirsTime() {
//        Assert.assertNull(this.series2.getAirsTime());
//        this.series2.setAirsTime("airs time 2");
//        Assert.assertEquals("airs time 2", this.series2.getAirsTime());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullFirstAired() {
//        this.series2.setFirstAired(null);
//    }
//
//    @Test
//    public final void testSetFirstAired() {
//        Assert.assertNull(this.series2.getFirstAired());
//        this.series2.setFirstAired("first aired 2");
//        Assert.assertEquals("first aired 2", this.series2.getFirstAired());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullRuntime() {
//        this.series2.setRuntime(null);
//    }
//
//    @Test
//    public final void testSetRuntime() {
//        Assert.assertNull(this.series2.getRuntime());
//        this.series2.setRuntime("runtime 2");
//        Assert.assertEquals("runtime 2", this.series2.getRuntime());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullNetwork() {
//        this.series2.setNetwork(null);
//    }
//
//    @Test
//    public final void testSetNetwork() {
//        Assert.assertNull(this.series2.getNetwork());
//        this.series2.setNetwork("network 2");
//        Assert.assertEquals("network 2", this.series2.getNetwork());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullOverview() {
//        this.series2.setOverview(null);
//    }
//
//    @Test
//    public final void testSetOverview() {
//        Assert.assertNull(this.series2.getOverview());
//        this.series2.setOverview("overview 2");
//        Assert.assertEquals("overview 2", this.series2.getOverview());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullGenres() {
//        this.series2.setGenres(null);
//    }
//
//    @Test
//    public final void testSetGenres() {
//        Assert.assertNull(this.series2.getGenres());
//        this.series2.setGenres("genres 2");
//        Assert.assertEquals("genres 2", this.series2.getGenres());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullActors() {
//        this.series2.setActors(null);
//    }
//
//    @Test
//    public final void testSetActors() {
//        Assert.assertNull(this.series2.getActors());
//        this.series2.setActors("actors 2");
//        Assert.assertEquals("actors 2", this.series2.getActors());
//    }
//
//    @Test
//    public final void testSetPoster() {
//        Poster p = this.mockPoster();
//        this.series1.setPoster(p);
//        Assert.assertEquals(p, this.series1.getPoster());
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public final void testSetNullSeasons() {
//        this.series2.setSeasons(null);
//    }
//
//    @Test
//    public final void testSetSeasons() {
//        Assert.assertNull(this.series2.getSeasons());
//        SeasonSet ss = this.mockSeasonSet(this.series2.getId());
//        this.series2.setSeasons(ss);
//        Assert.assertNotNull(this.series2.getSeasons());
//    }

    @Test
    public final void testEqualsObject() {
        Assert.assertTrue("series1 and series2 have the same id",
                this.series1.equals(this.series2));
        Assert.assertTrue("series2 and series3 have the same id",
                this.series2.equals(this.series3));
        Assert.assertFalse("series3 and series4 does not have the same id",
                this.series3.equals(this.series4));

        for (int i = 1; i <= 1000; i++) {
            Assert.assertTrue("equals should be consistently reflexive",
                    this.series1.equals(this.series1));
            Assert.assertTrue("equals should be consistently symmetric",
                    this.series2.equals(this.series1));
            Assert.assertTrue("equals should be consistently transitive",
                    this.series1.equals(this.series3));
            Assert.assertFalse(
                    "equals should consistently return false if the expected object is null",
                    this.series1.equals(null));
        }
    }

    @Test
    public final void testHashCode() {
        final int hashCode = this.series1.hashCode();
        for (int i = 1; i <= 1000; i++) {
            Assert.assertEquals(
                    "hashCode should consistently return the same value for the same object",
                    hashCode, this.series1.hashCode());
            Assert.assertEquals(
                    "hashCode should consistently return the same value for equal objects",
                    this.series1.hashCode(), this.series2.hashCode());
        }
    }

    @Test
    public final void testToString() {
        Assert.assertEquals(this.series1.getName(), this.series1.toString());
        Assert.assertEquals(this.series2.getName(), this.series2.toString());
        Assert.assertEquals(this.series3.getName(), this.series3.toString());
        Assert.assertEquals(this.series4.getName(), this.series4.toString());
    }
}
