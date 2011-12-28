/*
 *   ExampleOfAcceptanceTest.java
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

import junit.framework.Assert;
import junit.framework.TestCase;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.LocalizationProvider;

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
        this.theTVDB.createSeries("en", "name : A Great Series");

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

        private String searchField;

        public void fillSearchFieldWith(String string) {
            // TODO Auto-generated method stub
            this.searchField = string;
        }

        public String firtResult() {
            // TODO Auto-generated method stub
            return App.environment().seriesProvider().searchSeries(this.searchField)[0].getName();
            //return null;
        }

        public void search() {
            // TODO Auto-generated method stub
            
        }
        
    }
}
