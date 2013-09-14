package mobi.myseries.application.schedule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingService;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.application.marking.MarkingService;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;

public abstract class ScheduleMode implements Publisher<ScheduleListener> {
    public static final int TO_WATCH = 0;
    public static final int AIRED = 1;
    public static final int UNAIRED = 2;

    protected ScheduleSpecification mSpecification;
    protected SeriesFollowingService mFollowing;
    private ListenerSet<ScheduleListener> mListeners;
    protected List<Episode> mEpisodes;

    protected ScheduleMode(
            ScheduleSpecification specification,
            SeriesFollowingService following,
            UpdateService update,
            MarkingService marking) {
        Validate.isNonNull(specification, "specification");
        Validate.isNonNull(following, "following");
        Validate.isNonNull(update, "update");
        Validate.isNonNull(marking, "marking");

        mSpecification = specification;
        mEpisodes = new ArrayList<Episode>();
        mListeners = new ListenerSet<ScheduleListener>();
        mFollowing = following;

        mFollowing.register(mSeriesFollowingListener);
        update.register(mUpdateFinishListener);
        marking.register(mMarkingListener);

        loadEpisodes();
        sortEpisodes();
    }

    /* Interface */

    public int numberOfEpisodes() {
        return mEpisodes.size();
    }

    public Episode episodeAt(int position) {
        return mEpisodes.get(position);
    }

    public List<Episode> episodes() {
        return new ArrayList<Episode>(mEpisodes);
    }

    /* Load and sort */

    protected abstract void loadEpisodes();

    protected void sortEpisodes() {
        Collections.sort(mEpisodes, comparator(mSpecification.sortMode()));
    }

    private Comparator<Episode> comparator(int sortMode) {
        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                return EpisodeComparator.comparingByOldestFirst();
            case SortMode.NEWEST_FIRST:
                return EpisodeComparator.comparingByNewestFirst();
            default:
                return null;
        }
    }

    /* Publisher<ScheduleListener> */

    @Override
    public boolean register(ScheduleListener listener) {
        return mListeners.register(listener);
    }

    @Override
    public boolean deregister(ScheduleListener listener) {
        return mListeners.deregister(listener);
    }

    protected void notifyOnScheduleStateChanged() {
        for (ScheduleListener listener : mListeners) {
            listener.onScheduleStateChanged();
        }
    }

    protected void notifyOnScheduleStructureChanged() {
        for (ScheduleListener listener : mListeners) {
            listener.onScheduleStructureChanged();
        }
    }

    /* SeriesFollowingListener */

    private SeriesFollowingListener mSeriesFollowingListener = new BaseSeriesFollowingListener() {
        @Override
        public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries) {
            notifyOnScheduleStructureChanged();
        }

        @Override
        public void onSuccessToUnfollow(Series unfollowedSeries) {
            notifyOnScheduleStructureChanged();
        }

        @Override
        public void onSuccessToFollow(Series followedSeries) {
            notifyOnScheduleStructureChanged();
        }
    };

    /* UpdateFinishListener */

    private UpdateFinishListener mUpdateFinishListener = new UpdateFinishListener() {
        @Override
        public void onUpdateFinish() {
            notifyOnScheduleStructureChanged();
        }
    };

    /* MarkingListener */

    private MarkingListener mMarkingListener = markingListener();

    protected abstract MarkingListener markingListener();
}