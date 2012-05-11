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
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Season implements EpisodeListener, Publisher<SeasonListener> {
    private int seriesId;
    private int number;

    private TreeMap<Integer, Episode> episodes;
    private int numberOfSeenEpisodes;
    private Episode nextEpisodeToSee;
    private ListenerSet<SeasonListener> listeners;
    private boolean listening;

    public Season(int seriesId, int number) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");
        Validate.isTrue(number >= 0, "number should be non-negative");

        this.seriesId = seriesId;
        this.number = number;

        this.episodes = new TreeMap<Integer, Episode>();
        this.listeners = new ListenerSet<SeasonListener>();
        this.listening = true;
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
        return episode != null && this.episodes.containsKey(episode.number());
    }

    public Episode episode(int number) {
        return this.episodes.get(number);
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

    public Season including(Episode episode) {
        Validate.isNonNull(episode, "episode");
        Validate.isTrue(episode.seriesId() == this.seriesId, "episode's seriesId should be %d", this.seriesId);
        Validate.isTrue(episode.seasonNumber() == this.number, "episode's seasonNumber should be %d", this.number);
        Validate.isTrue(!this.includes(episode), "episode is already included");

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
        this.listening = false;

        for (Episode e : this.episodes.values()) {
            e.markAsSeen();
        }

        this.numberOfSeenEpisodes = this.numberOfEpisodes();
        this.notifyThatNumberOfSeenEpisodesChanged();
        this.nextEpisodeToSee = null;
        this.notifyThatNextToSeeChanged();
        this.listening = true;
        this.notifyThatWasMarkedAsSeen();

        return this;
    }

    public Season markAsNotSeen() {
        this.listening = false;

        for (Episode e : this.episodes.values()) {
            e.markAsNotSeen();
        }

        this.numberOfSeenEpisodes = 0;
        this.notifyThatNumberOfSeenEpisodesChanged();
        this.nextEpisodeToSee = this.episodes.get(this.episodes.firstKey());
        this.notifyThatNextToSeeChanged();
        this.listening = true;
        this.notifyThatWasMarkedAsNotSeen();

        return this;
    }

    private boolean nextEpisodeToSeeShouldBe(Episode candidate) {
        Episode current = this.nextEpisodeToSee;

        return !candidate.wasSeen() && (current == null || current.number() > candidate.number());
    }

    private Episode findNextEpisodeToSee() {
        for (Episode e : this.episodes.values()) {
            if (!e.wasSeen()) return e;
        }

        return null;
    }

    public Season mergeWith(Season other) {
        Validate.isNonNull(other, "other should be non-null");
        Validate.isTrue(other.seriesId == this.seriesId, "other's seriesId should be %d", this.seriesId);
        Validate.isTrue(other.number == this.number, "other's number should be %d", this.number);

        for (Episode e : this.episodes.values()) {
            if (other.includes(e)) e.mergeWith(other.episode(e.number()));
        }

        for (Episode e : other.episodes.values()) {
            if (!this.includes(e)) this.including(e);
        }

        this.notifyThatWasMerged();

        return this;
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

    private void notifyThatWasMerged() {
        for (SeasonListener l : this.listeners) {
            l.onMerge(this);
        }
    }

    @Override
    public void onMarkAsSeen(Episode episode) {
        if (!this.listening) return;

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
        if (!this.listening) return;

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
    public void onMerge(Episode episode) {
        //Season is not interested in this event
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime * (prime + this.seriesId) + this.number;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Season)) return false;
        Season other = (Season) obj;
        return other.seriesId == this.seriesId && other.number == this.number;
    }
}
