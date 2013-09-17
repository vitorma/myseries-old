package mobi.myseries.domain.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.Validate;
import mobi.myseries.shared.WeekDay;

public class Series {
    public static final int INVALID_SERIES_ID = -1;

    public static class Builder {
        private int id;
        private String title;
        private Status status;
        private Date airDate;
        private WeekDay airDay;
        private Time airTime;
        private String runtime = "";
        private String network = "";
        private String overview = "";
        private String genres = "";
        private String actors = "";
        private String posterUrl = "";
        private String bannerFileName = "";

        private final Set<Episode> episodes;
        private Long lastUpdate;

        private Builder() {
            this.id = Invalid.SERIES_ID;
            this.episodes = new HashSet<Episode>();
        }

        public Series build() {
            final Series series = new Series(this.id, this.title);

            series.status = this.status;
            series.airDay = this.airDay;
            series.airTime = this.airTime;
            series.airDate = this.airDate;
            series.runtime = this.runtime;
            series.network = this.network;
            series.overview = this.overview;
            series.genres = this.genres;
            series.actors = this.actors;
            series.posterFileName = this.posterUrl;
            series.bannerFileName = this.bannerFileName;

            if (this.lastUpdate == null) {
                series.lastUpdate = System.currentTimeMillis();
            } else {
                series.lastUpdate = this.lastUpdate;
            }

            return series.includingAll(this.episodes);
        }

        public Builder withActors(String actors) {
            this.actors = actors;
            return this;
        }

        public Builder withAirDate(Date airDate) {
            this.airDate = airDate;
            return this;
        }

        public Builder withAirDay(WeekDay airDay) {
            this.airDay = airDay;
            return this;
        }

        public Builder withAirTime(Time airTime) {
            this.airTime = airTime;
            return this;
        }

        public Builder withEpisode(Episode episode) {
            this.episodes.add(episode);
            return this;
        }

        public Builder withGenres(String genres) {
            this.genres = genres;
            return this;
        }

        public Builder withTvdbId(int id) {
            this.id = id;
            return this;
        }

        public Builder withLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withNetwork(String network) {
            this.network = network;
            return this;
        }

        public Builder withOverview(String overview) {
            this.overview = overview;
            return this;
        }

        public Builder withPoster(String posterUrl) {
            this.posterUrl = posterUrl;
            return this;
        }

        public Builder withBannerFileName(String bannerFileName) {
            this.bannerFileName = bannerFileName;
            return this;
        }

        public Builder withRuntime(String runtime) {
            this.runtime = runtime;
            return this;
        }

        public Builder withStatus(Status status) {
            this.status = status;
            return this;
        }
    }

    public static Series.Builder builder() {
        return new Series.Builder();
    }

    private final int id;
    private String title;
    private Status status;
    private Date airDate;
    private WeekDay airDay;
    private Time airTime;
    private String runtime;
    private String network;
    private String overview;
    private String genres;
    private String actors;
    private String posterFileName;
    private String bannerFileName;
    private final SeasonSet seasons;

    private Long lastUpdate;

    private Series(int id, String name) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isNonBlank(name, "name");

        this.id = id;
        this.title = name;

        this.seasons = new SeasonSet(this.id);
    }

    public String actors() {
        return this.actors;
    }

    public Date airDate() {
        return this.airDate;
    }

    public WeekDay airDay() {
        return this.airDay;
    }

    public Time airtime() {
        return this.airTime;
    }

    public List<Episode> episodes() {
        return this.seasons.episodes();
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        return this.seasons.episodesBy(specification);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Series) && (((Series) obj).id == this.id);
    }

    public String genres() {
        return this.genres;
    }

    @Override
    public int hashCode() {
        return this.id;
    }

    public boolean hasPoster() {
        return this.posterFileName != null;
    }

    public boolean hasSpecialEpisodes() {
        return this.seasons.hasSpecialEpisodes();
    }

    public int id() {
        return this.id;
    }

    @Deprecated
    public Series includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "items");

        for (Episode e : episodes) {
            this.seasons.include(e.withAirtime(this.airTime));
        }

        return this;
    }

    public Long lastUpdate() {
        return this.lastUpdate;
    }

    public void markAsUnwatched() {
        this.seasons.markAsUnwatched();
    }

    public void markAsWatched() {
        this.seasons.markAsWatched();
    }

    public synchronized void mergeWith(Series other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.id == this.id, "other should have the same id as this");

        this.title = other.title;
        this.status = other.status;
        this.airDate = other.airDate;
        this.airDay = other.airDay;
        this.airTime = other.airTime;
        this.runtime = other.runtime;
        this.network = other.network;
        this.overview = other.overview;
        this.genres = other.genres;
        this.actors = other.actors;
        this.posterFileName = other.posterFileName;

        this.seasons.mergeWith(other.seasons);
    }

    public String name() {
        return this.title;
    }

    public String network() {
        return this.network;
    }

    public Episode nextEpisodeToWatch(boolean includingSpecialEpisodes) {
        return this.seasons.nextEpisodeToWatch(includingSpecialEpisodes);
    }

    public int numberOfEpisodes() {
        return this.seasons.numberOfEpisodes();
    }

    public int numberOfEpisodes(Specification<Episode> specification) {
        return this.episodesBy(specification).size();
    }

    public String overview() {
        return this.overview;
    }

    public String posterUrl() {
        return this.posterFileName;
    }

    public String bannerFileName() {
        return this.bannerFileName;
    }

    public String runtime() {
        return this.runtime;
    }

    public Season season(int number) {
        return this.seasons.season(number);
    }

    public Season seasonAt(int position) {
        return this.seasons.seasonAt(position);
    }

    public SeasonSet seasons() {
        return this.seasons;
    }

    public Series setLastUpdate(Long lastUpdate) {
        this.lastUpdate = lastUpdate;
        return this;
    }

    public Series setPosterFilename(String posterFileName) {
        this.posterFileName = posterFileName;

        return this;
    }

    public Series setBannerFilename(String bannerFileName) {
        this.bannerFileName = bannerFileName;

        return this;
    }

    public Status status() {
        return this.status;
    }
}
