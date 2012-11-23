/*
 *   ScheduleMode.java
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

package mobi.myseries.application.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.update.UpdateListener;
import mobi.myseries.update.UpdateService;

public abstract class ScheduleMode implements Publisher<ScheduleListener>, SeriesFollowingListener, UpdateListener {
    public static final int RECENT = 0;
    public static final int NEXT = 1;
    public static final int UPCOMING = 2;

    protected ScheduleSpecification specification;
    protected List<Episode> episodes;
    protected SeriesRepository repository;
    private ListenerSet<ScheduleListener> listeners;

    protected ScheduleMode(ScheduleSpecification specification, SeriesRepository repository, FollowSeriesService following, UpdateService update) {
        this.specification = specification;
        this.episodes = new ArrayList<Episode>();

        this.repository = repository;
        this.listeners = new ListenerSet<ScheduleListener>();

        following.registerSeriesFollowingListener(this);
        update.register(this);

        this.loadEpisodes();
        this.sortEpisodes();
    }

    protected abstract void loadEpisodes();

    public int numberOfEpisodes() {
        return this.episodes.size();
    }

    public Episode episodeAt(int position) {
        return this.episodes.get(position);
    }

    public List<Episode> episodes() {
        return new ArrayList<Episode>(this.episodes);
    }

    protected void sortEpisodes() {
        Collections.sort(this.episodes, this.comparator(this.specification.sortMode()));
    }

    private Comparator<Episode> comparator(int sortMode) {
        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                return EpisodeComparator.comparingByOldestFirst();
            case SortMode.NEWEST_FIRST:
                return EpisodeComparator.comparingByNewestFirst();
            default:
                return null;
        }
    }

    /* Publisher<ScheduleListener> */

    @Override
    public boolean register(ScheduleListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(ScheduleListener listener) {
        return this.listeners.deregister(listener);
    }

    protected void notifyOnScheduleStateChanged() {
        for (ScheduleListener listener : this.listeners) {
            listener.onScheduleStateChanged();
        }
    }

    protected void notifyOnScheduleStructureChanged() {
        for (ScheduleListener listener : this.listeners) {
            listener.onScheduleStructureChanged();
        }
    }

    /* SeriesFollowingListener */

    @Override
    public void onFollowingStart(Series seriesToFollow) { }

    @Override
    public final void onFollowing(Series series) {
        this.notifyOnScheduleStructureChanged();
    }

    @Override
    public void onFollowingFailure(Series series, Exception e) { }

    @Override
    public final void onStopFollowing(Series series) {
        this.notifyOnScheduleStructureChanged();
    }

    @Override
    public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
        this.notifyOnScheduleStructureChanged();
    }

    /* UpdateListener */

    @Override
    public final void onUpdateStart() { }

    // TODO(Gabriel) Shouldn't we do something here?
    // Can't the series have been partially updated after a failure?
    @Override
    public final void onUpdateFailure(Exception e) { }

    @Override
    public final void onUpdateSuccess() {
        this.notifyOnScheduleStructureChanged();
    }

    @Override
    public final void onUpdateNotNecessary() { }
}