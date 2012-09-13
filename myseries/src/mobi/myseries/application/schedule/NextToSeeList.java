package mobi.myseries.application.schedule;

import mobi.myseries.application.FollowSeriesService;
import mobi.myseries.application.UpdateSeriesService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.domain.repository.SeriesRepository;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.HasDate;
import mobi.myseries.shared.Specification;

public class NextToSeeList extends ScheduleList implements SeriesListener {

    private NextToSeeList(ScheduleParameters parameters, SeriesRepository repository, FollowSeriesService following, UpdateSeriesService update) {
        super(parameters, repository, following, update);
    }

    @Override
    protected void load() {
        for (Series s : this.repository().getAll()) {
            Episode nextToSee = s.nextEpisodeToSee(this.parameters().includesSpecialEpisodes());

            if (nextToSee != null) {
                this.add(nextToSee);
            }

            s.register(this);
        }
    }

    private Episode nextToSeeOf(Series series) {
        Specification<Episode> specification = seriesIdSpecification(series.id());

        for (HasDate element : this.elements()) {
            if (!this.isEpisode(element)) {
                continue;
            }

            Episode episode = (Episode) element;

            if (specification.isSatisfiedBy(episode)) {
                return episode;
            }
        }

        return null;
    }

    //Specification-----------------------------------------------------------------------------------------------------

    private static Specification<Episode> seriesIdSpecification(final int seriesId) {
        return new AbstractSpecification<Episode>() {
            @Override
            public boolean isSatisfiedBy(Episode e) {
                return e.seriesId() == seriesId;
            }
        };
    }

    //SeriesListener----------------------------------------------------------------------------------------------------

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        Episode oldNextToSee = this.nextToSeeOf(series);

        if (oldNextToSee != null) {
            this.remove(oldNextToSee);
        }

        Episode newNextToSee = series.nextEpisodeToSee(this.parameters().includesSpecialEpisodes());

        if (newNextToSee != null) {
            this.add(newNextToSee);
        }

        this.notifyListeners();
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {}

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {}

    @Override
    public void onMerge(Series series) {}

    //SeriesFollowingListener-------------------------------------------------------------------------------------------

    @Override
    public void onFollowing(Series series) {
        Episode nextToSee = series.nextEpisodeToSee(this.parameters().includesSpecialEpisodes());

        if (nextToSee != null) {
            this.add(nextToSee);
        }

        this.notifyListeners();
    }

    @Override
    public void onStopFollowing(Series series) {
        Episode nextToSee = this.nextToSeeOf(series);

        if (nextToSee != null) {
            this.remove(nextToSee);
        }

        this.notifyListeners();
    }

    //Builder-----------------------------------------------------------------------------------------------------------

    public static class Builder extends ScheduleList.Builder {
        public Builder(SeriesRepository repository, FollowSeriesService following, UpdateSeriesService update) {
            super(repository, following, update);
        }

        public ScheduleList build() {
            return new NextToSeeList(parameters, repository, following, update);
        }
    }
}
