//<?xml version="1.0" encoding="UTF-8" ?>
// <Episode>
//    <id>332179</id>
//    <DVD_chapter></DVD_chapter>
//    <DVD_discid></DVD_discid>
//    <DVD_episodenumber></DVD_episodenumber>
//    <DVD_season></DVD_season>
//    <Director>|Joseph McGinty Nichol|</Director>
//    <EpisodeName>Chuck Versus the World</EpisodeName>
//    <EpisodeNumber>1</EpisodeNumber>
//    <FirstAired>2007-09-24</FirstAired>
//    <GuestStars>|Julia Ling|Vik Sahay|Mieko Hillman|</GuestStars>
//    <IMDB_ID></IMDB_ID>
//    <Language>English</Language>
//    <Overview>Chuck Bartowski is an average computer geek...</Overview>
//    <ProductionCode></ProductionCode>
//    <Rating>9.0</Rating>
//    <SeasonNumber>1</SeasonNumber>
//    <Writer>|Josh Schwartz|Chris Fedak|</Writer>
//    <absolute_number></absolute_number>
//    <airsafter_season></airsafter_season>
//    <airsbefore_episode></airsbefore_episode>
//    <airsbefore_season></airsbefore_season>
//    <filename>episodes/80348-332179.jpg</filename>
//    <lastupdated>1201292806</lastupdated>
//    <seasonid>27985</seasonid>
//    <seriesid>80348</seriesid>
//</Episode>

package br.edu.ufcg.aweseries.thetvdb.episode;

import br.edu.ufcg.aweseries.util.Strings;

public class Episode {
    private String id;
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
    private String seriesId;

    public Episode() {}

    public Episode(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Episode(String id, String seriesId, int number, int seasonNumber) {
        if (id == null || Strings.isBlank(id)) {
            throw new IllegalArgumentException("invalid id for episode");
        }

        if (seriesId == null || Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("invalid series id for episode");
        }

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
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

    public void setId(String id) {
        this.id = id;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
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

    public Episode copy() {
        Episode episode = new Episode();
        episode.id = this.id;
        episode.seasonNumber = this.seasonNumber;
        episode.viewed = this.viewed;
        return episode;
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
