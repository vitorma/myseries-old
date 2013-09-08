package mobi.myseries.application.schedule;

import java.util.Map;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Validate;
import android.util.SparseBooleanArray;

public class ScheduleSpecification extends AbstractSpecification<Episode> {
    private boolean isSatisfiedBySpecialEpisodes;
    private boolean isSatisfiedBySeenEpisodes;
    private SparseBooleanArray seriesToInclude;
    private int sortMode;

    public ScheduleSpecification() {
        this.isSatisfiedBySpecialEpisodes = false;
        this.isSatisfiedBySeenEpisodes = false;
        this.seriesToInclude = new SparseBooleanArray();
        this.sortMode = SortMode.OLDEST_FIRST;
    }

    public ScheduleSpecification specifyInclusionOfSpecialEpisodes(boolean including) {
        this.isSatisfiedBySpecialEpisodes = including;

        return this;
    }

    public ScheduleSpecification specifyInclusionOfSeenEpisodes(boolean including) {
        this.isSatisfiedBySeenEpisodes = including;
        return this;
    }

    public ScheduleSpecification specifyInclusionOf(Series series, boolean including) {
        Validate.isNonNull(series, "series");

        this.seriesToInclude.put(series.id(), including);

        return this;
    }

    public ScheduleSpecification specifyInclusionOfAllSeries(Map<Series, Boolean> inclusions) {
        Validate.isNonNull(inclusions, "inclusions");

        for (Series series : inclusions.keySet()) {
            this.seriesToInclude.put(series.id(), inclusions.get(series));
        }

        return this;
    }

    public ScheduleSpecification specifySortMode(int sortMode) {
        this.sortMode = sortMode;
        return this;
    }

    public boolean isSatisfiedBySpecialEpisodes() {
        return this.isSatisfiedBySpecialEpisodes;
    }

    public boolean isSatisfiedBySeenEpisodes() {
        return this.isSatisfiedBySeenEpisodes;
    }

    public boolean isSatisfiedByEpisodesOfSeries(int seriesId) {
        return this.seriesToInclude.get(seriesId);
    }

    public int sortMode() {
        return this.sortMode;
    }

    @Override
    public boolean isSatisfiedBy(Episode episode) {
        return (episode != null) &&
               (this.isSatisfiedBySpecialEpisodes() || episode.isNotSpecial()) &&
               (this.isSatisfiedBySeenEpisodes() || episode.unwatched()) &&
               (this.isSatisfiedByEpisodesOfSeries(episode.seriesId()));
    }
}
