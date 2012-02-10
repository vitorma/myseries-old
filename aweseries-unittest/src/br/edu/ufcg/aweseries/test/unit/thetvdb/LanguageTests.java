/*
 *   LanguageTests.java
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

import org.junit.Test;

import br.edu.ufcg.aweseries.series_source.Language;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LanguageTests {

    @Test
    public void languageFromLowerCaseAbbreviationReturnsRightValue() {
        assertThat(Language.from("en"), equalTo(Language.ENGLISH));
        assertThat(Language.from("pt"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("es"), equalTo(Language.SPANISH));
    }

    @Test
    public void languageFromUpperCaseAbbreviationReturnsRightValue() {
        assertThat(Language.from("EN"), equalTo(Language.ENGLISH));
        assertThat(Language.from("PT"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("ES"), equalTo(Language.SPANISH));
    }

    @Test(expected=IllegalArgumentException.class)
    public void languageFromNullAbbreviationThrowsIllegalArgumentException() {
        Language.from(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void languageFromInvalidAbbreviationThrowsIllegalArgumentException() {
        Language.from("zz");
    }

    @Test
    public void languageAbbreviationIsLowerCase() {
        assertThat(Language.ENGLISH.abbreviation(), equalTo("en"));
        assertThat(Language.PORTUGUESE.abbreviation(), equalTo("pt"));
        assertThat(Language.SPANISH.abbreviation(), equalTo("es"));
    }
}
