/*
 *   Schedule.java
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
import java.util.LinkedList;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.repository.SeriesRepositoryListener;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.SortedList;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Schedule implements SeriesRepositoryListener {
    private SeriesRepository seriesRepository;
    private ScheduleElements recent;
    private ScheduleElements upcoming;
    private ScheduleElements next;

    public Schedule(SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesRepository = seriesRepository;
        this.recent = new ScheduleElements();
        this.upcoming = new ScheduleElements();
        this.next = new ScheduleElements();

        this.load();
    }

    public ScheduleElements recent() {
        return this.recent;
    }

    public ScheduleElements upcoming() {
        return this.upcoming;
    }

    public ScheduleElements next() {
        return this.next;
    }

    private void load() {
        this.extractRecentEpisodesFrom(this.seriesCollection());
        this.extractUpcomingEpisodesFrom(this.seriesCollection());
        this.extractNextEpisodesFrom(this.seriesCollection());
    }

    private void extractRecentEpisodesFrom(Collection<Series> collection) {
        for (Series s : collection) {
            this.extractRecentEpisodesFrom(s);
        }
    }

    private void extractRecentEpisodesFrom(Series s) {
        this.recent.addAll(s.episodesBy(recentSpecification()));
    }

    private void extractUpcomingEpisodesFrom(Collection<Series> collection) {
        for (Series s : collection) {
            this.extractUpcomingEpisodesFrom(s);
        }
    }

    private void extractUpcomingEpisodesFrom(Series s) {
        this.upcoming.addAll(s.episodesBy(upcomingSpecification()));
    }

    private void extractNextEpisodesFrom(Collection<Series> collection) {
        for (Series s : collection) {
            this.extractNextEpisodeFrom(s);
        }
    }

    private void extractNextEpisodeFrom(Series s) {
        Episode e = s.nextEpisodeToSee(true);

        if (e != null && e.airDate() != null) {
            this.next.add(e);
        }
    }

    private Collection<Series> seriesCollection() {
        return this.seriesRepository.getAll();
    }

    //Listening and notifying------------------------------------------------------------------------------------------

    @Override
    public void onInsert(Series s) {
        // TODO Run asynchronously and notify specified listeners
        this.extractRecentEpisodesFrom(s);
        this.extractUpcomingEpisodesFrom(s);
        this.extractNextEpisodeFrom(s);
    }

    @Override
    public void onUpdate(Series s) {
        // TODO Run asynchronously and notify specified listeners
        List<Episode> recentOfSeries = s.episodesBy(recentSpecification());
        final int seriesId = s.id();
        this.recent.removeBy(seriesIdSpecification(seriesId));
        this.recent.addAll(recentOfSeries);

        // TODO Implement for upcoming and next
    }

    @Override
    public void onUpdate(Collection<Series> s) {
        // TODO Extract methods
        
    }

    @Override
    public void onDelete(Series s) {
        // TODO Extract methods
        
    }

    @Override
    public void onDelete(Collection<Series> s) {
        // TODO Extract methods
        
    }

    //------------------------------------------------------------------------------------------------------------------

    public static class ScheduleElements {
        private static final Comparator<HasDate> DEFAULT_COMPARATOR = comparator(SortMode.OLDEST_FIRST);

        private SortedList<HasDate> elements;

        private ScheduleElements() {
            this.elements = new SortedList<HasDate>(DEFAULT_COMPARATOR);
        }

        public int size() {
            return this.elements.size();
        }

        public boolean contains(HasDate element) {
            return this.elements.contains(element);
        }

        public HasDate get(int index) {
            return this.elements.get(index);
        }

        public boolean isEpisode(int index) {
            return this.isEpisode(this.get(index));
        }

        public boolean isEpisode(HasDate element) {
            return element != null && element.getClass() == Episode.class;
        }

        public boolean add(Episode element) {
            Day day = new Day(element.airDate());

            return (this.contains(day) || this.elements.add(day)) &&
                   (!this.contains(element) && this.elements.add(element));
        }

        public boolean remove(Episode element) {
            Day day = new Day(element.getDate());

            return (this.elements.remove(element)) &&
                   (this.containsEpisodesOn(day) || this.elements.remove(day));
        }

        public void removeBy(Specification<Episode> specification) {
            this.removeAll(this.episodesBy(specification));
        }

        public List<Episode> episodesBy(Specification<Episode> specification) {
            List<Episode> episodes = new LinkedList<Episode>();

            for (HasDate element : this.elements) {
                if (!this.isEpisode(element)) {continue;}

                Episode episode = (Episode) element;

                if (specification.isSatisfiedBy(episode)) {
                    episodes.add(episode);
                }
            }

            return episodes;
        }

        public List<Episode> getEpisodes() {
            List<Episode> episodes = new ArrayList<Episode>();

            for (HasDate element : this.elements) {
                if (this.isEpisode(element)) {
                    episodes.add((Episode) element);
                }
            }

            return episodes;
        }

        private void addAll(Collection<Episode> collection) {
            for (Episode e : collection) {
                this.add(e);
            }
        }

        private void removeAll(Collection<Episode> collection) {
            for (Episode e : collection) {
                this.remove(e);
            };
        }

        private boolean containsEpisodesOn(Day day) {
            int i = this.elements.indexOf(day) + 1;

            return i < this.size() && this.get(i).hasSameDateAs(day);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private static Comparator<HasDate> comparator(int sortMode) {
        switch(sortMode) {
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
            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = left.getDate().compareTo(right.getDate());

                if (dateComparation != 0) {return dateComparation;}

                return typeComparator().compare(left, right);
            }
        };
    }

    private static Comparator<HasDate> reversedComparator() {
        return new Comparator<HasDate>() {
            @Override
            public int compare(HasDate left, HasDate right) {
                int dateComparation = right.getDate().compareTo(left.getDate());

                if (dateComparation != 0) {return dateComparation;}

                return typeComparator().compare(left, right);
            }
        };
    }

    private static Comparator<HasDate> typeComparator() {
        return new Comparator<HasDate>() {
            private static final int TYPE_DAY = 0;
            private static final int TYPE_EPISODE = 1;

            @Override
            public int compare(HasDate left, HasDate right) {
                return typeOf(left) - typeOf(right);
            }

            private int typeOf(HasDate object) {
                return object.getClass() == Day.class ?
                       TYPE_DAY :
                       TYPE_EPISODE;
            }
        };
    }

    //------------------------------------------------------------------------------------------------------------------

    private static Specification<Episode> recentSpecification() {
        return AirdateSpecification.before(Dates.now());
    }

    private static Specification<Episode> upcomingSpecification() {
        return AirdateSpecification.after(Dates.now());
    }

    private static Specification<Episode> seriesIdSpecification(final int seriesId) {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.seriesId() == seriesId;
            }
        };
    }
}
