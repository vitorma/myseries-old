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

package mobi.myseries.domain.source;

import mobi.myseries.shared.Validate;

public enum Language {
    GERMAN("de"),
    SPANISH("es"),
    PORTUGUESE("pt"),
    ENGLISH("en");

    private String abbreviation;

    private Language(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String abbreviation() {
        return this.abbreviation;
    }

    public static Language from(String abbreviation) throws UnsupportedLanguageException {
        Validate.isNonNull(abbreviation, "abbreviation");

        for (Language l : values()) {
            if (l.abbreviation.equalsIgnoreCase(abbreviation)) return l;
        }

        throw new UnsupportedLanguageException(abbreviation);
    }

    public static Language from(String abbreviation, Language alternative) {
        Validate.isNonNull(alternative, "alternative");

        try {
            return from(abbreviation);
        } catch (UnsupportedLanguageException e) {
            return alternative;
        }
    }
}

