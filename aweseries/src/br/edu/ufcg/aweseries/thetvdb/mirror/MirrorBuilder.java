package br.edu.ufcg.aweseries.thetvdb.mirror;

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
