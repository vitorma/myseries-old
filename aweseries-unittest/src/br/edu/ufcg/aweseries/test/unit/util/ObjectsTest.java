/*
 *   ObjectsTest.java
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

package br.edu.ufcg.aweseries.test.unit.util;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.aweseries.util.Objects;

public class ObjectsTest {

	@Test(expected=IllegalArgumentException.class)
	public void testNullIsNotNullSafe() {
		Objects.nullSafe(new Object(), null);
	}

	@Test
	public void testNullSafe() {
		final Object safe = new Object();
		Assert.assertSame(safe, Objects.nullSafe(null, safe));
		Assert.assertSame(safe, Objects.nullSafe(safe, new Object()));
		Assert.assertNotSame(safe, Objects.nullSafe(new Object(), safe));
	}
}
