/*
 *   KeyValueParser.java
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

import br.edu.ufcg.aweseries.util.Strings;

public class KeyValueParser {

    public static class KeyValuePair {
        public String key;
        public String value;
    }

    private static final String MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE = "Malformed key value pair";

    private static String SEPARATOR = "\\s*:\\s*";

    public KeyValuePair parse(String pair) {
        if (pair == null) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        String[] parts = pair.split(SEPARATOR, 2);
        if (parts.length != 2) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        KeyValuePair parsedPair = new KeyValuePair();
        parsedPair.key = parts[0];
        parsedPair.value = parts[1];

        if (Strings.isBlank(parsedPair.key)) {
            throw new IllegalArgumentException(MALFORMED_ATTRIBUTE_EXCEPTION_MESSAGE);
        }

        return parsedPair;
    }
}