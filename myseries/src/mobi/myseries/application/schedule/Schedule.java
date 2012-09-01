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
import java.util.List;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

/*
 * FIXME Handling null dates.
 *
 *       Currently, episodes with null date are not added to the schedule.
 *       This approach is ok for the Recent and Upcomig modes but it does not work for the Next mode.
 *       Once Series#nextToSee has a lenient behavior in case Episode#getDate return null (comparing episodes by their
 *       numbers and by the numbers of their seasons), the next episode to see is allways added to the schedule.
 *       When such situation occurs, the application breaks [rule: Validate#isNonNull(date)].
 *
 *           Behavior of Series#nextToSee.
 *
 *               Should episodes with null date be disregarded?
 *               Should this method allways return the oldest not seen episode?
 *               Special episodes will may be hidden.
 *               How to get the next if the actual next is hidden? Currently, Series#nextToSee(false).
 *
 *           Showing null dates as UNNAVAILABLE DATE is an alternative for all schedule modes if the lenient behavior of
 *           Series#nextToSee is the standard behavior.
 *
 *               What is the mode for special episodes with null date? Upcoming or Recent? 
 */

public class Schedule implements SeriesFollowingListener, SeriesListener, SeasonListener, EpisodeListener {
    private SeriesRepository seriesRepository;
    private FollowSeriesService followSeriesService;

    private ScheduleElements recent;
    private ScheduleElements upcoming;
    private ScheduleElements next;

    private Notifier recentListenerNotifier;
    private Notifier upcomingListenerNotifier;
    private Notifier nextListenerNotifier;

    public Schedule(SeriesRepository seriesRepository, FollowSeriesService followSeriesService) {
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(followSeriesService, "followSeriesService");

        this.seriesRepository = seriesRepository;
        this.followSeriesService = followSeriesService;

        this.recent = new ScheduleElements();
        this.upcoming = new ScheduleElements();
        this.next = new ScheduleElements();

        this.recentListenerNotifier = new Notifier();
        this.upcomingListenerNotifier = new Notifier();
        this.nextListenerNotifier = new Notifier();

        this.load();
        this.registerItselfForListening();
    }

    public ScheduleElements recent() {
        return this.recent.copy();
    }

    public ScheduleElements upcoming() {
        return this.upcoming.copy();
    }

    public ScheduleElements next() {
        return this.next.copy();
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
        List<Episode> r = s.episodesBy(recentNotSeenSpecification());
        this.recent.addAll(r);
    }

    private void extractUpcomingEpisodesFrom(Collection<Series> collection) {
        for (Series s : collection) {
            this.extractUpcomingEpisodesFrom(s);
        }
    }

    private void extractUpcomingEpisodesFrom(Series s) {
        this.upcoming.addAll(s.episodesBy(upcomingNotSeenSpecification()));
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

    private static Specification<Episode> recentNotSeenSpecification() {
        return recentSpecification().and(notSeenSpecification());
    }

    private static Specification<Episode> upcomingNotSeenSpecification() {
        return upcomingSpecification().and(notSeenSpecification());
    }

    private static Specification<Episode> recentSpecification() {
        return AirdateSpecification.before(Dates.now());
    }

    private static Specification<Episode> upcomingSpecification() {
        return AirdateSpecification.after(Dates.now());
    }

    private static Specification<Episode> notSeenSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.wasNotSeen();
            }
        };
    }

    private static Specification<Episode> seriesIdSpecification(final int seriesId) {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.seriesId() == seriesId;
            }
        };
    }

    //Registering its listeners and itself as listener-----------------------------------------------------------------

    public boolean registerAsRecentListener(ScheduleListener listener) {
        return this.recentListenerNotifier.register(listener);
    }

    public boolean registerAsUpcomingListener(ScheduleListener listener) {
        return this.upcomingListenerNotifier.register(listener);
    }

    public boolean registerAsNextListener(ScheduleListener listener) {
        return this.nextListenerNotifier.register(listener);
    }

    private void registerItselfForListening() {
        this.followSeriesService.registerSeriesFollowingListener(this);

        for (Series series : this.seriesCollection()) {
            series.register(this);

            for (Season season : series.seasons().seasons()) {
                season.register(this);
            }

            for (Episode episode : series.episodes()) {
                episode.register(this);
            }
        }
    }

    //TODO Listening SeriesFollowing------------------------------------------------------------------------------------

    @Override
    public void onFollowing(Series series) {
        series.register(this);

        for (Season season : series.seasons().seasons()) {
            season.register(this);
        }

        for (Episode episode : series.episodes()) {
            episode.register(this);
        }

        List<Episode> recentEpisodes = series.episodesBy(recentNotSeenSpecification());
        List<Episode> upcomingEpisodes = series.episodesBy(upcomingNotSeenSpecification());
        Episode nextToSee = series.nextEpisodeToSee(true);

        if (!recentEpisodes.isEmpty()) {
            this.recent.addAll(recentEpisodes);
            this.recentListenerNotifier.notifyThatWereAdded(recentEpisodes);
        }

        if (!upcomingEpisodes.isEmpty()) {
            this.upcoming.addAll(upcomingEpisodes);
            this.upcomingListenerNotifier.notifyThatWereAdded(upcomingEpisodes);
        }

        if (nextToSee != null && nextToSee.getDate() != null) {
            this.next.add(nextToSee);
            this.nextListenerNotifier.notifyThatWasAdded(nextToSee);
        }
    }

    @Override
    public void onStopFollowing(Series series) {
        series.deregister(this);

        for (Season season : series.seasons().seasons()) {
            season.deregister(this);
        }

        for (Episode episode : series.episodes()) {
            episode.deregister(this);
        }

        List<Episode> recentEpisodes = series.episodesBy(recentNotSeenSpecification());
        List<Episode> upcomingEpisodes = series.episodesBy(upcomingNotSeenSpecification());
        Episode nextToSee = series.nextEpisodeToSee(true);

        if (!recentEpisodes.isEmpty()) {
            this.recent.removeAll(recentEpisodes);
            this.recentListenerNotifier.notifyThatWereRemoved(recentEpisodes);
        }

        if (!upcomingEpisodes.isEmpty()) {
            this.upcoming.removeAll(upcomingEpisodes);
            this.upcomingListenerNotifier.notifyThatWereRemoved(upcomingEpisodes);
        }

        if (nextToSee != null && nextToSee.getDate() != null) {
            this.next.remove(nextToSee);
            this.nextListenerNotifier.notifyThatWasRemoved(nextToSee);
        }
    }

    //Listening Series--------------------------------------------------------------------------------------------------

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        Episode oldNextToSee = this.next.firstEpisodeBy(seriesIdSpecification(series.id()));

        if (oldNextToSee != null) {
            this.next.remove(oldNextToSee);
            this.nextListenerNotifier.notifyThatWasRemoved(oldNextToSee);
        }

        Episode newNextToSee = series.nextEpisodeToSee(true);

        if (newNextToSee != null) {
            this.next.add(newNextToSee);
            this.nextListenerNotifier.notifyThatWasAdded(newNextToSee);
        }
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {}

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {}

    //Listening Season--------------------------------------------------------------------------------------------------

    @Override
    public void onMarkAsSeen(Season season) {
        List<Episode> recentEpisodes = season.episodesBy(recentSpecification());
        List<Episode> upcomingEpisodes = season.episodesBy(upcomingSpecification());

        if (!recentEpisodes.isEmpty()) {
            this.recent.removeAll(recentEpisodes);
            this.recentListenerNotifier.notifyThatWereRemoved(recentEpisodes);
        }

        if (!upcomingEpisodes.isEmpty()) {
            this.upcoming.removeAll(upcomingEpisodes);
            this.upcomingListenerNotifier.notifyThatWereRemoved(upcomingEpisodes);
        }
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        List<Episode> recentEpisodes = season.episodesBy(recentSpecification());
        List<Episode> upcomingEpisodes = season.episodesBy(upcomingSpecification());

        if (!recentEpisodes.isEmpty()) {
            this.recent.addAll(recentEpisodes);
            this.recentListenerNotifier.notifyThatWereAdded(recentEpisodes);
        }

        if (!upcomingEpisodes.isEmpty()) {
            this.upcoming.addAll(upcomingEpisodes);
            this.upcomingListenerNotifier.notifyThatWereAdded(upcomingEpisodes);
        }
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {}

    @Override
    public void onChangeNextEpisodeToSee(Season season) {}

    //Listening Episode-------------------------------------------------------------------------------------------------

    @Override
    public void onMarkAsSeen(Episode episode) {
        if (recentSpecification().isSatisfiedBy(episode)) {
            this.recent.remove(episode);
            this.recentListenerNotifier.notifyThatWasRemoved(episode);
        }

        if (upcomingSpecification().isSatisfiedBy(episode)) {
            this.upcoming.remove(episode);
            this.upcomingListenerNotifier.notifyThatWasRemoved(episode);
        }
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        if (recentSpecification().isSatisfiedBy(episode)) {
            this.recent.add(episode);
            this.recentListenerNotifier.notifyThatWasAdded(episode);
            return;
        }

        if (upcomingSpecification().isSatisfiedBy(episode)) {
            this.upcoming.add(episode);
            this.upcomingListenerNotifier.notifyThatWasAdded(episode);
        }
    }

    @Override
    public void onMarkAsSeenBySeason(Episode episode) {}

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {}

    //Notifier----------------------------------------------------------------------------------------------------------

    private static class Notifier implements Publisher<ScheduleListener> {
        private ListenerSet<ScheduleListener> listeners;

        private Notifier() {
            this.listeners = new ListenerSet<ScheduleListener>();
        }

        private void notifyThatWasAdded(Episode episode) {
            for (ScheduleListener listener : this.listeners) {
                listener.onAdd(episode);
            }
        }

        private void notifyThatWereAdded(Collection<Episode> episodes) {
            for (ScheduleListener listener : this.listeners) {
                listener.onAdd(episodes);
            }
        }

        private void notifyThatWasRemoved(Episode episode) {
            for (ScheduleListener listener : this.listeners) {
                listener.onRemove(episode);
            }
        }

        private void notifyThatWereRemoved(Collection<Episode> episodes) {
            for (ScheduleListener listener : this.listeners) {
                listener.onRemove(episodes);
            }
        }

        @Override
        public boolean register(ScheduleListener listener) {
            return this.listeners.register(listener);
        }

        @Override
        public boolean deregister(ScheduleListener listener) {
            return this.listeners.deregister(listener);
        }
    }

    //TODO Delete me ASAP-----------------------------------------------------------------------------------------------

    @Override
    public void onMerge(Episode episode) {}

    @Override
    public void onMerge(Series series) {}

    @Override
    public void onMerge(Season season) {}
}