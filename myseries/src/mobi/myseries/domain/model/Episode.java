package mobi.myseries.domain.model;

import java.util.Date;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.Validate;

/*
 * (Cleber)
 * Trakt.tv does not provide information about people (directors, writers, guest stars) of episodes,
 * but it provides attributes (e.g. tvdb_id and tvrage_id) which allow us getting such information (and other ones) from other sources.
 * We can do it in future.
 */

public class Episode {
    private int id;
    private int seriesId;
    private int number;
    private int seasonNumber;
    private String title;
    private Date airDate;
    private Time airTime;
    private String overview;
    private String directors;
    private String writers;
    private String guestStars;
    private String screenUrl;

    private boolean watchMark;

    private Episode(int id, int seriesId, int number, int seasonNumber) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");
        Validate.isTrue(seasonNumber >= 0, "seasonNumber should be non-negative");

        this.id = id;
        this.seriesId = seriesId;
        this.number = number;
        this.seasonNumber = seasonNumber;
    }

    public static Episode.Builder builder() {
        return new Episode.Builder();
    }

    public int id() {
        return this.id;
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public int seasonNumber() {
        return this.seasonNumber;
    }

    public boolean isSpecial() {
        return this.seasonNumber == Season.SPECIAL_SEASON_NUMBER;
    }

    public boolean isNotSpecial() {
        return !this.isSpecial();
    }

    public String title() {
        return this.title;
    }

    public Date airDate() {
        return this.airDate;
    }

    public Time airTime() {
        return this.airTime;
    }

    @Deprecated
    public Episode withAirtime(Time airtime) {
        this.airTime = airtime;
        return this;
    }

    public String overview() {
        return this.overview;
    }

    public String directors() {
        return this.directors;
    }

    public String writers() {
        return this.writers;
    }

    public String guestStars() {
        return this.guestStars;
    }

    public String screenUrl() {
        return this.screenUrl;
    }

    public boolean watched() {
        return this.watchMark;
    }

    public boolean unwatched() {
        return !this.watchMark;
    }

    public void markAsWatched() {
        this.watchMark = true;
    }

    public void markAsUnwatched() {
        this.watchMark = false;
    }

    public synchronized void mergeWith(Episode other) {
        // TODO(Gabriel): Replace all these verifications with a single this.isTheSameAs(other)?
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.seriesId == this.seriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.seasonNumber == this.seasonNumber, "other should have the same seasonNumber as this");
        Validate.isTrue(other.number == this.number, "other should have the same number as this");

        this.id = other.id;
        this.title = other.title;
        this.airDate = other.airDate;
        this.airTime = other.airTime;
        this.overview = other.overview;
        this.directors = other.directors;
        this.writers = other.writers;
        this.guestStars = other.guestStars;
        this.screenUrl = other.screenUrl;
    }

    public boolean isTheSameAs(Episode that) {
        return that != null
                && this.number == that.number
                && this.seasonNumber == that.seasonNumber
                && this.seriesId == that.seriesId;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != Episode.class)) {
            return false;
        }

        Episode that = (Episode) obj;

        return this.id == that.id;
    }

    public static class Builder {
        private int id;
        private int seriesId;
        private int number;
        private int seasonNumber;
        private String title = "";
        private Date airDate;
        private Time airTime;
        private String overview = "";
        private String directors = "";
        private String writers = "";
        private String guestStars = "";
        private String screenUrl = "";
        private boolean watchMark;

        private Builder() {
            this.id = Invalid.EPISODE_ID;
            this.seriesId = Invalid.SERIES_ID;
            this.number = Invalid.EPISODE_NUMBER;
            this.seasonNumber = Invalid.SEASON_NUMBER;
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withSeriesId(int seriesId) {
            this.seriesId = seriesId;
            return this;
        }

        public Builder withNumber(int number) {
            this.number = number;
            return this;
        }

        public Builder withSeasonNumber(int seasonNumber) {
            this.seasonNumber = seasonNumber;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withAirDate(Date airDate) {
            this.airDate = airDate;
            return this;
        }

        public Builder withAirtime(Time airTime) {
            this.airTime = airTime;
            return this;
        }

        public Builder withOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder withDirectors(String directors) {
            this.directors = directors;
            return this;
        }

        public Builder withWriters(String writers) {
            this.writers = writers;
            return this;
        }

        public Builder withGuestStars(String guestStars) {
            this.guestStars = guestStars;
            return this;
        }

        public Builder withScreenUrl(String screenUrl) {
            this.screenUrl = screenUrl;
            return this;
        }

        public Builder withWatchMark(boolean watchMark) {
            this.watchMark = watchMark;
            return this;
        }

        public Episode build() {
            Episode episode = new Episode(this.id, this.seriesId, this.number, this.seasonNumber);

            episode.title = this.title;
            episode.airDate = this.airDate;
            episode.airTime = this.airTime;
            episode.overview = this.overview;
            episode.directors = this.directors;
            episode.writers = this.writers;
            episode.guestStars = this.guestStars;
            episode.screenUrl = this.screenUrl;
            episode.watchMark = this.watchMark;

            return episode;
        }
    }
}
