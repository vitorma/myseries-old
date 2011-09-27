package br.edu.ufcg.aweseries.thetvdb.series;

import br.edu.ufcg.aweseries.thetvdb.season.Seasons;

public class Series {
    private String id;
    private String name;
    private String status;
    private String airsDay;
    private String airsTime;
    private String firstAired;
    private String runtime;
    private String network;
    private String overview;
    private String genres;
    private String actors;
    private String poster;
    private Seasons seasons;

    public Series(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Series() {}

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return this.status;
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

    public String getOverview() {
        return this.overview;
    }

    public String getGenres() {
        return this.genres;
    }

    public String getActors() {
        return this.actors;
    }

    public String getPoster() {
        return this.poster;
    }

    public Seasons getSeasons() {
        return this.seasons;
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

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setSeasons(Seasons seasons) {
        // Is this the best approach? Talking about it.
        this.seasons = seasons;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Series) &&
               ((Series) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
