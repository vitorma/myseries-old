package mobi.myseries.application.schedule;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Validate;
import android.util.SparseBooleanArray;

public class ScheduleSpecification extends AbstractSpecification<Episode> {
    private boolean mIsSatisfiedBySpecialEpisodes;
    private boolean mIsSatisfiedByWatchedEpisodes;
    private SparseBooleanArray mSeriesToExclude;
    private int mSortMode;

    public ScheduleSpecification() {
        mIsSatisfiedBySpecialEpisodes = false;
        mIsSatisfiedByWatchedEpisodes = false;
        mSeriesToExclude = new SparseBooleanArray();
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

    public ScheduleSpecification excludingAllSeries(int[] seriesToHide) {
        Validate.isNonNull(seriesToHide, "seriesToHide");

        for (int seriesId : seriesToHide) {
            mSeriesToExclude.put(seriesId, true);
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
        return !mSeriesToExclude.get(seriesId);
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
