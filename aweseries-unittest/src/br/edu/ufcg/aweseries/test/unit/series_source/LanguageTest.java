/*
 *   LanguageTest.java
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

package br.edu.ufcg.aweseries.test.unit.series_source;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import br.edu.ufcg.aweseries.series_source.Language;
import br.edu.ufcg.aweseries.series_source.UnsupportedLanguageException;

public class LanguageTest {

    //Getting abbreviation----------------------------------------------------------------------------------------------

    @Test
    public void languageAbbreviationIsLowerCase() {
        assertThat(Language.ENGLISH.abbreviation(), equalTo("en"));
        assertThat(Language.PORTUGUESE.abbreviation(), equalTo("pt"));
        assertThat(Language.SPANISH.abbreviation(), equalTo("es"));
        assertThat(Language.GERMAN.abbreviation(), equalTo("de"));
    }

    //Getting language from abbreviation--------------------------------------------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingLanguageFromNullAbbreviationCausesIllegalArgumentException() {
        Language.from(null);
    }

    @Test(expected=UnsupportedLanguageException.class)
    public void gettingLanguageFromAbbreviationOfUnsupportedLanguageCausesUnsupportedLanguageException() {
        Language.from("zz");
    }

    @Test
    public void fromRightAbbreviationDespiteOfItsCaseItShouldGetTheRightLanguage() {
        assertThat(Language.from("en"), equalTo(Language.ENGLISH));
        assertThat(Language.from("pt"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("es"), equalTo(Language.SPANISH));
        assertThat(Language.from("de"), equalTo(Language.GERMAN));

        assertThat(Language.from("EN"), equalTo(Language.ENGLISH));
        assertThat(Language.from("PT"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("ES"), equalTo(Language.SPANISH));
        assertThat(Language.from("DE"), equalTo(Language.GERMAN));

        assertThat(Language.from("eN"), equalTo(Language.ENGLISH));
        assertThat(Language.from("pT"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("eS"), equalTo(Language.SPANISH));
        assertThat(Language.from("dE"), equalTo(Language.GERMAN));

        assertThat(Language.from("En"), equalTo(Language.ENGLISH));
        assertThat(Language.from("Pt"), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("Es"), equalTo(Language.SPANISH));
        assertThat(Language.from("De"), equalTo(Language.GERMAN));
    }

    //Getting language from abbreviation with alternative language to return--------------------------------------------

    @Test(expected=IllegalArgumentException.class)
    public void gettingLanguageFromNullAbbreviationCausesIllegalArgumentExceptionDespiteOfTheAlternativeLanguage() {
        Language.from(null, Language.ENGLISH);
    }

    @Test(expected=IllegalArgumentException.class)
    public void gettingLanguageFromRightAbbreviationCausesIllegalArgumentExceptionIfTheAlternativeLanguageIsNull() {
        Language.from("en", null);
    }

    @Test
    public void fromAbbreviationOfUnsupportedLanguageItShouldGetTheAlternativeLanguage() {
        assertThat(Language.from("zz", Language.ENGLISH), equalTo(Language.ENGLISH));
        assertThat(Language.from("zz", Language.PORTUGUESE), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("zz", Language.SPANISH), equalTo(Language.SPANISH));
        assertThat(Language.from("zz", Language.GERMAN), equalTo(Language.GERMAN));
    }

    @Test
    public void fromRightAbbreviationDespiteOfItsCaseAndOfTheAlternativeLanguageItShouldGetTheRightLanguage() {
        assertThat(Language.from("en", Language.ENGLISH), equalTo(Language.ENGLISH));
        assertThat(Language.from("pt", Language.ENGLISH), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("es", Language.ENGLISH), equalTo(Language.SPANISH));
        assertThat(Language.from("de", Language.ENGLISH), equalTo(Language.GERMAN));

        assertThat(Language.from("EN", Language.ENGLISH), equalTo(Language.ENGLISH));
        assertThat(Language.from("PT", Language.ENGLISH), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("ES", Language.ENGLISH), equalTo(Language.SPANISH));
        assertThat(Language.from("DE", Language.ENGLISH), equalTo(Language.GERMAN));

        assertThat(Language.from("eN", Language.ENGLISH), equalTo(Language.ENGLISH));
        assertThat(Language.from("pT", Language.ENGLISH), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("eS", Language.ENGLISH), equalTo(Language.SPANISH));
        assertThat(Language.from("dE", Language.ENGLISH), equalTo(Language.GERMAN));

        assertThat(Language.from("En", Language.ENGLISH), equalTo(Language.ENGLISH));
        assertThat(Language.from("Pt", Language.ENGLISH), equalTo(Language.PORTUGUESE));
        assertThat(Language.from("Es", Language.ENGLISH), equalTo(Language.SPANISH));
        assertThat(Language.from("De", Language.ENGLISH), equalTo(Language.GERMAN));
    }
}
