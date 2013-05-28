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

    private int seriesId;
    private int number;

    private TreeMap<Integer, Episode> episodes;
    // FIXME(Gabriel): Use AtomicInteger or synchronized?
    private int numberOfSeenEpisodes;
    // FIXME(Gabriel): Use AtomicReference or synchronized?
    private Episode nextEpisodeToSee;
    private ListenerSet<SeasonListener> listeners;

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

    public int numberOfEpisodes() {
        return this.episodes.size();
    }

    public boolean includes(Episode episode) {
        return (episode != null) && this.episodes.containsKey(episode.number());
    }

    public Episode episode(int number) {
        return this.episodes.get(number);
    }

    public Episode episodeAt(int position) {
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

    public List<Episode> episodes() {
        return new ArrayList<Episode>(this.episodes.values());
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
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
        validate(episode);
        Validate.isTrue(!this.includes(episode), "episode is already included");
    }

    private void validateIncluded(Episode episode) {
        validate(episode);
        Validate.isTrue(this.includes(episode), "episode is not included");
    }

    public Season including(Episode episode) {
        validateNotIncluded(episode);

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

    public int numberOfSeenEpisodes() {
        return this.numberOfSeenEpisodes;
    }

    public boolean wasSeen() {
        return this.numberOfSeenEpisodes == this.numberOfEpisodes();
    }

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    public Season markAsSeen() {
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

    public Season markAsNotSeen() {
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
            if (!e.wasSeen()) return e;
        }

        return null;
    }

    public Season mergeWith(Season other) {
        // TODO(Gabriel): Replace these validations with an this.equals(other)?
        Validate.isNonNull(other, "other should be non-null");
        Validate.isTrue(other.seriesId == this.seriesId, "other's seriesId should be %d", this.seriesId);
        Validate.isTrue(other.number == this.number, "other's number should be %d", this.number);

        // merge existing episodes 
        for (Episode e : this.episodes.values()) {
            if (other.includes(e)) e.mergeWith(other.episode(e.number()));
        }

        // insert new episodes
        for (Episode e : other.episodes.values()) {
            if (!this.includes(e)) this.insert(e);
        }

        // remove nonexistent episodes
        List<Episode> myEpisodes = new ArrayList<Episode>(this.episodes());
        for (Episode e : myEpisodes) {
            if (!other.includes(e)) this.remove(e);
        }

        return this;
    }

    private void insert(Episode episode) {
        validateNotIncluded(episode);

        if (episode.wasSeen()) {
            this.numberOfSeenEpisodes++;
        }

        if (this.nextEpisodeToSeeShouldBe(episode)) {
            this.nextEpisodeToSee = episode;
            this.notifyThatNextToSeeChanged();
        }

        this.episodes.put(episode.number(), episode);
        episode.register(this);
    }

    private void remove(Episode episode) {
        validateIncluded(episode);

        episode.deregister(this);
        this.episodes.remove(episode.number());

        if (this.nextEpisodeToSee != null && this.nextEpisodeToSee.isTheSameAs(episode)) {
            this.nextEpisodeToSee = findNextEpisodeToSee();
            this.notifyThatNextToSeeChanged();
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
            l.onMarkAsSeen(this);
        }
    }

    private void notifyThatWasMarkedAsNotSeen() {
        for (SeasonListener l : this.listeners) {
            l.onMarkAsNotSeen(this);
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
    public void onMarkAsSeen(Episode episode) {
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
    public void onMarkAsNotSeen(Episode episode) {
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
        if (!(obj instanceof Season)) return false;
        Season other = (Season) obj;
        return (other.seriesId == this.seriesId) && (other.number == this.number);
    }
}
