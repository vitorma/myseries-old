package br.edu.ufcg.aweseries.thetvdb.mirror;

/**
 * A TheTVDB mirror.
 *
 * @author Cleber Goncalves de Sousa
 */
public final class Mirror {
    private int typeMask;
    private String path;

    public int getTypeMask() {
        return this.typeMask;
    }

    public void setTypeMask(int typeMask) {
        this.typeMask = typeMask;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Mirror copy() {
    	Mirror m = new Mirror();
    	m.setPath(this.path);
    	m.setTypeMask(this.typeMask);
    	return m;
    }

    //Generated hashCode and equals --------------------------------------------

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.path == null) ? 0 : this.path.hashCode());
        result = prime * result + this.typeMask;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Mirror other = (Mirror) obj;
        if (this.path == null) {
            if (other.path != null)
                return false;
        } else if (!this.path.equals(other.path))
            return false;
        if (this.typeMask != other.typeMask)
            return false;
        return true;
    }
}
