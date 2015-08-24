package mobi.myseries.domain.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Validate;

public class Series {
    public static final int INVALID_SERIES_ID = -1;

    public static class Builder {
        private int id;
        private String title;
        private Status status;
        private Date airDate;
        private Date airTime;
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
            id = Invalid.SERIES_ID;
            episodes = new HashSet<Episode>();
        }

        public Series build() {
            final Series series = new Series(id, title);

            series.status = status;
            series.airTime = airTime;
            series.airDate = airDate;
            series.runtime = runtime;
            series.network = network;
            series.overview = overview;
            series.genres = genres;
            series.actors = actors;
            series.posterFileName = posterUrl;
            series.bannerFileName = bannerFileName;

            if (lastUpdate == null) {
                series.lastUpdate = System.currentTimeMillis();
            } else {
                series.lastUpdate = lastUpdate;
            }

            return series.includingAll(episodes);
        }

        public Builder withActors(String actors) {
            this.actors = actors;
            return this;
        }

        public Builder withAirDate(Date airDate) {
            this.airDate = airDate;
            return this;
        }

        public Builder withAirTime(Date date) {
            airTime = date;
            return this;
        }

        public Builder withEpisode(Episode episode) {
            episodes.add(episode);
            return this;
        }

        public Builder withGenres(String genres) {
            this.genres = genres;
            return this;
        }

        public Builder withTraktId(int id) {
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
    private Date airTime;
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
        title = name;

        seasons = new SeasonSet(this.id);
    }

    public String actors() {
        return actors;
    }

    public Date airDate() {
        return airDate;
    }

    public Date airtime() {
        return airTime;
    }

    public List<Episode> episodes() {
        return seasons.episodes();
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        return seasons.episodesBy(specification);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Series) && (((Series) obj).id == id);
    }

    public String genres() {
        return genres;
    }

    @Override
    public int hashCode() {
        return id;
    }

    public boolean hasPoster() {
        return posterFileName != null;
    }

    public boolean hasSpecialEpisodes() {
        return seasons.hasSpecialEpisodes();
    }

    public int id() {
        return id;
    }

    @Deprecated
    public Series includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "items");

        for (Episode e : episodes) {
            seasons.include(e.withAirtime(airTime));
        }

        return this;
    }

    public Long lastUpdate() {
        return lastUpdate;
    }

    public void markAsUnwatched() {
        seasons.markAsUnwatched();
    }

    public void markAsWatched() {
        seasons.markAsWatched();
    }

    public synchronized void mergeWith(Series other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.id == id, "other should have the same id as this");

        title = other.title;
        status = other.status;
        airDate = other.airDate;
        airTime = other.airTime;
        runtime = other.runtime;
        network = other.network;
        overview = other.overview;
        genres = other.genres;
        actors = other.actors;
        posterFileName = other.posterFileName;

        seasons.mergeWith(other.seasons);
    }

    public String name() {
        return title;
    }

    public String network() {
        return network;
    }

    public Episode nextEpisodeToWatch(boolean includingSpecialEpisodes) {
        return seasons.nextEpisodeToWatch(includingSpecialEpisodes);
    }

    public int numberOfEpisodes() {
        return seasons.numberOfEpisodes();
    }

    public int numberOfEpisodes(Specification<Episode> specification) {
        return episodesBy(specification).size();
    }

    public String overview() {
        return overview;
    }

    public String posterUrl() {
        return posterFileName;
    }

    public String bannerFileName() {
        return bannerFileName;
    }

    public String runtime() {
        return runtime;
    }

    public Season season(int number) {
        return seasons.season(number);
    }

    public Season seasonAt(int position) {
        return seasons.seasonAt(position);
    }

    public SeasonSet seasons() {
        return seasons;
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
        return status;
    }
}
