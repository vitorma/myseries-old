package br.edu.ufcg.aweseries.thetvdb;

public class Series {
    private String id;
    private String name;
    private String status;
    private String overview;
    private String genre;
    private String actors;
    private String airsDay;
    private String airsTime;
    private String firstAired;
    private String runtime;
    private String network;

    public String getId() {
		return this.id;
	}

    public String getName() {
		return this.name;
	}

    public String getStatus() {
		return this.status;
	}

    public String getOverview() {
		return this.overview;
	}

    public String getGenre() {
		return this.genre;
	}

    public String getActors() {
		return this.actors;
	}

    public String getAirsDay() {
		return this.airsDay;
	}

    public String getAirsTime() {
		return this.airsTime;
	}

    public String getFirstAired() {
		return this.firstAired;
	}

    public String getRuntime() {
		return this.runtime;
	}

    public String getNetwork() {
		return this.network;
	}

    public void setId(String id) {
		this.id = id;
	}

    public void setName(String name) {
		this.name = name;
	}

    public void setStatus(String status) {
		this.status = status;
	}

    public void setOverview(String overview) {
		this.overview = overview;
	}

    public void setGenre(String genre) {
		this.genre = genre;
	}

    public void setActors(String actors) {
		this.actors = actors;
	}

    public void setAirsDay(String airsDay) {
		this.airsDay = airsDay;
	}

    public void setAirsTime(String airsTime) {
		this.airsTime = airsTime;
	}

    public void setFirstAired(String firstAired) {
		this.firstAired = firstAired;
	}

    public void setRuntime(String runtime) {
		this.runtime = runtime;
	}

    public void setNetwork(String network) {
		this.network = network;
	}

    @Override
    public String toString() {
        return this.getName();
    }
}
