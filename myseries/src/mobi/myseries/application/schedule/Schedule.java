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
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.Validate;

public class Schedule {
    private SeriesRepository seriesRepository;
    private FollowSeriesService followSeriesService;

    public Schedule(SeriesRepository seriesRepository, FollowSeriesService followSeriesService) {
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(followSeriesService, "followSeriesService");

        this.seriesRepository = seriesRepository;
        this.followSeriesService = followSeriesService;
    }

    public NextToSeeList.Builder nextBuilder() {
        return new NextToSeeList.Builder(this.seriesRepository, this.followSeriesService);
    }

    public RecentList.Builder recentBuilder() {
        return new RecentList.Builder(this.seriesRepository, this.followSeriesService);
    }

    public UpcomingList.Builder upcomingBuilder() {
        return new UpcomingList.Builder(this.seriesRepository, this.followSeriesService);
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