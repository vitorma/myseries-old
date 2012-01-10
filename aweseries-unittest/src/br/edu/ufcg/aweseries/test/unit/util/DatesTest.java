/*
 *   DatesTest.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;

import br.edu.ufcg.aweseries.util.Dates;

public class DatesTest {
	private static final DateFormat FORMAT;
	static {
		FORMAT = new SimpleDateFormat("dd/MM/yyyy");
		FORMAT.setLenient(false);
	}
	private static final String DATE = "01/01/2011";
	private static final Date ALTERNATIVE = new Date();

	@Test(expected = IllegalArgumentException.class)
	public void testParseDateWithNullFormat() {
		Dates.parseDate(DATE, null, ALTERNATIVE);
	}

	@Test
	public void testParseDate() throws ParseException {
		Assert.assertEquals(FORMAT.parse(DATE), Dates.parseDate(DATE, FORMAT, ALTERNATIVE));
		Assert.assertEquals(ALTERNATIVE, Dates.parseDate(null, FORMAT, ALTERNATIVE));
		Assert.assertEquals(ALTERNATIVE, Dates.parseDate("invalid date", FORMAT, ALTERNATIVE));
		Assert.assertEquals(ALTERNATIVE, Dates.parseDate("2011/01/01", FORMAT, ALTERNATIVE));
		Assert.assertEquals(null, Dates.parseDate("invalid date", FORMAT, null));
	}
}
