/*
 *   Day.java
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

package mobi.myseries.application.schedule;

import java.util.Date;

import mobi.myseries.shared.HasDate;

public class Day implements HasDate {
    private Date date;

    public Day(Date date) {
        this.date = date;
    }

    @Override
    public Date getDate() {
        return this.date;
    }

    @Override
    public int hashCode() {
        return this.date != null ? this.date.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
               obj.getClass() == Day.class &&
               this.date == null ? ((Day) obj).date == null : this.date.equals(((Day) obj).date);
    }
}
