/*
 *   NumbersTest.java
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

import br.edu.ufcg.aweseries.util.Numbers;

public class NumbersTest {

	@Test
	public void testParseInt() {
		final int alternative = -2;

		Assert.assertEquals(alternative, Numbers.parseInt(null, alternative));
		Assert.assertEquals(alternative, Numbers.parseInt("", alternative));
		Assert.assertEquals(alternative, Numbers.parseInt("a", alternative));
		Assert.assertEquals(alternative, Numbers.parseInt("-1.0", alternative));
		Assert.assertEquals(alternative, Numbers.parseInt("-2147483649", alternative));
		Assert.assertEquals(alternative, Numbers.parseInt("2147483648", alternative));

		Assert.assertEquals(-2147483648, Numbers.parseInt("-2147483648", alternative));
		Assert.assertEquals(2147483647, Numbers.parseInt("2147483647", alternative));
		Assert.assertEquals(-1, Numbers.parseInt("-0000000000000000000000000001", alternative));
	}
}
