package br.edu.ufcg.aweseries.model;


public class EpisodeBuilder {
    private static final String DEFAULT_STRING = "";
    private static final String DEFAULT_NAME = "Unnamed Episode";

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

    public EpisodeBuilder() {
        this.number = -1;
        this.seasonNumber = -1;
    }

    public EpisodeBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public EpisodeBuilder withSeriesId(String seriesId) {
        this.seriesId = seriesId;
        return this;
    }

    public EpisodeBuilder withNumber(int number) {
        this.number = number;
        return this;
    }

    public EpisodeBuilder withNumber(String number) {
        try {
            this.number = Integer.valueOf(number);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("number should be an integer");
        }
        return this;
    }

    public EpisodeBuilder withSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
        return this;
    }

    public EpisodeBuilder withSeasonNumber(String seasonNumber) {
        try {
            this.seasonNumber = Integer.valueOf(seasonNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("season number should be an integer");
        }
        return this;
    }

    public EpisodeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EpisodeBuilder withFirstAired(String firstAired) {
        this.firstAired = firstAired;
        return this;
    }

    public EpisodeBuilder withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public EpisodeBuilder withDirector(String director) {
        this.director = director;
        return this;
    }

    public EpisodeBuilder withWriter(String writer) {
        this.writer = writer;
        return this;
    }

    public EpisodeBuilder withGuestStars(String guestStars) {
        this.guestStars = guestStars;
        return this;
    }

    public EpisodeBuilder withPoster(String poster) {
        this.poster = poster;
        return this;
    }

    public EpisodeBuilder withViewed(boolean viewed) {
        this.viewed = viewed;
        return this;
    }

    public Episode build() {
        Episode episode = new Episode(this.id, this.seriesId, this.number, this.seasonNumber);

        episode.setName(this.name != null ? this.name : DEFAULT_NAME);
        episode.setFirstAired(this.firstAired != null ? this.firstAired : DEFAULT_STRING);
        episode.setOverview(this.overview != null ? this.overview : DEFAULT_STRING);
        episode.setDirector(this.director != null ? this.director : DEFAULT_STRING);
        episode.setWriter(this.writer != null ? this.writer : DEFAULT_STRING);
        episode.setGuestStars(this.guestStars != null ? this.guestStars : DEFAULT_STRING);
        episode.setPoster(this.poster != null ? this.poster : DEFAULT_STRING);
        episode.setViewed(this.viewed);

        return episode;
    }
}
