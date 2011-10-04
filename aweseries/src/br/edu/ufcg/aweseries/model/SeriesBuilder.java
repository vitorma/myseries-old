package br.edu.ufcg.aweseries.model;


public class SeriesBuilder {
    private static final String DEFAULT_STRING = "";

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

    public SeriesBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public SeriesBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SeriesBuilder withStatus(String status) {
        this.status = status;
        return this;
    }

    public SeriesBuilder withAirsDay(String airsDay) {
        this.airsDay = airsDay;
        return this;
    }

    public SeriesBuilder withAirsTime(String airsTime) {
        this.airsTime = airsTime;
        return this;
    }

    public SeriesBuilder withFirstAired(String firstAired) {
        this.firstAired = firstAired;
        return this;
    }

    public SeriesBuilder withRuntime(String runtime) {
        this.runtime = runtime;
        return this;
    }

    public SeriesBuilder withNetwork(String network) {
        this.network = network;
        return this;
    }

    public SeriesBuilder withOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public SeriesBuilder withGenres(String genres) {
        this.genres = genres;
        return this;
    }

    public SeriesBuilder withActors(String actors) {
        this.actors = actors;
        return this;
    }

    public SeriesBuilder withPoster(String poster) {
        this.poster = poster;
        return this;
    }

    public SeriesBuilder withSeasons(Seasons seasons) {
        this.seasons = seasons;
        return this;
    }

    public Series build() {
        final Series series = new Series(this.id, this.name);

        series.setStatus(this.status != null ? this.status : DEFAULT_STRING);
        series.setAirsDay(this.airsDay != null ? this.airsDay : DEFAULT_STRING);
        series.setAirsTime(this.airsTime != null ? this.airsTime : DEFAULT_STRING);
        series.setFirstAired(this.firstAired != null ? this.firstAired : DEFAULT_STRING);
        series.setRuntime(this.runtime != null ? this.runtime : DEFAULT_STRING);
        series.setNetwork(this.network != null ? this.network : DEFAULT_STRING);
        series.setOverview(this.overview != null ? this.overview : DEFAULT_STRING);
        series.setGenres(this.genres != null ? this.genres : DEFAULT_STRING);
        series.setActors(this.actors != null ? this.actors : DEFAULT_STRING);
        series.setPoster(this.poster != null ? this.poster : DEFAULT_STRING);
        series.setSeasons(this.seasons != null ? this.seasons : new Seasons());

        return series;
    }
}
