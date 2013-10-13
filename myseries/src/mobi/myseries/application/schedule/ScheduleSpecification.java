package mobi.myseries.application.schedule;

import java.util.Map;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Validate;
import android.util.SparseBooleanArray;

public class ScheduleSpecification extends AbstractSpecification<Episode> {
    private boolean mIsSatisfiedBySpecialEpisodes;
    private boolean mIsSatisfiedByWatchedEpisodes;
    private SparseBooleanArray mSeriesToInclude;
    private int mSortMode;

    public ScheduleSpecification() {
        mIsSatisfiedBySpecialEpisodes = false;
        mIsSatisfiedByWatchedEpisodes = false;
        mSeriesToInclude = new SparseBooleanArray();
        mSortMode = SortMode.OLDEST_FIRST;
    }

    public ScheduleSpecification includingSpecialEpisodes(boolean including) {
        mIsSatisfiedBySpecialEpisodes = including;

        return this;
    }

    public ScheduleSpecification includingWatchedEpisodes(boolean including) {
        mIsSatisfiedByWatchedEpisodes = including;

        return this;
    }

    public ScheduleSpecification specifyInclusionOfSeries(Series series, boolean including) {
        Validate.isNonNull(series, "series");

        mSeriesToInclude.put(series.id(), including);

        return this;
    }

    public ScheduleSpecification includingAllSeries(Map<Series, Boolean> inclusions) {
        Validate.isNonNull(inclusions, "inclusions");

        for (Series series : inclusions.keySet()) {
            mSeriesToInclude.put(series.id(), inclusions.get(series));
        }

        return this;
    }

    public ScheduleSpecification specifySortMode(int sortMode) {
        mSortMode = sortMode;

        return this;
    }

    public boolean isSatisfiedBySpecialEpisodes() {
        return mIsSatisfiedBySpecialEpisodes;
    }

    public boolean isSatisfiedByWatchedEpisodes() {
        return mIsSatisfiedByWatchedEpisodes;
    }

    public boolean isSatisfiedByEpisodesOfSeries(int seriesId) {
        return mSeriesToInclude.get(seriesId);
    }

    public int sortMode() {
        return mSortMode;
    }

    @Override
    public boolean isSatisfiedBy(Episode episode) {
        return (episode != null) &&
                (isSatisfiedBySpecialEpisodes() || episode.isNotSpecial()) &&
                (isSatisfiedByWatchedEpisodes() || episode.unwatched()) &&
                (isSatisfiedByEpisodesOfSeries(episode.seriesId()));
    }
}
