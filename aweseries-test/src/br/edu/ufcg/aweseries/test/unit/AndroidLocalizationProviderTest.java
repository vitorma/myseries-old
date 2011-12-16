package br.edu.ufcg.aweseries.test.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Locale;

import junit.framework.TestCase;
import br.edu.ufcg.aweseries.AndroidLocalizationProvider;
import br.edu.ufcg.aweseries.LocalizationProvider;

public class AndroidLocalizationProviderTest extends TestCase {

    public void testReturningSystemLanguage() {
        LocalizationProvider lp = new AndroidLocalizationProvider();

        assertThat(lp.language(), equalTo(Locale.getDefault().getLanguage()));
    }
}
