package br.edu.ufcg.aweseries.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.edu.ufcg.aweseries.util.Strings;

public class Episode {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

    private boolean seen;

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
        this.setDirector("");
        this.setFirstAired("");
        this.setGuestStars("");
        this.setName("");
        this.setOverview("");
        this.setPoster("");
        this.setWriter("");
        this.markWetherSeen(false);
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

    public boolean isSpecial() {
        return this.getSeasonNumber() == 0;
    }

    public String getName() {
        return this.name;
    }

    public String getFirstAired() {
        return this.firstAired;
    }

    public Date getDateFirstAired() {
        if (Strings.isBlank(this.getFirstAired())) {
            return new Date(Long.MAX_VALUE);
        }

        try {
            return dateFormat.parse(this.getFirstAired());
        } catch (ParseException e) {
            return new Date(Long.MAX_VALUE);
        }
    }

    public boolean airedBefore(Date d) {
        return this.getDateFirstAired().compareTo(d) < 0;
    }

    public boolean airedUntil(Date d) {
        return this.getDateFirstAired().compareTo(d) <= 0;
    }

    public boolean airedAt(Date d) {
        return this.getDateFirstAired().compareTo(d) == 0;
    }

    public boolean airedFrom(Date d) {
        return this.getDateFirstAired().compareTo(d) >= 0;
    }

    public boolean airedAfter(Date d) {
        return this.getDateFirstAired().compareTo(d) > 0;
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

    public boolean wasSeen() {
        return this.seen;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name should not be null");
        }
        
        this.name = name;
    }

    public void setFirstAired(String firstAired) {
        if (firstAired == null) {
            throw new IllegalArgumentException("First aired should not be null");
        }

        this.firstAired = firstAired;
    }

    public void setOverview(String overview) {
        if (overview == null) {
            throw new IllegalArgumentException("Overview should not be null");
        }

        this.overview = overview;
    }

    public void setDirector(String director) {
        if (director == null) {
            throw new IllegalArgumentException("Director should not be null");
        }

        this.director = director;
    }

    public void setWriter(String writer) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer should not be null");
        }

        this.writer = writer;
    }

    public void setGuestStars(String guestStars) {
        if (guestStars == null) {
            throw new IllegalArgumentException("Guest stars should not be null");
        }

        this.guestStars = guestStars;
    }

    public void setPoster(String poster) {
        if (poster == null) {
            throw new IllegalArgumentException("Poster should not be null");
        }

        this.poster = poster;
    }

    public void markWetherSeen(boolean seen) {
        this.seen = seen;
    }

    public void markAsSeen() {
        this.seen = true;
    }

    public void markAsNotSeen() {
        this.seen = false;
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
