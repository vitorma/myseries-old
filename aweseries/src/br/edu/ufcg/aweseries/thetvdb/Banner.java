package br.edu.ufcg.aweseries.thetvdb;

public final class Banner {
	private String id;
	private String path;
	private String type;
	private String type2;
	private String season;

	public String getId() {
		return this.id;
	}

	public String getPath() {
		return this.path;
	}

	public String getType() {
		return this.type;
	}

	public String getType2() {
		return this.type2;
	}

	public String getSeason() {
		return this.season;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType2(String type2) {
		this.type2 = type2;
	}

	public void setSeason(String season) {
		this.season = season;
	}

	public Banner copy() {
		Banner b = new Banner();
		b.setId(this.id);
		b.setPath(this.path);
		b.setType(this.type);
		b.setType2(this.type2);
		b.setSeason(this.season);
		return b;
	}

	//Generated hashCode and equals --------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.id == null) ? 0 : this.id.hashCode());
		result = prime * result
				+ ((this.path == null) ? 0 : this.path.hashCode());
		result = prime * result
				+ ((this.season == null) ? 0 : this.season.hashCode());
		result = prime * result
				+ ((this.type == null) ? 0 : this.type.hashCode());
		result = prime * result
				+ ((this.type2 == null) ? 0 : this.type2.hashCode());
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
		Banner other = (Banner) obj;
		if (this.id == null) {
			if (other.id != null)
				return false;
		} else if (!this.id.equals(other.id))
			return false;
		if (this.path == null) {
			if (other.path != null)
				return false;
		} else if (!this.path.equals(other.path))
			return false;
		if (this.season == null) {
			if (other.season != null)
				return false;
		} else if (!this.season.equals(other.season))
			return false;
		if (this.type == null) {
			if (other.type != null)
				return false;
		} else if (!this.type.equals(other.type))
			return false;
		if (this.type2 == null) {
			if (other.type2 != null)
				return false;
		} else if (!this.type2.equals(other.type2))
			return false;
		return true;
	}
}
