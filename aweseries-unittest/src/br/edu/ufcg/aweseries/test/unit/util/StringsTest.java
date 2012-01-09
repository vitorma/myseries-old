/*
 *   StringsTest.java
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

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.util.Strings;

public class StringsTest {

	@Test(expected = IllegalArgumentException.class)
    public void testIsNullEmpty() {
        Strings.isEmpty(null);
    }

    @Test
    public void testIsEmpty() {
        Assert.assertTrue(Strings.isEmpty(""));
        Assert.assertFalse(Strings.isEmpty("                  "));
        Assert.assertFalse(Strings.isEmpty("0"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIsNullBlank() {
        Strings.isBlank(null);
    }

    @Test
    public void testIsBlank() {
        Assert.assertTrue(Strings.isBlank(""));
        Assert.assertTrue(Strings.isBlank("                  "));
        Assert.assertFalse(Strings.isBlank("0"));
        Assert.assertFalse(Strings.isBlank("  0  "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeNullPipeSeparatedString() {
        Strings.normalizePipeSeparated(null);
    }

    @Test
    public void testNormalizeEmptyPipeSeparatedString() {
        final String normalized = "";

        Assert.assertEquals(normalized, Strings.normalizePipeSeparated(""));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("      "));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("|"));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("   |      "));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("||"));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("   ||  "));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("|   |"));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("   |   |    "));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("|||||"));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("   |||||||||  "));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated("|   |  | ||     |"));
        Assert.assertEquals(normalized, Strings.normalizePipeSeparated(" ||||||   |  | ||    |  "));
    }

    @Test
    public void testNormalizeSinglePipeSeparatedString() {
        final String normalized = "single pipe separated";

        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("single pipe separated"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("   single pipe separated  "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|single pipe separated|"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("         |single pipe separated|    "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("| single pipe separated   |"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("   |      single pipe separated   |      "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|single pipe separated"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       |single pipe separated   "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       |    single pipe separated      "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("single pipe separated|"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("     single pipe separated|       "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("     single pipe separated     |       "));
    }

    @Test
    public void testNormalizePipeSeparatedString() {
        final String normalized = "pipe, separated, string";

        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|pipe|separated|string|"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("    |pipe|separated|string|   "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|   pipe|   separated  |string  |"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("     |   pipe|   separated  |string  |    "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|pipe|separated|string"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       |pipe|separated|string     "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("|  pipe  |  separated  |  string"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("    |   pipe | separated  |  string   "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("pipe|separated|string|"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       pipe|separated|string|     "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("pipe  |  separated  |  string   |"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       pipe | separated  |  string   |   "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("pipe|separated|string"));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("       pipe|separated|string     "));
        Assert.assertEquals(normalized,
                Strings.normalizePipeSeparated("    pipe  |     separated  |  string   "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceNullStringWithNullSurrogate() {
    	Strings.replaceIfNull(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReplaceNonNullStringWithNullSurrogate() {
    	Strings.replaceIfNull("non null string", null);
    }

    @Test
    public void testReplaceNullStringWithNonNullSurrogate() {
    	final String surrogate = "non null string";
    	Assert.assertEquals(surrogate, Strings.replaceIfNull(null, surrogate));
    }

    @Test
    public void testReplaceNonNullStringWithNonNullSurrogate() {
    	final String string = "non null string"; 
    	Assert.assertEquals(string, Strings.replaceIfNull(string, "surrogate"));
    }
}
