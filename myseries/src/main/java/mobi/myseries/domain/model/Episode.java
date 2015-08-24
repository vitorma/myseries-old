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
    private long mId;
    private final int mSeriesId;
    private final int mNumber;
    private final int mSeasonNumber;
    private String mTitle;
    private Date mAirDate;
    private Date mAirTime;
    private String mOverview;
    private String mDirectors;
    private String mWriters;
    private String mGuestStars;
    private String mScreenUrl;

    private boolean mWatchMark;

    private Episode(long id, int seriesId, int number, int seasonNumber) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");
        Validate.isTrue(seasonNumber >= 0, "seasonNumber should be non-negative");

        mId = id;
        mSeriesId = seriesId;
        mNumber = number;
        mSeasonNumber = seasonNumber;
    }

    public static Episode.Builder builder() {
        return new Episode.Builder();
    }

    public long id() {
        return mId;
    }

    public int seriesId() {
        return mSeriesId;
    }

    public int number() {
        return mNumber;
    }

    public int seasonNumber() {
        return mSeasonNumber;
    }

    public boolean isSpecial() {
        return mSeasonNumber == Season.SPECIAL_SEASON_NUMBER;
    }

    public boolean isNotSpecial() {
        return !isSpecial();
    }

    public String title() {
        return mTitle;
    }

    public Date airDate() {
        return mAirDate;
    }

    public Date airTime() {
        return mAirTime;
    }

    @Deprecated
    public Episode withAirtime(Date airTime2) {
        mAirTime = airTime2;
        return this;
    }

    public String overview() {
        return mOverview;
    }

    public String directors() {
        return mDirectors;
    }

    public String writers() {
        return mWriters;
    }

    public String guestStars() {
        return mGuestStars;
    }

    public String screenUrl() {
        return mScreenUrl;
    }

    public boolean watched() {
        return mWatchMark;
    }

    public boolean unwatched() {
        return !mWatchMark;
    }

    public void markAsWatched() {
        mWatchMark = true;
    }

    public void markAsUnwatched() {
        mWatchMark = false;
    }

    public synchronized void mergeWith(Episode other) {
        // TODO(Gabriel): Replace all these verifications with a single this.isTheSameAs(other)?
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.mSeriesId == mSeriesId, "other should have the same seriesId as this");
        Validate.isTrue(other.mSeasonNumber == mSeasonNumber, "other should have the same seasonNumber as this");
        Validate.isTrue(other.mNumber == mNumber, "other should have the same number as this");

        mId = other.mId;
        mTitle = other.mTitle;
        mAirDate = other.mAirDate;
        mAirTime = other.mAirTime;
        mOverview = other.mOverview;
        mDirectors = other.mDirectors;
        mWriters = other.mWriters;
        mGuestStars = other.mGuestStars;
        mScreenUrl = other.mScreenUrl;
    }

    public boolean isTheSameAs(Episode that) {
        return that != null
                && mNumber == that.mNumber
                && mSeasonNumber == that.mSeasonNumber
                && mSeriesId == that.mSeriesId;
    }

    @Override
    public int hashCode() {
        return mSeriesId * 3331 + mSeasonNumber * 443 + mNumber * 47;
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj == null) || (obj.getClass() != Episode.class)) {
            return false;
        }

        Episode that = (Episode) obj;

        return isTheSameAs(that);
    }

    public static class Builder {
        private long mId;
        private int mSeriesId;
        private int mNumber;
        private int mSeasonNumber;
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
            mId = Invalid.EPISODE_ID;
            mSeriesId = Invalid.SERIES_ID;
            mNumber = Invalid.EPISODE_NUMBER;
            mSeasonNumber = Invalid.SEASON_NUMBER;
        }

        public Builder withId(long id) {
            mId = id;
            return this;
        }

        public Builder withSeriesId(int seriesId) {
            mSeriesId = seriesId;
            return this;
        }

        public Builder withNumber(int number) {
            mNumber = number;
            return this;
        }

        public Builder withSeasonNumber(int seasonNumber) {
            mSeasonNumber = seasonNumber;
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
            Episode episode = new Episode(mId, mSeriesId, mNumber, mSeasonNumber);

            episode.mTitle = title;
            episode.mAirDate = airDate;
            episode.mAirTime = airTime;
            episode.mOverview = overview;
            episode.mDirectors = directors;
            episode.mWriters = writers;
            episode.mGuestStars = guestStars;
            episode.mScreenUrl = screenUrl;
            episode.mWatchMark = watchMark;

            return episode;
        }
    }
}
