/*
 *   Series.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.domain.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.Validate;
import mobi.myseries.shared.WeekDay;

public class Series implements SeasonSetListener, Publisher<SeriesListener> {

    public static class Builder {
        private int id;
        private String name;
        private Status status;
        private Date airDate;
        private WeekDay airDay;
        private Time airtime;
        private String runtime;
        private String network;
        private String overview;
        private String genres;
        private String actors;
        private String posterFileName;
        private String bannerFileName;

        private final Set<Episode> episodes;
        private Long lastUpdate;

        private Builder() {
            this.id = Invalid.SERIES_ID;
            this.episodes = new HashSet<Episode>();
        }

        public Series build() {
            final Series series = new Series(this.id, this.name);

            series.status = this.status;
            series.airDay = this.airDay;
            series.airtime = this.airtime;
            series.airDate = this.airDate;
            series.runtime = this.runtime;
            series.network = this.network;
            series.overview = this.overview;
            series.genres = this.genres;
            series.actors = this.actors;
            series.posterFileName = this.posterFileName;
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

        public Builder withAirtime(Time airtime) {
            this.airtime = airtime;
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

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
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

        public Builder withPosterFileName(String posterFileName) {
            this.posterFileName = posterFileName;
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
    private String name;
    private Status status;
    private Date airDate;
    private WeekDay airDay;
    private Time airtime;
    private String runtime;
    private String network;
    private String overview;
    private String genres;
    private String actors;
    private String posterFileName;
    private String bannerFileName;
    private final SeasonSet seasons;

    private final ListenerSet<SeriesListener> listeners;

    private Long lastUpdate;

    private Series(int id, String name) {
        Validate.isTrue(id >= 0, "id should be non-negative");
        Validate.isNonBlank(name, "name");

        this.id = id;
        this.name = name;

        this.seasons = new SeasonSet(this.id);
        this.seasons.register(this);
        this.listeners = new ListenerSet<SeriesListener>();
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
        return this.airtime;
    }

    @Override
    public boolean deregister(SeriesListener listener) {
        return this.listeners.deregister(listener);
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

    public Series includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "items");

        for (Episode e : episodes) {
            this.seasons.including(e.withAirtime(this.airtime));
        }

        return this;
    }

    public Long lastUpdate() {
        return this.lastUpdate;
    }

    public void markAsNotSeen() {
        for (Season s : this.seasons.seasons()) {
            s.setBeingMarkedBySeries(true);
            s.markAsNotSeen();
            s.setBeingMarkedBySeries(false);
        }

        this.notifyThatWasMarkedAsNotSeen();
    }

    public void markAsSeen() {
        for (Season s : this.seasons.seasons()) {
            s.setBeingMarkedBySeries(true);
            s.markAsSeen();
            s.setBeingMarkedBySeries(false);
        }

        this.notifyThatWasMarkedAsSeen();
    }

    public synchronized void mergeWith(Series other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(other.id == this.id, "other should have the same id as this");

        this.name = other.name;
        this.status = other.status;
        this.airDate = other.airDate;
        this.airDay = other.airDay;
        this.airtime = other.airtime;
        this.runtime = other.runtime;
        this.network = other.network;
        this.overview = other.overview;
        this.genres = other.genres;
        this.actors = other.actors;
        this.posterFileName = other.posterFileName;

        this.seasons.mergeWith(other.seasons);
    }

    public String name() {
        return this.name;
    }

    public String network() {
        return this.network;
    }

    public Episode nextEpisodeToSee(boolean includingSpecialEpisodes) {
        return this.seasons.nextEpisodeToSee(includingSpecialEpisodes);
    }

    private void notifyThatNextEpisodeToSeeChanged() {
        for (SeriesListener l : this.listeners) {
            l.onChangeNextEpisodeToSee(this);
        }
    }

    private void notifyThatNextNonSpecialEpisodeToSeeChanged() {
        for (SeriesListener l : this.listeners) {
            l.onChangeNextNonSpecialEpisodeToSee(this);
        }
    }

    private void notifyThatNumberOfSeenEpisodesChanged() {
        for (SeriesListener l : this.listeners) {
            l.onChangeNumberOfSeenEpisodes(this);
        }
    }

    private void notifyThatWasMarkedAsNotSeen() {
        for (SeriesListener l : this.listeners) {
            l.onMarkAsNotSeen(this);
        }
    }

    private void notifyThatWasMarkedAsSeen() {
        for (SeriesListener l : this.listeners) {
            l.onMarkAsSeen(this);
        }
    }

    public int numberOfEpisodes() {
        return this.seasons.numberOfEpisodes();
    }

    public int numberOfEpisodes(Specification<Episode> specification) {
        return this.episodesBy(specification).size();
    }

    public int numberOfSeenEpisodes() {
        return this.seasons.numberOfSeenEpisodes();
    }

    public int numberOfUnwatchedEpisodes() {
        return this.numberOfEpisodes() - this.numberOfSeenEpisodes();
    }

    @Override
    public void onChangeNextEpisodeToSee(SeasonSet seasonSet) {
        this.notifyThatNextEpisodeToSeeChanged();
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(SeasonSet seasonSet) {
        this.notifyThatNextNonSpecialEpisodeToSeeChanged();
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(SeasonSet seasonSet) {
        this.notifyThatNumberOfSeenEpisodesChanged();
    }

    public String overview() {
        return this.overview;
    }

    public String posterFileName() {
        return this.posterFileName;
    }

    public String bannerFileName() {
        return this.bannerFileName;
    }

    @Override
    public boolean register(SeriesListener listener) {
        return this.listeners.register(listener);
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
