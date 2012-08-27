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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Schedule {
    private SeriesRepository seriesRepository;

    public Schedule(SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesRepository = seriesRepository;
    }

    public List<Episode> episodes(int scheduleMode, int sortMode) {
        List<Episode> episodes = this.episodes(scheduleMode);

        Collections.sort(episodes, this.episodeComparator(sortMode));

        return episodes;
    }

    public List<Episode> episodes(int scheduleMode) {
        ArrayList<Episode> episodes = new ArrayList<Episode>();

        switch(scheduleMode) {
            case ScheduleMode.RECENT:
                episodes.addAll(this.recent());
                break;
            case ScheduleMode.TODAY:
                episodes.addAll(this.today());
                break;
            case ScheduleMode.UPCOMING:
                episodes.addAll(this.upcoming());
                break;
        }

        return episodes;
    }

    public ScheduleDays days(int scheduleMode, int sortMode) {
        switch(scheduleMode) {
            case ScheduleMode.RECENT:
                return this.daysBy(recentEpisodeSpecification(), sortMode);
            case ScheduleMode.TODAY:
                return this.daysBy(todayEpisodeSpecification(), sortMode);
            case ScheduleMode.UPCOMING:
                return this.daysBy(upcomingEpisodeSpecification(), sortMode);
            default:
                return null;
        }
    }

    public List<Episode> recent() {
        return this.episodesBy(recentEpisodeSpecification());
    }

    public List<Episode> today() {
        return this.episodesBy(todayEpisodeSpecification());
    }

    public List<Episode> upcoming() {
        return this.episodesBy(upcomingEpisodeSpecification());
    }

    private List<Episode> episodesBy(Specification<Episode> specification) {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Series s : this.seriesCollection()) {
            episodes.addAll(s.episodesBy(specification));
        }

        return episodes;
    }

    private ScheduleDays daysBy(Specification<Episode> specification, int sortMode) {
        ScheduleDays days = new ScheduleDays(sortMode);

        for (Series s : this.seriesCollection()) {
            days.addAll(s.episodesBy(specification));
        }

        return days;
    }

    private Collection<Series> seriesCollection() {
        return this.seriesRepository.getAll();
    }

    private static Specification<Episode> recentEpisodeSpecification() {
        return AirdateSpecification.before(Dates.today());
    }

    private static Specification<Episode> todayEpisodeSpecification() {
        return AirdateSpecification.on(Dates.today());
    }

    private static Specification<Episode> upcomingEpisodeSpecification() {
        return AirdateSpecification.after(Dates.today());
    }

    private Comparator<Episode> episodeComparator(int sortMode) {
        switch(sortMode) {
            case SortMode.NEWEST_FIRST:
                return EpisodeComparator.byNewestFirst();
            case SortMode.OLDEST_FIRST:
                return EpisodeComparator.byOldestFirst();
            default:
                return null;
        }
    }
}
