/*
 *   Objects.java
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

public class Objects {

    public static <T> T nullSafe(T actual, T safe) {
        if (safe == null) {
            throw new IllegalArgumentException("safe should not be null");
        }

        return actual != null ? actual : safe;
    }

    public static <T> boolean areDifferent(T left, T right) {
        if (left == null) {
            return right != null;
        }

        if (right == null) {
            return true;
        }

        return !left.equals(right);
    }
}
