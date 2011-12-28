/*
 *   Mirrors.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class Mirrors {
    private static final Random RANDOM = new Random();
    private Map<MirrorType, List<Mirror>> mirrors;

    public Mirrors() {
        this.mirrors = new HashMap<MirrorType, List<Mirror>>();
        for (MirrorType type : MirrorType.values()) {
            this.mirrors.put(type, new ArrayList<Mirror>());
        }
    }

    public void add(Mirror mirror) {
        if (mirror == null) {
            throw new IllegalArgumentException("mirror should not be null");
        }

        for (MirrorType mt : this.getMirrorTypesFor(mirror.getTypeMask())) {
            this.mirrors.get(mt).add(mirror);
        }
    }

    private ArrayList<MirrorType> getMirrorTypesFor(int typeMask) {
        final ArrayList<MirrorType> mirrorTypes = new ArrayList<MirrorType>();

        for (MirrorType mt : MirrorType.values()) {
            if (mt.matches(typeMask)) {
                mirrorTypes.add(mt);
            }
        }

        return mirrorTypes;
    }

    public Mirror getRandomMirror(MirrorType type) {
        if (type == null) {
            throw new IllegalArgumentException("type should not be null");
        }

        List<Mirror> candidates = this.mirrors.get(type);
        return candidates.get(RANDOM.nextInt(candidates.size()));
    }
}
