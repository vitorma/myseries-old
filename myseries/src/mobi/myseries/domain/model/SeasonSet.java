/*
 *   SeasonSet.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class SeasonSet implements SeasonListener, Publisher<SeasonSetListener> {
    private static final int SPECIAL_EPISODES_SEASON_NUMBER = 0;
    private final int seriesId;

    private final TreeMap<Integer, Season> seasons;
    private final ListenerSet<SeasonSetListener> listeners;

    public SeasonSet(int seriesId) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");

        this.seriesId = seriesId;

        this.seasons = new TreeMap<Integer, Season>();
        this.listeners = new ListenerSet<SeasonSetListener>();
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int numberOfSeasons() {
        return this.seasons.size();
    }

    public boolean includes(Season season) {
        return (season != null) && this.seasons.containsKey(season.number());
    }

    public Season season(int number) {
        return this.seasons.get(number);
    }

    public Season seasonAt(int position) {
        int i = 0;

        for (Integer seasonNumber : this.seasons.keySet()) {
            if (i == position) {
                return this.season(seasonNumber);
            }

            i++;
        }

        throw new IndexOutOfBoundsException(
            "invalid position, " + position + ", should be in the range [0, "
                + this.numberOfSeasons() + ")");
    }

    private Season ensuredSeason(int number) {
        if (!this.seasons.containsKey(number)) {
            Season season = new Season(this.seriesId, number);
            this.including(season);
        }

        return this.season(number);
    }

    public List<Season> seasons() {
        return new ArrayList<Season>(this.seasons.values());
    }

    public boolean hasSpecialEpisodes() {
        return this.seasons.containsKey(0);
    }

    private SeasonSet including(Season season) {
        season.register(this);

        this.seasons.put(season.number(), season);

        return this;
    }

    private SeasonSet excluding(Season season) {
        season.deregister(this);

        this.seasons.remove(season.number());

        return this;
    }

    public int numberOfEpisodes() {
        int numberOfEpisodes = 0;

        for (Season s : this.seasons.values()) {
            numberOfEpisodes += s.numberOfEpisodes();
        }

        return numberOfEpisodes;
    }

    public List<Episode> episodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.episodes());
        }

        return episodes;
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        Validate.isNonNull(specification, "specification");

        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.episodesBy(specification));
        }

        return episodes;
    }

    public SeasonSet including(Episode episode) {
        Validate.isNonNull(episode, "episode");

        this.ensuredSeason(episode.seasonNumber()).including(episode);

        return this;
    }

    public SeasonSet includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "episodes");

        for (Episode e : episodes) {
            this.including(e);
        }

        return this;
    }

    public int numberOfSeenEpisodes() {
        int numberOfSeenEpisodes = 0;

        for (Season s : this.seasons.values()) {
            numberOfSeenEpisodes += s.numberOfSeenEpisodes();
        }

        return numberOfSeenEpisodes;
    }

    public Episode nextEpisodeToSee(boolean includingSpecialEpisodes) {
        if (!includingSpecialEpisodes) {
            return this.nextNonSpecialEpisodeToSee();
        }

        Episode nextNonSpecialEpisodeToSee = this.nextNonSpecialEpisodeToSee();
        Episode nextSpecialEpisodeToSee = this.nextSpecialEpisodeToSee();

        if (nextNonSpecialEpisodeToSee == null) {
            return nextSpecialEpisodeToSee;
        }
        if (nextSpecialEpisodeToSee == null) {
            return nextNonSpecialEpisodeToSee;
        }

        return DatesAndTimes.compareByNullLast(nextNonSpecialEpisodeToSee.airDate(),
            nextSpecialEpisodeToSee.airDate()) < 1
            ? nextNonSpecialEpisodeToSee
                : nextSpecialEpisodeToSee;
    }

    private Episode nextSpecialEpisodeToSee() {
        Season specialEpisodes = this.season(SeasonSet.SPECIAL_EPISODES_SEASON_NUMBER);
        return specialEpisodes != null ? specialEpisodes.nextEpisodeToSee() : null;
    }

    private Episode nextNonSpecialEpisodeToSee() {
        for (Season s : this.seasons.values()) {
            if (s.number() == SeasonSet.SPECIAL_EPISODES_SEASON_NUMBER) {
                continue;
            }

            if (!s.wasSeen()) {
                return s.nextEpisodeToSee();
            }
        }

        return null;
    }

    public synchronized SeasonSet mergeWith(SeasonSet other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(this.seriesId == other.seriesId, "other's seriesId should be %d",
            this.seriesId);

        this.mergeExistingSeasonsThatStillExistIn(other);
        this.insertNewSeasonsFrom(other);
        this.removeSeasonsThatNoLongerExistIn(other);

        return this;
    }

    private void mergeExistingSeasonsThatStillExistIn(SeasonSet other) {
        for (Season s : this.seasons.values()) {
            if (other.includes(s)) {
                s.mergeWith(other.season(s.number()));
            }
        }
    }

    private void insertNewSeasonsFrom(SeasonSet other) {
        for (Season s : other.seasons.values()) {
            if (!this.includes(s)) {
                this.including(s);
            }
        }
    }

    private void removeSeasonsThatNoLongerExistIn(SeasonSet other) {
        List<Season> mySeasons = this.seasons();
        for (Season s : mySeasons) {
            if (!other.includes(s)) {
                this.excluding(s);
            }
        }
    }

    @Override
    public boolean register(SeasonSetListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(SeasonSetListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyThatNumberOfSeenEpisodesChanged() {
        for (SeasonSetListener l : this.listeners) {
            l.onChangeNumberOfSeenEpisodes(this);
        }
    }

    private void notifyThatNextEpisodeToSeeChanged() {
        for (SeasonSetListener l : this.listeners) {
            l.onChangeNextEpisodeToSee(this);
        }
    }

    @Override
    public void onMarkAsSeen(Season season) {
        // SeasonSet is not interested in this event
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        // SeasonSet is not interested in this event
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {
        this.notifyThatNumberOfSeenEpisodesChanged();
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        // FIXME Notify only if the general next to see change
        this.notifyThatNextEpisodeToSeeChanged();
    }

    @Override
    public void onMarkAsSeenBySeries(Season season) { }

    @Override
    public void onMarkAsNotSeenBySeries(Season season) { }
}
