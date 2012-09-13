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
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.application.UpdateListener;
import mobi.myseries.application.UpdateSeriesService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.SortedList;
import mobi.myseries.shared.Validate;

public abstract class ScheduleList implements Publisher<ScheduleListener>, SeriesFollowingListener, UpdateListener {
    private ScheduleParameters parameters;
    private SeriesRepository repository;

    private SortedList<HasDate> elements;
    private TreeMap<Day, Integer> numberOfEpisodesByDay;

    private ListenerSet<ScheduleListener> listeners;

    protected ScheduleList(ScheduleParameters parameters, SeriesRepository repository, FollowSeriesService following, UpdateSeriesService update) {
        this.parameters = parameters;
        this.repository = repository;
        this.elements = new SortedList<HasDate>(comparator(parameters.sortMode()));
        this.numberOfEpisodesByDay = new TreeMap<Day, Integer>();
        this.listeners = new ListenerSet<ScheduleListener>();

        following.registerSeriesFollowingListener(this);
        update.registerSeriesUpdateListener(this);

        this.load();
    }

    protected ScheduleParameters parameters() {
        return this.parameters;
    }

    protected SeriesRepository repository() {
        return this.repository;
    }

    protected SortedList<HasDate> elements() {
        return this.elements;
    }

    public int size() {
        return this.elements.size();
    }

    public HasDate get(int index) {
        return this.elements.get(index);
    }

    public boolean isEpisode(HasDate element) {
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

    protected boolean add(Episode episode) {
        if (!this.parameters.includesEpisodesOfSeries(episode.seriesId())) {
            return false;
        }

        if (this.elements.contains(episode)) {
            return false;
        }

        Day day = new Day(episode.getDate());

        if (this.numberOfEpisodesByDay.containsKey(day)) {
            int numberOfEpisodesOfTheDay = this.numberOfEpisodesByDay.get(day);
            this.numberOfEpisodesByDay.put(day, numberOfEpisodesOfTheDay + 1);
        } else {
            this.numberOfEpisodesByDay.put(day, 1);
            this.elements.add(day);
        }

        return this.elements.add(episode);
    }

    protected boolean remove(Episode episode) {
        if (!this.elements.remove(episode)) {
            return false;
        }

        Day day = new Day(episode.getDate());
        int numberOfEpisodesOfTheDay = this.numberOfEpisodesByDay.get(day);

        this.numberOfEpisodesByDay.put(day, numberOfEpisodesOfTheDay - 1);

        if (this.numberOfEpisodesByDay.get(day) == 0) {
            this.numberOfEpisodesByDay.remove(day);
            this.elements.remove(day);
        }

        return true;
    }

    private void clear() {
        this.elements.clear();
        this.numberOfEpisodesByDay.clear();
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

    //UpdateListener----------------------------------------------------------------------------------------------------

    @Override
    public void onUpdateStart() {}

    @Override
    public void onUpdateFailure() {}

    @Override
    public void onUpdateSuccess() {
        this.clear();
        this.load();
        this.notifyListeners();
    }

    protected abstract void load();

    //Comparator--------------------------------------------------------------------------------------------------------

    private static Comparator<HasDate> comparator(int sortMode) {
        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                return naturalComparator();
            case SortMode.NEWEST_FIRST:
                return reversedComparator();
            default:
                return null;
        }
    }

    private static Comparator<HasDate> naturalComparator() {
        return new Comparator<HasDate>() {
            private static final int TYPE_DAY = 0;
            private static final int TYPE_EPISODE = 1;

            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = Dates.compareByNullLast(left.getDate(), right.getDate());

                if (dateComparation != 0) {
                    return dateComparation;
                }

                int typeComparation = typeOf(left) - typeOf(right);

                if (typeComparation != 0) {
                    return typeComparation;
                }

                if (typeOf(left) == TYPE_DAY) {
                    return 0;
                }

                //Episode comparation

                Episode e1 = (Episode) left;
                Episode e2 = (Episode) right;

                int seriesIdComparation = e1.seriesId() - e2.seriesId();

                if (seriesIdComparation != 0) {
                    return seriesIdComparation;
                }

                int seasonNumberComparation = e1.seasonNumber() - e2.seasonNumber();

                if (seasonNumberComparation != 0) {
                    return seasonNumberComparation;
                }

                return e1.number() - e2.number();
            }

            private int typeOf(HasDate object) {
                return object.getClass() == Day.class ? TYPE_DAY : TYPE_EPISODE;
            }
        };
    }

    private static Comparator<HasDate> reversedComparator() {
        return new Comparator<HasDate>() {
            private static final int TYPE_DAY = 0;
            private static final int TYPE_EPISODE = 1;

            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = Dates.compareByNullLast(right.getDate(), left.getDate());

                if (dateComparation != 0) {
                    return dateComparation;
                }

                int typeComparation = typeOf(left) - typeOf(right);

                if (typeComparation != 0) {
                    return typeComparation;
                }

                if (typeOf(left) == TYPE_DAY) {
                    return 0;
                }

                //Episode comparation

                Episode e2 = (Episode) left;
                Episode e1 = (Episode) right;

                int seriesIdComparation = e1.seriesId() - e2.seriesId();

                if (seriesIdComparation != 0) {
                    return seriesIdComparation;
                }

                int seasonNumberComparation = e1.seasonNumber() - e2.seasonNumber();

                if (seasonNumberComparation != 0) {
                    return seasonNumberComparation;
                }

                return e1.number() - e2.number();
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
        protected UpdateSeriesService update;

        public Builder(SeriesRepository repository, FollowSeriesService following, UpdateSeriesService update) {
            Validate.isNonNull(repository, "repository");
            Validate.isNonNull(following, "following");

            this.repository = repository;
            this.following = following;
            this.update = update;
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

        public Builder includingEpisodesOfAllSeries(Collection<Series> collection) {
            this.parameters.setInclusionOfEpisodesOfAllSeries(collection);
            return this;
        }

        public Builder sortingBy(int sortMode) {
            this.parameters.setSortMode(sortMode);
            return this;
        }

        public abstract ScheduleList build();
    }
}