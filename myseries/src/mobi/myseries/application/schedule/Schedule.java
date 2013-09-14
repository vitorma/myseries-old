package mobi.myseries.application.schedule;

import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.shared.Validate;

public class Schedule {
    private MarkingService mMarking;
    private SeriesFollowingService mFollowing;
    private UpdateService mUpdate;

    public Schedule(SeriesFollowingService following, UpdateService update, MarkingService marking) {
        Validate.isNonNull(following, "following");
        Validate.isNonNull(update, "update");
        Validate.isNonNull(marking, "marking");

        mMarking = marking;
        mFollowing = following;
        mUpdate = update;
    }

    public ScheduleMode toWatch(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new ToWatch(specification, mFollowing, mUpdate, mMarking);
    }

    public ScheduleMode aired(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new Aired(specification, mFollowing, mUpdate, mMarking);
    }

    public ScheduleMode unaired(ScheduleSpecification specification) {
        Validate.isNonNull(specification, "specification");

        return new Unaired(specification, mFollowing, mUpdate, mMarking);
    }
}