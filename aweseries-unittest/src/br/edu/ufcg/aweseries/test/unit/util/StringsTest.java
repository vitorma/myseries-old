package br.edu.ufcg.aweseries.test.unit.util;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.util.Strings;

public class StringsTest {

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
}
