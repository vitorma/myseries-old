package br.edu.ufcg.aweseries.test.unit.thetvdb;


import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class UrlSupplierTest {

    private static final String API_KEY = "6F2B5A871C96FB05";
    private static final UrlSupplier supplier = new UrlSupplier(UrlSupplierTest.API_KEY);

    @Test
    public void testEmptyPosterFilename() {
        assertThat(supplier.getSeriesPosterUrl(""), nullValue());
    }

    @Test
    public void testNullPosterFilename() {
        assertThat(supplier.getSeriesPosterUrl(null), nullValue());
    }

    @Test
    public void testWhitespacesOnlyPosterFilename() {
        assertThat(supplier.getSeriesPosterUrl("   \t"), nullValue());
    }
}
