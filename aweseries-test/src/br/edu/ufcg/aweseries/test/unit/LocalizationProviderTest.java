package br.edu.ufcg.aweseries.test.unit;

import java.util.Locale;

import br.edu.ufcg.aweseries.LocalizationProvider;
import junit.framework.TestCase;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class LocalizationProviderTest extends TestCase {

    public void testReturningSystemLanguage() {
        LocalizationProvider lp = new LocalizationProvider();

        assertThat(lp.language(), equalTo(Locale.getDefault().getLanguage()));
    }
}
