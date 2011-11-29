package br.edu.ufcg.aweseries.test.unit.thetvdb;

import org.junit.Test;

import br.edu.ufcg.aweseries.thetvdb.Language;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class LanguageTests {

    @Test
    public void languageFromLowerCaseStringReturnsRightValue() {
        assertThat(Language.from("en"), equalTo(Language.EN));
        assertThat(Language.from("pt"), equalTo(Language.PT));
        assertThat(Language.from("es"), equalTo(Language.ES));
    }

    @Test
    public void languageFromUpperCaseStringReturnsRightValue() {
        assertThat(Language.from("EN"), equalTo(Language.EN));
        assertThat(Language.from("PT"), equalTo(Language.PT));
        assertThat(Language.from("ES"), equalTo(Language.ES));
    }

    @Test
    public void languageAbbreviationIsLowerCase() {
        assertThat(Language.EN.abbreviation(), equalTo("en"));
        assertThat(Language.PT.abbreviation(), equalTo("pt"));
        assertThat(Language.ES.abbreviation(), equalTo("es"));
    }
}
