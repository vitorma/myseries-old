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

import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.repository.series.SeriesRepository;
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

    public ScheduleMode toWatch(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new ToWatch(specification, this.repository, this.following, this.update);
    }

    public ScheduleMode aired(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new Aired(specification, this.repository, this.following, this.update);
    }

    public ScheduleMode unaired(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new Unaired(specification, this.repository, this.following, this.update);
    }
}