package mobi.myseries.gui.library;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.application.update.BaseUpdateListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.EpisodesToCountSpecification;
import mobi.myseries.gui.shared.SeenEpisodeSpecification;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MySeriesAdapter extends BaseAdapter implements Publisher<MySeriesAdapter.Listener> {
    private ArrayList<Series> mItems;
    private AsyncTask<Void, Void, Void> mLoadingTask;

    public MySeriesAdapter() {
        mItems = new ArrayList<Series>();

        reloadData();

        App.seriesFollowingService().register(mSeriesFollowingListener);
        App.updateSeriesService().register(mUpdateListener);
        App.backupService().register(mBackupListener);
        App.markingService().register(mMarkingListener);
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        if(position < mItems.size())
            return mItems.get(position);

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(App.context(), R.layout.myseries_item, null);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Series series = mItems.get(position);

        setUpView(viewHolder, series);

        return view;
    }

    private void setUpView(ViewHolder viewHolder, final Series series) {
        String posterFilePath = App.imageService().getPosterOf(series);
        if(posterFilePath == null) {
            UniversalImageLoader.loader().displayImage(UniversalImageLoader.drawableURI(R.drawable.generic_poster), 
                    viewHolder.mPoster, 
                    UniversalImageLoader.defaultDisplayBuilder()
                    .build());
        } else {
            UniversalImageLoader.loader().displayImage(UniversalImageLoader.fileURI(posterFilePath), 
                    viewHolder.mPoster, 
                    UniversalImageLoader.defaultDisplayBuilder()
                    .showImageOnFail(R.drawable.generic_poster).build());
        }

        String name = series.name();
        viewHolder.mName.setText(name);

        boolean countSpecialEpisodes = App.preferences().forMySeries().countSpecialEpisodes();
        boolean countUnairedEpisodes = App.preferences().forMySeries().countUnairedEpisodes();
        Specification<Episode> spec1 = new EpisodesToCountSpecification(countSpecialEpisodes, countUnairedEpisodes);
        Specification<Episode> spec2 = new SeenEpisodeSpecification();

        String seenEpisodes = String.valueOf(series.numberOfEpisodes(spec1.and(spec2)));
        viewHolder.mWatchedEpisodes.setText(seenEpisodes);

        String allEpisodes = "/" + series.numberOfEpisodes(spec1);
        viewHolder.mAllEpisodes.setText(allEpisodes);

        viewHolder.watchedEpisodesBar.updateWith(series.episodesBy(spec1));

        viewHolder.moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyOnItemContextRequest(series.id());
            }
        });
    }

    public void reloadData() {
        if(mIsLoading) mLoadingTask.cancel(true);
        this.mLoadingTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                mIsLoading = true;
                notifyStartLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {
                if(!isCancelled())
                     setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if(isCancelled())
                    return;
                mIsLoading = false;
                notifyFinishLoading();
                notifyDataSetChanged();
            }
        }.execute();
    }

    private void setUpData() {
        mItems = new ArrayList<Series>();
        Map<Series,Boolean> filterOptions = App.preferences().forMySeries().seriesToShow();

        for (Entry<Series,Boolean> option : filterOptions.entrySet()) {
            if (option.getValue()) {
                mItems.add(option.getKey());
            }
        }

        int sortMode = App.preferences().forMySeries().sortMode();

        Collections.sort(mItems, SeriesComparator.bySortMode(sortMode));
    }

    /* ViewHolder */

    private static class ViewHolder {
        private final ImageView mPoster;
        private final TextView mName;
        private final TextView mWatchedEpisodes;
        private final TextView mAllEpisodes;
        private final SeenEpisodesBar watchedEpisodesBar;
        private final ImageButton moreButton;

        private ViewHolder(View view) {
            mPoster = (ImageView) view.findViewById(R.id.poster);
            mName = (TextView) view.findViewById(R.id.name);
            mWatchedEpisodes = (TextView) view.findViewById(R.id.seenEpisodes);
            mAllEpisodes = (TextView) view.findViewById(R.id.allEpisodes);
            watchedEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);
            moreButton = (ImageButton) view.findViewById(R.id.moreButton);
            view.setTag(this);
        }
    }

    /* MarkingListener */

    private final MarkingListener mMarkingListener = new MarkingListener() {
        @Override
        public void onMarked(Episode markedEpisode) {
            notifyDataSetChanged();
        }

        @Override
        public void onMarked(Season markedSeason) {
            notifyDataSetChanged();
        }

        @Override
        public void onMarked(Series markedSeries) {
            notifyDataSetChanged();
        }
    };

    /* SeriesFollowingListener */

    private final SeriesFollowingListener mSeriesFollowingListener = new BaseSeriesFollowingListener() {
        @Override
        public void onSuccessToFollow(Series followedSeries) {
            reloadData();
        }

        @Override
        public void onSuccessToUnfollow(Series unfollowedSeries) {
            reloadData();
        }

        @Override
        public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries) {
            reloadData();
        }
    };

    /* UpdateListener */

    private final UpdateListener mUpdateListener = new BaseUpdateListener() {
        @Override
        public void onUpdateFinish() {
            reloadData();
        }
    };

    /* BackupListener */

    private final BackupListener mBackupListener = new BackupListener() {
        @Override
        public void onBackupSucess() { }

        @Override
        public void onRestoreSucess() {
            reloadData();
        }

        @Override
        public void onRestoreFailure(BackupMode mode, Exception e) { }

        @Override
        public void onStart() { }

        @Override
        public void onBackupFailure(BackupMode mode, Exception e) { }

        @Override
        public void onBackupCompleted(BackupMode mode) { }

        @Override
        public void onBackupRunning(BackupMode mode) { }

        @Override
        public void onRestoreRunning(BackupMode mode) { }

        @Override
        public void onRestoreCompleted(BackupMode mode) {
            reloadData();
        }
    };

    /* MySeriesAdapter.Listener */

    public static interface Listener {
        public void onStartLoading();
        public void onFinishLoading();
        public void onItemContextRequest(int seriesId);
    }

    private boolean mIsLoading;
    private final ListenerSet<MySeriesAdapter.Listener> mListeners = new ListenerSet<MySeriesAdapter.Listener>();

    public boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public boolean register(MySeriesAdapter.Listener listener) {
        return mListeners.register(listener);
    }

    @Override
    public boolean deregister(MySeriesAdapter.Listener listener) {
        return mListeners.deregister(listener);
    }

    private void notifyStartLoading() {
        for (MySeriesAdapter.Listener listener : mListeners) {
            listener.onStartLoading();
        }
    }

    private void notifyFinishLoading() {
        for (MySeriesAdapter.Listener listener : mListeners) {
            listener.onFinishLoading();
        }
    }

    private void notifyOnItemContextRequest(int seriesId) {
        for (MySeriesAdapter.Listener listener : mListeners) {
            listener.onItemContextRequest(seriesId);
        }
    }
}
