/*
 *   Dates.java
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

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class Dates {
    private static final String NULL_FORMAT_MESSAGE = "format should not be null";

    public static Date parseDate(String date, DateFormat format, Date alternative) {
        if (format == null) {
            throw new IllegalArgumentException(NULL_FORMAT_MESSAGE);
        }

        try {
            return format.parse(date);
        } catch (NullPointerException e) {
            return alternative;
        } catch (ParseException e) {
            return alternative;
        }
    }

    public static Date parseDate(Long date, Date alternative) {
        return date != null ? new Date(date) : alternative;
    }

    public static String toString(Date date, DateFormat format, String alternative) {
        if (format == null) {
            throw new IllegalArgumentException(NULL_FORMAT_MESSAGE);
        }

        return date != null ? format.format(date) : alternative;
    }
}