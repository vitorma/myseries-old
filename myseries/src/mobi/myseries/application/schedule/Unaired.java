package mobi.myseries.application.schedule;

import java.util.ArrayList;
import java.util.List;

import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Specification;

public class Unaired extends TimelineMode {

    public Unaired(
            ScheduleSpecification specification,
            SeriesFollowingService following,
            UpdateService update,
            MarkingService marking) {
        super(specification, following, update, marking);
    }

    public List<Episode> nextEpisodes() {
        if (numberOfEpisodes() == 0) {
            return new ArrayList<Episode>();
        }

        List<Episode> result = new ArrayList<Episode>();

        Episode first = episodeAt(positionAtTime(0));
        result.add(first);

        for (int i = 1; i < numberOfEpisodes(); i++) {
            Episode next = episodeAt(positionAtTime(i));

            if (EpisodeComparator.compareOnlyByAirdate(first, next) == 0) {
                result.add(next);
            } else {
                break;
            }
        }

        return result;
    }

    private int positionAtTime(int position) {
        return mSpecification.sortMode() == SortMode.OLDEST_FIRST ?
                position :
                numberOfEpisodes() - 1 - position;
    }

    @Override
    protected Specification<Episode> airDateSpecification() {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.airDate().after(DatesAndTimes.now());
            }
        };
    }
}
