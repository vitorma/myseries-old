package br.edu.ufcg.aweseries.thetvdb;


public class Series {

    private String id;
	private String name;
    private String genre;
    private String airsDay;
    private String airsTime;
    private String network;
    private String actors;

	public String getActors() {
        return actors;
    }

    public String getAirsDay() {
        return airsDay;
    }

    public String getAirsTime() {
        return airsTime;
    }

    public String getGenre() {
        return this.genre;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getNetwork() {
        return network;
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
    
    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String seriesName) {
        this.name = seriesName;
    }

    public void setNetwork(String network) {
        this.network = network;
    }
    
    @Override
    public String toString() {
        return this.getName();
    }
}
