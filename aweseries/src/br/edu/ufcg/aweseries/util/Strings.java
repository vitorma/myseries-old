/*
 *   Strings.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.util;

public abstract class Strings {
    public static boolean isBlank(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return Strings.isEmpty(string.trim());
    }

    public static String normalizePipeSeparated(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        final String[] items = string.trim().split("\\|");
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

    public static boolean isEmpty(String string) {
        if (string == null) {
            throw new IllegalArgumentException("string should not be null");
        }

        return string.equals("");
    }
}
