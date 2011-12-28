/*
 *   KeyValueParserTest.java
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

package br.edu.ufcg.aweseries.test;

import br.edu.ufcg.aweseries.test.KeyValueParser.KeyValuePair;
import junit.framework.TestCase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

public class KeyValueParserTest extends TestCase {

    private KeyValueParser parser;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.parser = new KeyValueParser();
    }

    public void testReturnCorrectKeyValuePairForStraightforwardUsage() {
        KeyValuePair returnedPair = this.parser.parse("key : value");

        assertThat(returnedPair.key, equalTo("key"));
        assertThat(returnedPair.value, equalTo("value"));
    }

    public void testReturnCorrectKeyValuePairForValidUsage() {
        KeyValuePair returnedPair = this.parser.parse("key key : value value : value");

        assertThat(returnedPair.key, equalTo("key key"));
        assertThat(returnedPair.value, equalTo("value value : value"));
    }

    public void testMalformedAttributeThrowsIllegalArgumentException0() {
        try {
            this.parser.parse(null);
            fail("Should have thrown an exception for malformed key-value String");
        } catch (IllegalArgumentException e) {}
    }

    public void testMalformedAttributeThrowsIllegalArgumentException1() {
        try {
            this.parser.parse("");
            fail("Should have thrown an exception for malformed key-value String");
        } catch (IllegalArgumentException e) {}
    }

    public void testMalformedAttributeThrowsIllegalArgumentException2() {
        try {
            this.parser.parse("id");
            fail("Should have thrown an exception for malformed key-value String");
        } catch (IllegalArgumentException e) {}
    }

    public void testMalformedAttributeThrowsIllegalArgumentException3() {
        try {
            this.parser.parse(":");
            fail("Should have thrown an exception for malformed key-value String");
        } catch (IllegalArgumentException e) {}
    }

    public void testMalformedAttributeThrowsIllegalArgumentException4() {
        try {
            this.parser.parse(" : id");
            fail("Should have thrown an exception for malformed key-value String");
        } catch (IllegalArgumentException e) {}
    }

}
