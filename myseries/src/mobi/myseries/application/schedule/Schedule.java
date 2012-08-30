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

import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.domain.repository.SeriesRepositoryListener;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Schedule implements SeriesRepositoryListener, SeriesListener, EpisodeListener {
    private SeriesRepository seriesRepository;
    private ExecutorService threadExecutor;

    private ScheduleElements recent;
    private ScheduleElements upcoming;
    private ScheduleElements next;

    public Schedule(SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesRepository = seriesRepository;
        this.threadExecutor = Executors.newSingleThreadExecutor();

        this.recent = new ScheduleElements();
        this.upcoming = new ScheduleElements();
        this.next = new ScheduleElements();

        this.load();
        this.registerForListening();
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

    private Collection<Series> seriesCollection() {
        return this.seriesRepository.getAll();
    }

    //Loading-----------------------------------------------------------------------------------------------------------

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

    //Episode specification---------------------------------------------------------------------------------------------

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

    //Listening and notifying------------------------------------------------------------------------------------------

    private void registerForListening() {
        this.seriesRepository.register(this);

        for (Series s : this.seriesCollection()) {
            s.register(this);

            for (Episode e : s.episodes()) {
                e.register(this);
            }
        }
    }

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
        this.recent.removeBy(seriesIdSpecification(s.id()));
        this.upcoming.removeBy(seriesIdSpecification(s.id()));
        this.next.removeBy(seriesIdSpecification(s.id()));

        this.extractRecentEpisodesFrom(s);
        this.extractUpcomingEpisodesFrom(s);
        this.extractNextEpisodeFrom(s);
    }

    @Override
    public void onUpdate(Episode e) {}

    @Override
    public void onUpdate(Collection<Series> collection) {
        // TODO Run asynchronously and notify specified listeners
        for (Series s : collection) {
            this.recent.removeBy(seriesIdSpecification(s.id()));
            this.upcoming.removeBy(seriesIdSpecification(s.id()));
            this.next.removeBy(seriesIdSpecification(s.id()));

            this.extractRecentEpisodesFrom(s);
            this.extractUpcomingEpisodesFrom(s);
            this.extractNextEpisodeFrom(s);
        }
    }

    @Override
    public void onDelete(Series s) {
        // TODO Run asynchronously and notify specified listeners
        this.recent.removeBy(seriesIdSpecification(s.id()));
        this.upcoming.removeBy(seriesIdSpecification(s.id()));
        this.next.removeBy(seriesIdSpecification(s.id()));
    }

    @Override
    public void onDelete(Collection<Series> collection) {
        // TODO Run asynchronously and notify specified listeners
        for (Series s : collection) {
            this.recent.removeBy(seriesIdSpecification(s.id()));
            this.upcoming.removeBy(seriesIdSpecification(s.id()));
            this.next.removeBy(seriesIdSpecification(s.id()));
        }
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {}

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {}

    @Override
    public void onMerge(Series series) {}

    @Override
    public void onMarkAsSeen(Episode episode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onMerge(Episode episode) {}

    private static class RecentNotifier {
        private ListenerSet<RecentListener> listeners;
    }

    private static interface EpisodeCollectionListener {
        public void onRemove(Collection<Episode> e);
        public void onAdd(Collection<Episode> e);
    }

    public static interface RecentListener extends EpisodeListener, EpisodeCollectionListener {}

    public static interface UpcomingListener extends EpisodeListener, EpisodeCollectionListener {}

    public static interface NextListener extends EpisodeCollectionListener {
        public void onChange(int index, Episode e);
    }
}