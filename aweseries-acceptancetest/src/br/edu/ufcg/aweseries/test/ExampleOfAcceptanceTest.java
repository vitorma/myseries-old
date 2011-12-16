package br.edu.ufcg.aweseries.test;

import junit.framework.Assert;
import junit.framework.TestCase;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.LocalizationProvider;
import br.edu.ufcg.aweseries.thetvdb.Language;

public class ExampleOfAcceptanceTest extends TestCase {

    private TheTVDBDouble theTVDB;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        this.setUpAcceptanceTestingEnvironment();

        this.theTVDB = new TheTVDBDouble();
    }

    public void testSearchSeries() {
        SearchUI searchUI = new SearchUI();

        // Given
        this.theTVDB.createSeries(Language.EN, "name : A Great Series");

        // When
        searchUI.fillSearchFieldWith("Great");
        searchUI.search();

        // Then
        Assert.assertEquals("A Great Series", searchUI.firtResult());
    }

    // Temporary Garbage ------------------------------------------------------------

    private class EnglishLocalizationProvider implements LocalizationProvider {

        @Override
        public String language() {
            return "en";
        }        
    }

    private void setUpAcceptanceTestingEnvironment() {
        App.environment().setLocalizationTo(new EnglishLocalizationProvider());
        //App.environment().setTheTVDBTo()
    }

    private class SearchUI {

        public void fillSearchFieldWith(String string) {
            // TODO Auto-generated method stub
            
        }

        public String firtResult() {
            // TODO Auto-generated method stub
            return null;
        }

        public void search() {
            // TODO Auto-generated method stub
            
        }
        
    }
}
