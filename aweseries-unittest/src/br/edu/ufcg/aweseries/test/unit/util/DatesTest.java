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
	private static final String STRING_DATE = "01/01/2011";
	private static final Date TODAY = new Date();

	@Test(expected = IllegalArgumentException.class)
	public void testParseDateFromStringWithNullFormat() {
		Dates.parseDate(STRING_DATE, null, TODAY);
	}

	@Test
	public void testParseDateFromString() throws ParseException {
		Assert.assertEquals(FORMAT.parse(STRING_DATE), Dates.parseDate(STRING_DATE, FORMAT, TODAY));
		Assert.assertEquals(TODAY, Dates.parseDate(null, FORMAT, TODAY));
		Assert.assertEquals(TODAY, Dates.parseDate("invalid date", FORMAT, TODAY));
		Assert.assertEquals(TODAY, Dates.parseDate("2011/01/01", FORMAT, TODAY));
		Assert.assertEquals(null, Dates.parseDate("invalid date", FORMAT, null));
		Assert.assertEquals(null, Dates.parseDate(null, FORMAT, null));
	}

	@Test
	public void testParseDateFromLong() {
		Assert.assertEquals(TODAY, Dates.parseDate(null, TODAY));
		Assert.assertEquals(new Date(0L), Dates.parseDate(0L, TODAY));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToStringWithNullFormat() {
		Dates.toString(TODAY, null, STRING_DATE);
	}

	@Test
	public void testToString() throws ParseException {
		Date date = Dates.parseDate("05/01/2012", FORMAT, TODAY);
		Assert.assertEquals(FORMAT.format(date), Dates.toString(date, FORMAT, STRING_DATE));
		Assert.assertEquals(STRING_DATE, Dates.toString(null, FORMAT, STRING_DATE));
		Assert.assertEquals(null, Dates.toString(null, FORMAT, null));
	}

	@Test
	public void testCompare() {
		Date d1 = new Date(1L);
		Date d2 = new Date(2L);

		Assert.assertEquals(-1, Dates.compare(d1, null));
		Assert.assertEquals(0, Dates.compare(null, null));
		Assert.assertEquals(1, Dates.compare(null, d1));

		Assert.assertEquals(-1, Dates.compare(d1, d2));
		Assert.assertEquals(0, Dates.compare(d1, d1));
		Assert.assertEquals(1, Dates.compare(d2, d1));
	}
}
