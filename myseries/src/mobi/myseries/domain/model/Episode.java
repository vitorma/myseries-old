package mobi.myseries.domain.model;

import java.util.Date;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Validate;

/*
 * (Cleber)
 * Trakt.tv does not provide information about people (directors, writers, guest stars) of episodes,
 * but it provides attributes (e.g. tvdb_id and tvrage_id) which allow us getting such information (and other ones) from other sources.
 * We can do it in future.
 */

public class Episode {
    private int id;
    private final int seriesId;
    private final int number;
    private final int seasonNumber;
    private String title;
    private Date airDate;
    private Date airTime;
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
        return id;
    }

    public int seriesId() {
        return seriesId;
    }

    public int number() {
        return number;
    }

    public int seasonNumber() {
        return seasonNumber;
    }

    public boolean isSpecial() {
        return seasonNumber == Season.SPECIAL_SEASON_NUMBER;
    }

    public boolean isNotSpecial() {
        return !isSpecial();
    }

    public String title() {
        return title;
    }

    public Date airDate() {
        return airDate;
    }

    public Date airTime() {
        return airTime;
    }

    @Deprecated
    public Episode withAirtime(Date airTime2) {
        airTime = airTime2;
        return this;
    }

    public String overview() {
        return overview;
    }

    public String directors() {
        return directors;
    }

    public String writers() {
        return writers;
    }

    public String guestStars() {
        return guestStars;
    }

    public String screenUrl() {
        return screenUrl;
    }

    public boolean watched() {
        return watchMark;
    }

    public boolean unwatched() {
        return !watchMark;
    }

    public void markAsWatched() {
        watchMark = true;
    }

    public void markAsUnwatched() {
        watchMark = false;
    }

    public synchronized void mergeWith(Episode other) {
        // TODO(Gabriel): Replace all these verifications with a single this.isTheSameAs(other)?
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.seriesId == seriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.seasonNumber == seasonNumber, "other should have the same seasonNumber as this");
        Validate.isTrue(other.number == number, "other should have the same number as this");

        id = other.id;
        title = other.title;
        airDate = other.airDate;
        airTime = other.airTime;
        overview = other.overview;
        directors = other.directors;
        writers = other.writers;
        guestStars = other.guestStars;
        screenUrl = other.screenUrl;
    }

    public boolean isTheSameAs(Episode that) {
        return that != null
                && number == that.number
                && seasonNumber == that.seasonNumber
                && seriesId == that.seriesId;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != Episode.class)) {
            return false;
        }

        Episode that = (Episode) obj;

        return id == that.id;
    }

    public static class Builder {
        private int id;
        private int seriesId;
        private int number;
        private int seasonNumber;
        private String title = "";
        private Date airDate;
        private Date airTime;
        private String overview = "";
        private String directors = "";
        private String writers = "";
        private String guestStars = "";
        private String screenUrl = "";
        private boolean watchMark;

        private Builder() {
            id = Invalid.EPISODE_ID;
            seriesId = Invalid.SERIES_ID;
            number = Invalid.EPISODE_NUMBER;
            seasonNumber = Invalid.SEASON_NUMBER;
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

        public Builder withAirtime(Date airTime) {
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
            Episode episode = new Episode(id, seriesId, number, seasonNumber);

            episode.title = title;
            episode.airDate = airDate;
            episode.airTime = airTime;
            episode.overview = overview;
            episode.directors = directors;
            episode.writers = writers;
            episode.guestStars = guestStars;
            episode.screenUrl = screenUrl;
            episode.watchMark = watchMark;

            return episode;
        }
    }
}
