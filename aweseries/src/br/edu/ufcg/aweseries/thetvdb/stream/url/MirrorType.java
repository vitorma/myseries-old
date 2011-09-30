package br.edu.ufcg.aweseries.thetvdb.stream.url;

public enum MirrorType {
    XML(1), BANNER(2), ZIP(4);

    private int mask;

    MirrorType(int mask) {
        this.mask = mask;
    }

    public boolean matches(int i) {
        return (i & this.mask) != 0;
    }
}