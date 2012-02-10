/*
 *   Language.java
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

package br.edu.ufcg.aweseries.series_source;

import br.edu.ufcg.aweseries.util.Validate;

public enum Language {
    DE,//German
    ES,//Spanish
    PT,//Portuguese
    EN;//English

    public static Language from(String abbreviation) {
        Validate.isNonNull(abbreviation, "abbreviation");

        return Language.valueOf(abbreviation.toUpperCase());
    }

    public static Language from(String abbreviation, Language alternative) {
        Validate.isNonNull(abbreviation, "abbreviation");
        Validate.isNonNull(alternative, "alternative");

        try {
            return Language.valueOf(abbreviation.toUpperCase());
        } catch (Exception e) {
            return alternative;
        }
    }

    public String abbreviation() {
        return this.toString().toLowerCase();
    }
}

