/*
 *   AndroidLocalizationProviderTest.java
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