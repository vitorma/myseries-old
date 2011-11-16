/*
 *   UrlSupplierTest.java
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


import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.stream.url.UrlSupplier;

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
