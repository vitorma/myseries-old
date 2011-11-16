/*
 *   MirrorBuilder.java
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

package br.edu.ufcg.aweseries.thetvdb.stream.url;



public class MirrorBuilder {
    private String path;
    private int typeMask;

    public MirrorBuilder withPath(String path) {
        this.path = path;
        return this;
    }

    public MirrorBuilder withTypeMask(int typeMask) {
        this.typeMask = typeMask;
        return this;
    }

    public MirrorBuilder withTypeMask(String typeMask) {
        try {
            this.typeMask = Integer.valueOf(typeMask);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("type mask should be an integer");
        }
        return this;
    }

    public Mirror build() {
        return new Mirror(this.path, this.typeMask);
    }
}
