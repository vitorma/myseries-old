/*
 *   Season.java
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
import java.util.List;
import java.util.TreeMap;

import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Season implements EpisodeListener, Publisher<SeasonListener> {
    public static int SPECIAL_EPISODES_SEASON_NUMBER = 0;

    private final int seriesId;
    private final int number;

    // The next 3 attributes have mutual dependency, thus all methods that read or write
    // to them must be synchronized.
    private final TreeMap<Integer, Episode> episodes;
    private int numberOfSeenEpisodes;
    private Episode nextEpisodeToSee;

    private final ListenerSet<SeasonListener> listeners;
    private volatile boolean beingMarkedBySeries;

    public Season(int seriesId, int number) {
        Validate.isTrue(number >= 0, "number should be non-negative");

        this.seriesId = seriesId;
        this.number = number;

        this.episodes = new TreeMap<Integer, Episode>();
        this.listeners = new ListenerSet<SeasonListener>();
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public boolean isSpecial() {
        return this.number == SPECIAL_EPISODES_SEASON_NUMBER;
    }

    public synchronized int numberOfEpisodes() {
        return this.episodes.size();
    }

    public synchronized int numberOfEpisodes(Specification<Episode> specification) {
        return this.episodesBy(specification).size();
    }

    public synchronized boolean includes(Episode episode) {
        return (episode != null) && this.episodes.containsKey(episode.number());
    }

    public synchronized Episode episode(int number) {
        return this.episodes.get(number);
    }

    public synchronized Episode episodeAt(int position) {
        int i = 0;

        for (Integer episodeNumber : this.episodes.keySet()) {
            if (i == position) {
                return this.episode(episodeNumber);
            }

            i++;
        }

        throw new IndexOutOfBoundsException(
            "invalid position, " + position + ", should be in the range [0, " + this.numberOfEpisodes() + ")");
    }

    public synchronized List<Episode> episodes() {
        return new ArrayList<Episode>(this.episodes.values());
    }

    public synchronized List<Episode> episodesBy(Specification<Episode> specification) {
        Validate.isNonNull(specification, "specification");

        List<Episode> result = new ArrayList<Episode>();

        for (Episode e : this.episodes.values()) {
            if (specification.isSatisfiedBy(e)) {
                result.add(e);
            }
        }

        return result;
    }

    private void validate(Episode episode) {
        Validate.isNonNull(episode, "episode");
        Validate.isTrue(episode.seriesId() == this.seriesId, "episode's seriesId should be %d", this.seriesId);
        Validate.isTrue(episode.seasonNumber() == this.number, "episode's seasonNumber should be %d", this.number);
    }

    private void validateNotIncluded(Episode episode) {
        this.validate(episode);
        Validate.isTrue(!this.includes(episode), "episode is already included");
    }

    private void validateIncluded(Episode episode) {
        this.validate(episode);
        Validate.isTrue(this.includes(episode), "episode is not included");
    }

    public synchronized Season including(Episode episode) {
        this.validateNotIncluded(episode);

        if (episode.wasSeen()) {
            this.numberOfSeenEpisodes++;
            this.notifyThatNumberOfSeenEpisodesChanged();
        }

        if (!episode.wasSeen() && this.wasSeen()) {
            this.notifyThatWasMarkedAsNotSeen();
        }

        if (this.nextEpisodeToSeeShouldBe(episode)) {
            this.nextEpisodeToSee = episode;
            this.notifyThatNextToSeeChanged();
        }

        this.episodes.put(episode.number(), episode);
        episode.register(this);

        return this;
    }

    public synchronized int numberOfSeenEpisodes() {
        return this.numberOfSeenEpisodes;
    }

    public synchronized boolean wasSeen() {
        return this.numberOfSeenEpisodes == this.numberOfEpisodes();
    }

    public synchronized Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public synchronized Season markAsSeen() {
        for (Episode e : this.episodes.values()) {
            e.setBeingMarkedBySeason(true);
            e.markAsSeen();
            e.setBeingMarkedBySeason(false);
        }

        this.numberOfSeenEpisodes = this.numberOfEpisodes();
        this.notifyThatNumberOfSeenEpisodesChanged();
        this.nextEpisodeToSee = null;
        this.notifyThatNextToSeeChanged();
        this.notifyThatWasMarkedAsSeen();

        return this;
    }

    public synchronized Season markAsNotSeen() {
        for (Episode e : this.episodes.values()) {
            e.setBeingMarkedBySeason(true);
            e.markAsNotSeen();
            e.setBeingMarkedBySeason(false);
        }

        this.numberOfSeenEpisodes = 0;
        this.notifyThatNumberOfSeenEpisodesChanged();
        this.nextEpisodeToSee = this.episodes.get(this.episodes.firstKey());
        this.notifyThatNextToSeeChanged();
        this.notifyThatWasMarkedAsNotSeen();

        return this;
    }

    private boolean nextEpisodeToSeeShouldBe(Episode candidate) {
        Episode current = this.nextEpisodeToSee;

        return !candidate.wasSeen() && ((current == null) || (current.number() > candidate.number()));
    }

    private Episode findNextEpisodeToSee() {
        for (Episode e : this.episodes.values()) {
            if (!e.wasSeen()) {
                return e;
            }
        }

        return null;
    }

    public synchronized Season mergeWith(Season other) {
        // TODO(Gabriel): Replace these validations with an this.equals(other)?
        Validate.isNonNull(other, "other should be non-null");
        Validate.isTrue(other.seriesId == this.seriesId, "other's seriesId should be %d", this.seriesId);
        Validate.isTrue(other.number == this.number, "other's number should be %d", this.number);

        this.mergeExistingEpisodesThatStillExistIn(other);
        this.insertNewEpisodesFrom(other);
        this.removeEpisodesThatNoLongerExistIn(other);

        return this;
    }

    private synchronized void mergeExistingEpisodesThatStillExistIn(Season other) {
        for (Episode e : this.episodes.values()) {
            if (other.includes(e)) {
                e.mergeWith(other.episode(e.number()));
            }
        }
    }

    private synchronized void insertNewEpisodesFrom(Season other) {
        for (Episode e : other.episodes.values()) {
            if (!this.includes(e)) {
                this.insert(e);
            }
        }
    }

    private synchronized void removeEpisodesThatNoLongerExistIn(Season other) {
        List<Episode> myEpisodes = new ArrayList<Episode>(this.episodes());
        for (Episode e : myEpisodes) {
            if (!other.includes(e)) {
                this.remove(e);
            }
        }
    }

    private synchronized void insert(Episode episode) {
        this.validateNotIncluded(episode);

        if (episode.wasSeen()) {
            this.numberOfSeenEpisodes++;
        }

        if (this.nextEpisodeToSeeShouldBe(episode)) {
            this.nextEpisodeToSee = episode;
        }

        this.episodes.put(episode.number(), episode);
        episode.register(this);
    }

    private synchronized void remove(Episode episode) {
        this.validateIncluded(episode);

        episode.deregister(this);
        this.episodes.remove(episode.number());

        if (this.nextEpisodeToSee != null && this.nextEpisodeToSee.isTheSameAs(episode)) {
            this.nextEpisodeToSee = this.findNextEpisodeToSee();
        }

        if (episode.wasSeen()) {
            this.numberOfSeenEpisodes--;
        }
    }

    @Override
    public boolean register(SeasonListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(SeasonListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyThatWasMarkedAsSeen() {
        for (SeasonListener l : this.listeners) {
            if (this.beingMarkedBySeries) {
                l.onMarkAsSeenBySeries(this);
            } else {
                l.onMarkAsSeen(this);
            }
        }
    }

    private void notifyThatWasMarkedAsNotSeen() {
        for (SeasonListener l : this.listeners) {
            if (this.beingMarkedBySeries) {
                l.onMarkAsNotSeenBySeries(this);
            } else {
                l.onMarkAsNotSeen(this);
            }
        }
    }

    private void notifyThatNumberOfSeenEpisodesChanged() {
        for (SeasonListener l : this.listeners) {
            l.onChangeNumberOfSeenEpisodes(this);
        }
    }

    private void notifyThatNextToSeeChanged() {
        for (SeasonListener l : this.listeners) {
            l.onChangeNextEpisodeToSee(this);
        }
    }

    @Override
    public synchronized void onMarkAsSeen(Episode episode) {
        this.numberOfSeenEpisodes++;
        this.notifyThatNumberOfSeenEpisodesChanged();

        if (this.wasSeen()) {
            this.notifyThatWasMarkedAsSeen();
            this.nextEpisodeToSee = null;
            this.notifyThatNextToSeeChanged();
        }

        if (!this.wasSeen() && episode.equals(this.nextEpisodeToSee)) {
            this.nextEpisodeToSee = this.findNextEpisodeToSee();
            this.notifyThatNextToSeeChanged();
        }
    }

    @Override
    public synchronized void onMarkAsNotSeen(Episode episode) {
        if (this.wasSeen()) {
            this.notifyThatWasMarkedAsNotSeen();
        }

        if (this.nextEpisodeToSeeShouldBe(episode)) {
            this.nextEpisodeToSee = episode;
            this.notifyThatNextToSeeChanged();
        }

        this.numberOfSeenEpisodes--;
        this.notifyThatNumberOfSeenEpisodesChanged();
    }

    @Override
    public void onMarkAsSeenBySeason(Episode episode) {}

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {}

    @Override
    public int hashCode() {
        final int prime = 31;
        return (prime * (prime + this.seriesId)) + this.number;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Season)) {
            return false;
        }
        Season other = (Season) obj;
        return (other.seriesId == this.seriesId) && (other.number == this.number);
    }

    public void setBeingMarkedBySeries(boolean b) {
        this.beingMarkedBySeries = b;
    }
}
