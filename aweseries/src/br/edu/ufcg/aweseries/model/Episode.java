package br.edu.ufcg.aweseries.model;

import br.edu.ufcg.aweseries.util.Strings;

public class Episode {
    private String id;
    private String seriesId;
    private int number;
    private int seasonNumber;
    private String name;
    private String firstAired;
    private String overview;
    private String director;
    private String writer;
    private String guestStars;
    private String poster;

    private boolean viewed;

    public Episode(String id, String seriesId, int number, int seasonNumber) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for episode");
        }

        if (seriesId == null || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for episode");
        }

        if (number < 0) {
            throw new IllegalArgumentException("invalid number for episode");
        }

        if (seasonNumber < 0) {
            throw new IllegalArgumentException("invalid season number for episode");
        }

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
        this.seasonNumber = seasonNumber;
    }

    public String getId() {
        return this.id;
    }

    public String getSeriesId() {
        return this.seriesId;
    }

    public int getNumber() {
        return this.number;
    }

    public int getSeasonNumber() {
        return this.seasonNumber;
    }

    public String getName() {
        return this.name;
    }

    public String getFirstAired() {
        return this.firstAired;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getDirector() {
        return this.director;
    }

    public String getWriter() {
        return this.writer;
    }

    public String getGuestStars() {
        return this.guestStars;
    }

    public String getPoster() {
        return this.poster;
    }

    public boolean isViewed() {
        return this.viewed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public void setGuestStars(String guestStars) {
        this.guestStars = guestStars;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }

    public void markAsViewed() {
        this.viewed = true;
    }

    public void markAsNotViewed() {
        this.viewed = false;
    }

    @Override
    public int hashCode() {
        return  this.getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Episode) &&
               ((Episode) obj).getId().equals(this.getId());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
