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

package br.edu.ufcg.aweseries.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Validate;

public class SeasonSet implements SeasonListener {
    private int seriesId;

    private TreeMap<Integer, Season> seasons;
    private Episode nextEpisodeToSee;
    private List<SeasonSetListener> listeners;

    //Construction------------------------------------------------------------------------------------------------------

    public SeasonSet(int seriesId) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");

        this.seriesId = seriesId;

        this.seasons = new TreeMap<Integer, Season>();
        this.listeners = new LinkedList<SeasonSetListener>();
    }

    //Immutable---------------------------------------------------------------------------------------------------------

    public int seriesId() {
        return this.seriesId;
    }

    //Seasons-----------------------------------------------------------------------------------------------------------

    public int numberOfSeasons() {
        return this.seasons.size();
    }

    private boolean includesSeason(int seasonNumber) {
        return this.seasons.containsKey(seasonNumber);
    }

    public Season season(int number) {
        return this.seasons.get(number);
    }

    public List<Season> seasons() {
        return new ArrayList<Season>(this.seasons.values());
    }

    private SeasonSet including(Season season) {
        season.register(this);

        this.seasons.put(season.number(), season);

        return this;
    }

    private Season createSeason(int seasonNumber) {
        return new Season(this.seriesId, seasonNumber);
    }

    //Episodes----------------------------------------------------------------------------------------------------------

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
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.episodesBy(specification));
        }

        return episodes;
    }

    //TODO A more beautiful code
    public SeasonSet including(Episode episode) {
        Validate.isNonNull(episode, "episode");
        Validate.isTrue(episode.seriesId() == this.seriesId, "episode's seriesId should be %d", this.seriesId);

        if (!this.includesSeason(episode.seasonNumber())) {
            this.including(this.createSeason(episode.seasonNumber()));
        }

        this.season(episode.seasonNumber()).including(episode);

        return this;
    }

    public SeasonSet includingAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "episodes");

        for (Episode e : episodes) {
            this.including(e);
        }

        return this;
    }

    //Seen--------------------------------------------------------------------------------------------------------------

    public Episode nextEpisodeToSee() {
        return this.nextEpisodeToSee;
    }

    private boolean nextEpisodeToSeeShouldBeThatOf(Season season) {
        Episode current = this.nextEpisodeToSee;
        Episode candidate = season.nextEpisodeToSee();

        return (current == null && candidate != null) ||
               (current != null && candidate == null && current.seasonNumber() == season.number()) ||
               (current != null && candidate != null && Dates.compare(current.airDate(), candidate.airDate()) > 0);
    }

    public int numberOfSeenEpisodes() {
        int numberOfSeenEpisodes = 0;

        for (Season s : this.seasons.values()) {
            numberOfSeenEpisodes += s.numberOfSeenEpisodes();
        }

        return numberOfSeenEpisodes;
    }

    //Merge-------------------------------------------------------------------------------------------------------------

    public void mergeWith(SeasonSet other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(this.seriesId == other.seriesId, "other's seriesId should be %d", this.seriesId);

        for (Season s : this.seasons.values()) {
            if (other.includesSeason(s.number())) {
                s.mergeWith(other.season(s.number()));
            }
        }

        for (Season s : other.seasons.values()) {
            if (!this.includesSeason(s.number())) {
                this.including(s);
            }
        }

        this.notifyThatWasMerged();
    }

    //SeasonSetListener-------------------------------------------------------------------------------------------------

    public boolean register(SeasonSetListener listener) {
        Validate.isNonNull(listener, "listener");

        for (SeasonSetListener l : this.listeners) {
            if (l == listener) return false;
        }

        return this.listeners.add(listener);
    }

    public boolean deregister(SeasonSetListener listener) {
        Validate.isNonNull(listener, "listener");

        for (int i = 0; i < this.listeners.size(); i++) {
            if (this.listeners.get(i) == listener) {
                this.listeners.remove(i);
                return true;
            }
        }

        return false;
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

    private void notifyThatWasMerged() {
        for (SeasonSetListener l : this.listeners) {
            l.onMerge(this);
        }
    }

    //SeasonListener----------------------------------------------------------------------------------------------------

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        if (this.nextEpisodeToSeeShouldBeThatOf(season)) {
            this.nextEpisodeToSee = season.nextEpisodeToSee();
            this.notifyThatNextEpisodeToSeeChanged();
        }
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onMarkAsSeen(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onMerge(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {
        this.notifyThatNumberOfSeenEpisodesChanged();
    }
}
