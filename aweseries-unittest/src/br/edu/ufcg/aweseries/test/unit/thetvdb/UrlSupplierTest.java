/*
 *   UrlSupplierTest.java
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

import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlSupplier;

public class UrlSupplierTest {

	private static final String API_KEY = "AK1";
	private static final UrlSupplier supplier = new UrlSupplier(UrlSupplierTest.API_KEY);

	@Test(expected=IllegalArgumentException.class)
	public void constructingAnUrlSupplierWithANullApiKeyCausesIllegalArgumentException() {
		new UrlSupplier(null);
	}

	@Test
	public void testEmptyPosterFilename() {
		assertThat(supplier.urlForPoster(""), nullValue());
	}

	@Test
	public void testNullPosterFilename() {
		assertThat(supplier.urlForPoster(null), nullValue());
	}

	@Test
	public void testWhitespacesOnlyPosterFilename() {
		assertThat(supplier.urlForPoster("   \t"), nullValue());
	}
}
