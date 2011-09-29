package br.edu.ufcg.aweseries.thetvdb.mirror;

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
