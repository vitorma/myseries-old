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
    private static final int SPECIAL_EPISODES_SEASON_NUMBER = 0;
    private int seriesId;

    private TreeMap<Integer, Season> seasons;
    private int numberOfSeenEpisodes;
    private Episode nextEpisodeToSee;
    private Episode nextNonSpecialEpisodeToSee;
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

    public boolean includes(Season season) {
        return season != null && this.seasons.containsKey(season.number());
    }

    public Season season(int number) {
        return this.seasons.get(number);
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

    private SeasonSet including(Season season) {
        season.register(this);

        this.seasons.put(season.number(), season);

        return this;
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

    //Seen--------------------------------------------------------------------------------------------------------------

    public int numberOfSeenEpisodes() {
        return this.numberOfSeenEpisodes;
    }

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

    public Episode nextNonSpecialEpisodeToSee() {
        return this.nextNonSpecialEpisodeToSee;
    }

    //Merge-------------------------------------------------------------------------------------------------------------

    public SeasonSet mergeWith(SeasonSet other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(this.seriesId == other.seriesId, "other's seriesId should be %d", this.seriesId);

        for (Season s : this.seasons.values()) {
            if (other.includes(s)) s.mergeWith(other.season(s.number()));
        }

        for (Season s : other.seasons.values()) {
            if (!this.includes(s)) this.including(s);
        }

        this.notifyThatWasMerged();

        return this;
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

    private void notifyThatNextNonSpecialEpisodeToSeeChanged() {
        for (SeasonSetListener l : this.listeners) {
            l.onChangeNextNonSpecialEpisodeToSee(this);
        }
    }

    private void notifyThatWasMerged() {
        for (SeasonSetListener l : this.listeners) {
            l.onMerge(this);
        }
    }

    //SeasonListener----------------------------------------------------------------------------------------------------

    @Override
    public void onMarkAsSeen(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        //SeasonSet is not interested in this event
    }

    @Override
    public void onIncreaseNumberOfSeenEpisodes(Season season) {
        this.numberOfSeenEpisodes++;
        this.notifyThatNumberOfSeenEpisodesChanged();
    }

    @Override
    public void onDecreaseNumberOfSeenEpisodes(Season season) {
        this.numberOfSeenEpisodes--;
        this.notifyThatNumberOfSeenEpisodesChanged();
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        if (this.nextEpisodeToSeeShouldBeThatOf(season)) {
            this.nextEpisodeToSee = season.nextEpisodeToSee();
            this.notifyThatNextEpisodeToSeeChanged();
        }

        if (season.number() != SPECIAL_EPISODES_SEASON_NUMBER) {
            this.nextNonSpecialEpisodeToSee = season.nextEpisodeToSee();
            this.notifyThatNextNonSpecialEpisodeToSeeChanged();
        }
    }

    @Override
    public void onMerge(Season season) {
        //SeasonSet is not interested in this event
    }
}
