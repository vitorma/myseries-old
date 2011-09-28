package br.edu.ufcg.aweseries.thetvdb.series;

import br.edu.ufcg.aweseries.thetvdb.season.Seasons;
import br.edu.ufcg.aweseries.util.Strings;

public final class SeriesBuilder {
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
        this.genres = Strings.normalizePipeSeparated(genres);
        return this;
    }

    public SeriesBuilder withActors(String actors) {
        this.actors = Strings.normalizePipeSeparated(actors);
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
        series.setStatus(this.status);
        series.setAirsDay(airsDay);
        series.setAirsTime(airsTime);
        series.setFirstAired(firstAired);
        series.setRuntime(runtime);
        series.setNetwork(network);
        series.setOverview(overview);
        series.setGenres(genres);
        series.setActors(actors);
        series.setPoster(poster);
        series.setSeasons(seasons);
        return series;
    }
}
