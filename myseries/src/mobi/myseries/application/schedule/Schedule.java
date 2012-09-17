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

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.UpdateService;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.Validate;

public class Schedule {
    private SeriesRepository repository;
    private FollowSeriesService following;
    private UpdateService update;

    public Schedule(SeriesRepository seriesRepository, FollowSeriesService following, UpdateService update) {
        Validate.isNonNull(seriesRepository, "repository");
        Validate.isNonNull(following, "following");
        Validate.isNonNull(update, "update");

        this.repository = seriesRepository;
        this.following = following;
        this.update = update;
    }

    public NextToSeeList.Builder nextBuilder() {
        return new NextToSeeList.Builder(this.repository, this.following, this.update);
    }

    public RecentList.Builder recentBuilder() {
        return new RecentList.Builder(this.repository, this.following, this.update);
    }

    public UpcomingList.Builder upcomingBuilder() {
        return new UpcomingList.Builder(this.repository, this.following, this.update);
    }

    //TODO Delete the methods below ASAP--------------------------------------------------------------------------------

    public ScheduleList recent() {
        return this.recentBuilder().build();
    }

    public ScheduleList upcoming() {
        return this.upcomingBuilder().build();
    }

    public ScheduleList next() {
        return this.nextBuilder().build();
    }
}