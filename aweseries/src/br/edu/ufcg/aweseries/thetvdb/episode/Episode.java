package br.edu.ufcg.aweseries.thetvdb.episode;

public class Episode {
	private String id;
	private String name;
    private boolean viewed;
    private int seasonNumber;

    public Episode() {}

    public Episode(String id, String name) {
    	this.id = id;
    	this.name = name;
    }

    public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public boolean isViewed() {
        return this.viewed;
    }

    public void markAsViewed() {
        this.viewed = true;
    }

    public void markAsNotViewed() {
        this.viewed = false;        
    }

    public int getSeasonNumber() {
    	return this.seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
    	this.seasonNumber = seasonNumber;
    }

    public Episode copy() {
    	Episode episode = new Episode();
    	episode.id = this.id;
    	episode.seasonNumber = this.seasonNumber;
    	episode.viewed = this.viewed;
		return episode;
    }
}
