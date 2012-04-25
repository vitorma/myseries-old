/*
 *   AndroidLocalizationProvider.java
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

package mobi.myseries.application;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import mobi.myseries.R;

public class AndroidLocalizationProvider implements LocalizationProvider {

    @Override
    public String language() {
        return Locale.getDefault().getLanguage();
    }

    @Override
    public DateFormat dateFormat() {
        return new SimpleDateFormat(App.environment().context().getString(R.string.date_format_short));
    }
}
