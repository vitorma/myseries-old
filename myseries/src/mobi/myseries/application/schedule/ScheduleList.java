/*
 *   ScheduleList.java
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.SortedList;
import mobi.myseries.shared.Validate;

public abstract class ScheduleList implements Publisher<ScheduleListener>, SeriesFollowingListener {
    private ScheduleParameters parameters;
    private SortedList<HasDate> elements;
    private HashMap<Day, Integer> numberOfEpisodesPerDay;
    private ListenerSet<ScheduleListener> listeners;

    protected ScheduleList(ScheduleParameters parameters, FollowSeriesService following) {
        this.parameters = parameters;
        this.elements = new SortedList<HasDate>(comparator());
        this.numberOfEpisodesPerDay = new HashMap<Day, Integer>();
        this.listeners = new ListenerSet<ScheduleListener>();

        following.registerSeriesFollowingListener(this);
    }

    protected ScheduleParameters parameters() {
        return this.parameters;
    }

    protected SortedList<HasDate> elements() {
        return this.elements;
    }

    public int size() {
        return this.elements.size();
    }

    public HasDate get(int index) {
        HasDate element = this.elements.get(index);

        if (this.parameters().sortMode() == SortMode.OLDEST_FIRST) {
            return element;
        }

        if (this.isEpisode(element)) {
            return this.elements.get(this.size() - index);
        }

        return this.elements.get(this.size() - 1 - index - this.numberOfEpisodesPerDay.get(element));
    }

    public boolean isEpisode(int index) {
        return this.isEpisode(this.get(index));
    }

    protected boolean isEpisode(HasDate element) {
        return element != null && element.getClass() == Episode.class;
    }

    public List<Episode> episodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (HasDate element : this.elements) {
            if (this.isEpisode(element)) {
                episodes.add((Episode) element);
            }
        }

        return episodes;
    }

    protected boolean add(Episode element) {
        Day day = new Day(element.getDate());

        if (!this.numberOfEpisodesPerDay.containsKey(day)) {
            this.numberOfEpisodesPerDay.put(day, 0);
            this.elements.add(day);
        }

        if (!this.elements.contains(element)) {
            this.numberOfEpisodesPerDay.put(day, this.numberOfEpisodesPerDay.get(day) + 1);
            this.elements.add(element);
            return true;
        }

        return false;
    }

    protected boolean remove(Episode element) {
        Day day = new Day(element.getDate());

        boolean removed = this.elements.remove(element);

        if (removed) {
            this.numberOfEpisodesPerDay.put(day, this.numberOfEpisodesPerDay.get(day) - 1);
        }

        if (removed && this.numberOfEpisodesPerDay.get(day) == 0) {
            this.numberOfEpisodesPerDay.remove(day);
            this.elements.remove(day);
        }

        return removed;
    }

    public ScheduleList sortBy(int sortMode) {
        if (this.parameters.sortMode() != sortMode) {
            this.parameters.setSortMode(sortMode);
        }

        return this;
    }

    //Publisher---------------------------------------------------------------------------------------------------------

    @Override
    public boolean register(ScheduleListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(ScheduleListener listener) {
        return this.listeners.deregister(listener);
    }

    protected void notifyListeners() {
        for (ScheduleListener listener : this.listeners) {
            listener.onStateChanged();
        }
    }

    //Comparator--------------------------------------------------------------------------------------------------------

    private static Comparator<HasDate> comparator() {
        return new Comparator<HasDate>() {
            private static final int TYPE_DAY = 0;
            private static final int TYPE_EPISODE = 1;

            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = Dates.compareByNullLast(left.getDate(), right.getDate());

                if (dateComparation != 0) {return dateComparation;}

                return typeOf(left) - typeOf(right);
            }

            private int typeOf(HasDate object) {
                return object.getClass() == Day.class ? TYPE_DAY : TYPE_EPISODE;
            }
        };
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static abstract class Builder {
        protected SeriesRepository repository;
        protected FollowSeriesService following;
        protected ScheduleParameters parameters;

        public Builder(SeriesRepository repository, FollowSeriesService following) {
            Validate.isNonNull(repository, "repository");
            Validate.isNonNull(following, "following");

            this.repository = repository;
            this.following = following;
            this.parameters = new ScheduleParameters();
        }

        public Builder includingSpecialEpisodes(boolean includingSpecialEpisodes) {
            this.parameters.setInclusionOfSpecialEpisodes(includingSpecialEpisodes);
            return this;
        }

        public Builder includingSeenEpisodes(boolean includingSeenEpisodes) {
            this.parameters.setInclusionOfSeenEpisodes(includingSeenEpisodes);
            return this;
        }

        public Builder sortingBy(int sortMode) {
            this.parameters.setSortMode(sortMode);
            return this;
        }

        public abstract ScheduleList build();
    }
}