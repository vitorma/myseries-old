/*
 *   SeriesSetTest.java
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


package br.edu.ufcg.aweseries.test.unit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesSet;

public class SeriesSetTest {
    private SeriesSet seriesSet;

    private Series mockSeries(String id) {
        Series s = Mockito.mock(Series.class);
        Mockito.when(s.getId()).thenReturn(id);
        return s;
    }

    @Before
    public void setUp() {
        this.seriesSet = new SeriesSet();
    }

    @Test
    public void testSeriesSet() {
        Assert.assertTrue(this.seriesSet.isEmpty());
    }

    @Test
    public void testSize() {
        for (int i=1; i<=10; i++) {
            this.seriesSet.add(mockSeries(String.valueOf(i)));
            Assert.assertEquals(i, this.seriesSet.size());
        }

        for (int i=10; i>=1; i--) {
            this.seriesSet.remove(mockSeries(String.valueOf(i)));
            Assert.assertEquals(i-1, this.seriesSet.size());
        }
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(this.seriesSet.isEmpty());

        for (int i=1; i<=10; i++) {
            this.seriesSet.add(mockSeries(String.valueOf(i)));
            Assert.assertFalse(this.seriesSet.isEmpty());
        }

        for (int i=10; i>=1; i--) {
            Assert.assertFalse(this.seriesSet.isEmpty());
            this.seriesSet.remove(mockSeries(String.valueOf(i)));
        }

        Assert.assertTrue(this.seriesSet.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsNullSeries() {
        this.seriesSet.contains(null);
    }

    @Test
    public void testContains() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.add(s);
            Assert.assertTrue(this.seriesSet.contains(s));
        }

        for (int i=10; i>=1; i--) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.remove(s);
            Assert.assertFalse(this.seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSeriesWithNullId() {
        this.seriesSet.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonExistentSeries() {
        this.seriesSet.get("1");
    }

    @Test
    public void testGet() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.add(s);
            Assert.assertEquals(s, this.seriesSet.get(String.valueOf(i)));
        }
    }

    @Test
    public void testGetAll() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.add(s);
        }

        for (Series s : this.seriesSet) {
            Assert.assertTrue(this.seriesSet.getAll().contains(s));
        }

        for (Series s : this.seriesSet.getAll()) {
            Assert.assertTrue(this.seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullSeries() {
        this.seriesSet.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAlreadyExistentSeries() {
        this.seriesSet.add(mockSeries("1"));
        this.seriesSet.add(mockSeries("1"));
    }

    @Test
    public void testAdd() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.add(s);
            Assert.assertTrue(this.seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddANullSeriesCollection() {
        this.seriesSet.addAll(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddASeriesCollectionWithAtLeastOneNullSeries() {
        List<Series> l = new ArrayList<Series>();
        l.add(null);
        this.seriesSet.addAll(l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddASeriesCollectionWithAtLeastOneExistentSeries() {
        Series s = mockSeries("1");
        this.seriesSet.add(mockSeries("1"));
        List<Series> l = new ArrayList<Series>();
        l.add(s);
        this.seriesSet.addAll(l);
    }

    @Test
    public void testAddAll() {
        List<Series> l = new ArrayList<Series>();

        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            l.add(s);
        }

        this.seriesSet.addAll(l);

        for (Series s : l) {
            Assert.assertTrue(this.seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullSeries() {
        this.seriesSet.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentSeries() {
        this.seriesSet.remove(mockSeries("1"));
    }

    @Test
    public void testRemove() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            this.seriesSet.add(s);
            this.seriesSet.remove(s);
            Assert.assertFalse(this.seriesSet.contains(s));
        }
    }

    @Test
    public void testRemoveAll() {
        for (int i=1; i<=10; i++) {
            this.seriesSet.add(mockSeries(String.valueOf(i)));
        }

        this.seriesSet.clear();

        for (int i=1; i<=10; i++) {
            Assert.assertFalse(this.seriesSet.contains(mockSeries(String.valueOf(i))));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorWithoutNext() {
        this.seriesSet.iterator().next();
    }

    @Test
    public void testIterator() {
        Assert.assertFalse(this.seriesSet.iterator().hasNext());

        for (int i=1; i<=10; i++) {
            this.seriesSet.add(mockSeries(String.valueOf(i)));
        }

        Iterator<Series> it = this.seriesSet.iterator();
        for (int i=1; i<=10; i++) {
            Assert.assertTrue(it.hasNext());
            it.next();
        }

        List<Series> l = new ArrayList<Series>();
        for (Series s : this.seriesSet) {
            l.add(s);
        }
        Assert.assertEquals(this.seriesSet.size(), l.size());
    }
}
