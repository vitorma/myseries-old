/*
 *   Mirror.java
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


package br.edu.ufcg.aweseries.thetvdb.stream.url;

public class Mirror {
    private String path;
    private int typeMask;

    public Mirror(String path, int typeMask) {
        if (path == null) {
            throw new IllegalArgumentException("path should not be null");
        }

        this.path = path;
        this.typeMask = typeMask;
    }

    public String getPath() {
        return this.path;
    }

    public int getTypeMask() {
        return this.typeMask;
    }

    @Override
    public int hashCode() {
        return this.getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Mirror &&
               ((Mirror) obj).getPath().equals(this.getPath());
    }
}
