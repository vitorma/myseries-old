package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import junit.framework.TestCase;
import br.edu.ufcg.aweseries.thetvdb.UrlSupplier;

public class UrlSupplierTest extends TestCase {
	
	private static final String API_KEY = "6F2B5A871C96FB05";

	public void testNullPosterFilename() {
		UrlSupplier supplier = new UrlSupplier(API_KEY);
		assertThat(supplier.getSeriesPosterUrl(null), nullValue());
	}

	public void testEmptyPosterFilename() {
		UrlSupplier supplier = new UrlSupplier(API_KEY);
		assertThat(supplier.getSeriesPosterUrl(""), nullValue());
	}

	public void testWhitespacesOnlyPosterFilename() {
		UrlSupplier supplier = new UrlSupplier(API_KEY);
		assertThat(supplier.getSeriesPosterUrl("   \t"), nullValue());
	}
}
