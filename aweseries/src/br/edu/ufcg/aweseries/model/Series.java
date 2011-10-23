package br.edu.ufcg.aweseries.model;

import br.edu.ufcg.aweseries.util.Strings;

public class Series implements Comparable<Series> {
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
    private Poster poster;
    private Seasons seasons;

    public Series(String id, String name) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for series");
        }

        if (name == null || Strings.isBlank(name)) {
            throw new IllegalArgumentException("invalid name for series");
        }

        this.id = id;
        this.name = name;
    }

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

    public Poster getPoster() {
        return this.poster;
    }

    public Seasons getSeasons() {
        return this.seasons;
    }

    public void setStatus(String status) {
        if (status == null) {
            throw new IllegalArgumentException("invalid status for series");
        }

        this.status = status;
    }

    public void setAirsDay(String airsDay) {
        if (airsDay == null) {
            throw new IllegalArgumentException("invalid airs day for series");
        }

        this.airsDay = airsDay;
    }

    public void setAirsTime(String airsTime) {
        if (airsTime == null) {
            throw new IllegalArgumentException("invalid airs time for series");
        }

        this.airsTime = airsTime;
    }

    public void setFirstAired(String firstAired) {
        if (firstAired == null) {
            throw new IllegalArgumentException("invalid first aired for series");
        }

        this.firstAired = firstAired;
    }

    public void setRuntime(String runtime) {
        if (runtime == null) {
            throw new IllegalArgumentException("invalid runtime for series");
        }

        this.runtime = runtime;
    }

    public void setNetwork(String network) {
        if (network == null) {
            throw new IllegalArgumentException("invalid network for series");
        }

        this.network = network;
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("invalid overview for series");
        }

        this.overview = overview;
    }

    public void setGenres(String genres) {
        if (genres == null) {
            throw new IllegalArgumentException("invalid genres for series");
        }

        this.genres = genres;
    }

    public void setActors(String actors) {
        if (actors == null) {
            throw new IllegalArgumentException("invalid actors for series");
        }

        this.actors = actors;
    }

    public void setPoster(Poster poster) {
        this.poster = poster;
    }

    public void setSeasons(Seasons seasons) {
        if (seasons == null) {
            throw new IllegalArgumentException("invalid seasons for series");
        }

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

    //TODO: Test
    public boolean hasPoster() {
        return this.poster != null;
    }

    @Override
    public int compareTo(Series otherSeries) {
        return this.name.compareTo(otherSeries.getName());
    }   
}
