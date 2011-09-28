package br.edu.ufcg.aweseries.test.unit.thetvdb.util;

import org.junit.Assert;
import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.util.Strings;

public class StringsTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeNullPipeSeparatedString() {
        Strings.normalizePipeSeparated(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeNoPipeSeparatedString() {
        Strings.normalizePipeSeparated("not|pipe|separated|string");
    }

    @Test
    public void testNormalizeEmptyPipeSeparatedString() {
        Assert.assertEquals("", Strings.normalizePipeSeparated(""));
        Assert.assertEquals("", Strings.normalizePipeSeparated("||"));
        Assert.assertEquals("", Strings.normalizePipeSeparated("|   |"));
    }

    @Test
    public void testNormalizeSinglePipeSeparatedString() {
        Assert.assertEquals("cleber",
                Strings.normalizePipeSeparated("|cleber|"));
        Assert.assertEquals("cleber",
                Strings.normalizePipeSeparated("| cleber   |"));
    }

    @Test
    public void testNormalizePipeSeparatedString() {
        Assert.assertEquals("cleber, gabriel, tiago",
                Strings.normalizePipeSeparated("|cleber|gabriel|tiago|"));
        Assert.assertEquals("cleber, gabriel, tiago",
                Strings.normalizePipeSeparated("| cleber  | gabriel|tiago  |"));
    }
}
