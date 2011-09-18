package br.edu.ufcg.aweseries.test.unit;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class UrlSupplierTest {

    private static final String API_KEY = "6F2B5A871C96FB05";

    @Test
    public void testEmptyPosterFilename() {
        final UrlSupplier supplier = new UrlSupplier(UrlSupplierTest.API_KEY);
        Assert.assertThat(supplier.getSeriesPosterUrl(""),
                CoreMatchers.nullValue());
    }

    @Test
    public void testNullPosterFilename() {
        final UrlSupplier supplier = new UrlSupplier(UrlSupplierTest.API_KEY);
        Assert.assertThat(supplier.getSeriesPosterUrl(null),
                CoreMatchers.nullValue());
    }

    @Test
    public void testWhitespacesOnlyPosterFilename() {
        final UrlSupplier supplier = new UrlSupplier(UrlSupplierTest.API_KEY);
        Assert.assertThat(supplier.getSeriesPosterUrl("   \t"),
                CoreMatchers.nullValue());
    }
}
