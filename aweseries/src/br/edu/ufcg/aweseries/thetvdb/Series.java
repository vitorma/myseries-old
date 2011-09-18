package br.edu.ufcg.aweseries.thetvdb;

import java.util.ArrayList;
import java.util.List;

public class Series {
    private String id;
    private String name;
    private String status;
    private String overview;
    private List<String> genres;
    private List<String> actors;
    private String airsDay;
    private String airsTime;
    private String firstAired;
    private String runtime;
    private String network;
    private String poster;

    public Series() {
        this.actors = new ArrayList<String>();
        this.genres = new ArrayList<String>();
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

    public String getOverview() {
        return this.overview;
    }

    public List<String> getGenres() {
        return this.genres;
    }

    public List<String> getActors() {
        return this.actors;
    }

    public String getActorsAsString(String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String actor : this.getActors()) {
            stringBuilder.append(actor);
            stringBuilder.append(separator);
        }

        return stringBuilder.substring(0,
                stringBuilder.length() - separator.length());
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

    public String getPoster() {
        return this.poster;
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

    public void addGenre(String genre) {
        this.genres.add(genre);
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

    public void setPoster(String poster) {
        this.poster = poster;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public void addActor(String s) {
        this.actors.add(s);
    }

    public String getGenresAsString(String separator) {
        StringBuilder builder = new StringBuilder();
        for (String s : this.genres) {
            builder.append(s);
            builder.append(separator);
        }

        return builder.substring(0, builder.length() - separator.length());

    }
}
