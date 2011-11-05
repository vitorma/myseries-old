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
        seriesSet = new SeriesSet();
    }

    @Test
    public void testSeriesSet() {
        Assert.assertTrue(seriesSet.isEmpty());
    }

    @Test
    public void testSize() {
        for (int i=1; i<=10; i++) {
            seriesSet.add(mockSeries(String.valueOf(i)));
            Assert.assertEquals(i, seriesSet.size());
        }

        for (int i=10; i>=1; i--) {
            seriesSet.remove(mockSeries(String.valueOf(i)));
            Assert.assertEquals(i-1, seriesSet.size());
        }
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(seriesSet.isEmpty());

        for (int i=1; i<=10; i++) {
            seriesSet.add(mockSeries(String.valueOf(i)));
            Assert.assertFalse(seriesSet.isEmpty());
        }

        for (int i=10; i>=1; i--) {
            Assert.assertFalse(seriesSet.isEmpty());
            seriesSet.remove(mockSeries(String.valueOf(i)));
        }

        Assert.assertTrue(seriesSet.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testContainsNullSeries() {
        seriesSet.contains(null);
    }

    @Test
    public void testContains() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.add(s);
            Assert.assertTrue(seriesSet.contains(s));
        }

        for (int i=10; i>=1; i--) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.remove(s);
            Assert.assertFalse(seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSeriesWithNullId() {
        seriesSet.get(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonExistentSeries() {
        seriesSet.get("1");
    }

    @Test
    public void testGet() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.add(s);
            Assert.assertEquals(s, seriesSet.get(String.valueOf(i)));
        }
    }

    @Test
    public void testGetAll() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.add(s);
        }

        for (Series s : seriesSet) {
            Assert.assertTrue(seriesSet.getAll().contains(s));
        }

        for (Series s : seriesSet.getAll()) {
            Assert.assertTrue(seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullSeries() {
        seriesSet.add(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddAlreadyExistentSeries() {
        seriesSet.add(mockSeries("1"));
        seriesSet.add(mockSeries("1"));
    }

    @Test
    public void testAdd() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.add(s);
            Assert.assertTrue(seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddANullSeriesCollection() {
        seriesSet.addAll(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddASeriesCollectionWithAtLeastOneNullSeries() {
        List<Series> l = new ArrayList<Series>();
        l.add(null);
        seriesSet.addAll(l);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddASeriesCollectionWithAtLeastOneExistentSeries() {
        Series s = mockSeries("1");
        seriesSet.add(mockSeries("1"));
        List<Series> l = new ArrayList<Series>();
        l.add(s);
        seriesSet.addAll(l);
    }

    @Test
    public void testAddAll() {
        List<Series> l = new ArrayList<Series>();

        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            l.add(s);
        }

        seriesSet.addAll(l);

        for (Series s : l) {
            Assert.assertTrue(seriesSet.contains(s));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNullSeries() {
        seriesSet.remove(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveNonExistentSeries() {
        seriesSet.remove(mockSeries("1"));
    }

    @Test
    public void testRemove() {
        for (int i=1; i<=10; i++) {
            Series s = mockSeries(String.valueOf(i));
            seriesSet.add(s);
            seriesSet.remove(s);
            Assert.assertFalse(seriesSet.contains(s));
        }
    }

    @Test
    public void testRemoveAll() {
        for (int i=1; i<=10; i++) {
            seriesSet.add(mockSeries(String.valueOf(i)));
        }

        seriesSet.clear();

        for (int i=1; i<=10; i++) {
            Assert.assertFalse(seriesSet.contains(mockSeries(String.valueOf(i))));
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void testIteratorWithoutNext() {
        seriesSet.iterator().next();
    }

    @Test
    public void testIterator() {
        Assert.assertFalse(seriesSet.iterator().hasNext());

        for (int i=1; i<=10; i++) {
            seriesSet.add(mockSeries(String.valueOf(i)));
        }

        Iterator<Series> it = seriesSet.iterator();
        for (int i=1; i<=10; i++) {
            Assert.assertTrue(it.hasNext());
            it.next();
        }

        List<Series> l = new ArrayList<Series>();
        for (Series s : seriesSet) {
            l.add(s);
        }
        Assert.assertEquals(seriesSet.size(), l.size());
    }
}
