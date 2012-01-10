/*
 *   Strings.java
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

package br.edu.ufcg.aweseries.util;

public abstract class Strings {
    private static final String separator = "\\|";
    private static final String nullStringMessage = "string should not be null";
    private static final String nullSurrogateMessage = "surrogate should not be null";

    public static boolean isEmpty(String string) {
        if (string == null) {
            throw new IllegalArgumentException(nullStringMessage);
        }

        return string.equals("");
    }

    public static boolean isBlank(String string) {
        if (string == null) {
            throw new IllegalArgumentException(nullStringMessage);
        }

        return Strings.isEmpty(string.trim());
    }

    public static String normalizePipeSeparated(String string) {
        if (string == null) {
            throw new IllegalArgumentException(nullStringMessage);
        }

        final String[] items = string.trim().split(separator);
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            if (!isBlank(items[i])) {
                builder.append(items[i].trim());
                if (i < items.length - 1) {
                    builder.append(", ");
                }
            }
        }

        return builder.toString();
    }

    public static String replaceIfNull(String string, String surrogate) {
        if (surrogate == null) {
            throw new IllegalArgumentException(nullSurrogateMessage);
        }

        return string != null ? string : surrogate;
    }
}
