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

package mobi.myseries.shared;

public abstract class Strings {
    private static final String SEPARATOR = "\\|";

    public static boolean isEmpty(String string) {
        Validate.isNonNull(string, "string");

        return string.equals("");
    }

    public static boolean isBlank(String string) {
        Validate.isNonNull(string, "string");

        return Strings.isEmpty(string.trim());
    }

    public static String normalizePipeSeparated(String string) {
        Validate.isNonNull(string, "string");

        final String[] items = string.trim().split(SEPARATOR);
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
}
