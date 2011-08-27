package br.edu.ufcg.aweseries.thetvdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public final class Mirrors {
    private static final Random RANDOM = new Random();
    private Map<MirrorType, List<Mirror>> mirrors;

    public Mirrors() {
        this.mirrors = new HashMap<MirrorType, List<Mirror>>();
        for (MirrorType type : MirrorType.values()) {
            this.mirrors.put(type, new ArrayList<Mirror>());
        }
    }

    public void add(Mirror mirror) {
        if (mirror == null) {return;}
        for (MirrorType type : MirrorType.values()) {
            if (type.matches(mirror.getTypeMask())) {
                this.mirrors.get(type).add(mirror);
            }
        }
    }

    public Mirror getRandomMirror(MirrorType type) {
        List<Mirror> candidates = this.mirrors.get(type);
        return candidates.get(RANDOM.nextInt(candidates.size()));
    }
}
