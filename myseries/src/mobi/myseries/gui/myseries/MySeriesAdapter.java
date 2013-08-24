package mobi.myseries.gui.myseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.shared.EpisodesToCountSpecification;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.SeenEpisodeSpecification;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Specification;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MySeriesAdapter extends BaseAdapter implements Publisher<MySeriesAdapter.Listener> {
    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterFrom(App.resources());

    private ArrayList<Series> items;

    public MySeriesAdapter() {
        this.items = new ArrayList<Series>();

        this.reloadData();

        App.followSeriesService().register(this.seriesFollowingListener);
        App.updateSeriesService().register(this.updateListener);
        App.backupService().register(this.backupListener);
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
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

        Series series = this.items.get(position);

        this.setUpView(viewHolder, series);

        return view;
    }

    private void setUpView(ViewHolder viewHolder, final Series series) {
        Bitmap poster = App.imageService().getPosterOf(series);
        viewHolder.poster.setImageBitmap(Objects.nullSafe(poster, GENERIC_POSTER));

        String name = series.name();
        viewHolder.name.setText(name);

        boolean countSpecialEpisodes = App.preferences().forMySeries().countSpecialEpisodes();
        boolean countUnairedEpisodes = App.preferences().forMySeries().countUnairedEpisodes();
        Specification<Episode> spec1 = new EpisodesToCountSpecification(countSpecialEpisodes, countUnairedEpisodes);
        Specification<Episode> spec2 = new SeenEpisodeSpecification();

        String seenEpisodes = String.valueOf(series.numberOfEpisodes(spec1.and(spec2)));
        viewHolder.seenEpisodes.setText(seenEpisodes);

        String allEpisodes = "/" + series.numberOfEpisodes(spec1);
        viewHolder.allEpisodes.setText(allEpisodes);

        viewHolder.seenEpisodesBar.updateWith(series.episodesBy(spec1));

        viewHolder.moreButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MySeriesAdapter.this.notifyOnItemContextRequest(series.id());
            }
        });
    }

    //XXX Carregar poster assincronamente

    public void reloadData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                MySeriesAdapter.this.isLoading = true;
                MySeriesAdapter.this.notifyStartLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {
                MySeriesAdapter.this.setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                MySeriesAdapter.this.isLoading = false;
                MySeriesAdapter.this.notifyFinishLoading();
                MySeriesAdapter.this.notifyDataSetChanged();
            }
        }.execute();
    }

    private void setUpData() {
        this.items = new ArrayList<Series>();
        Map<Series,Boolean> filterOptions = App.preferences().forMySeries().seriesToShow();

        for (Entry<Series,Boolean> option : filterOptions.entrySet()) {
            if (option.getValue()) {
                this.items.add(option.getKey());
            }
        }

        for (Series s : this.items) {
            s.register(this.seriesListener);
        }

        int sortMode = App.preferences().forMySeries().sortMode();

        Collections.sort(this.items, SeriesComparator.bySortMode(sortMode));
    }

    /* ViewHolder */

    private static class ViewHolder {
        private final ImageView poster;
        private final TextView name;
        private final TextView seenEpisodes;
        private final TextView allEpisodes;
        private final SeenEpisodesBar seenEpisodesBar;
        private final ImageButton moreButton;

        private ViewHolder(View view) {
            this.poster = (ImageView) view.findViewById(R.id.poster);
            this.name = (TextView) view.findViewById(R.id.name);
            this.seenEpisodes = (TextView) view.findViewById(R.id.seenEpisodes);
            this.allEpisodes = (TextView) view.findViewById(R.id.allEpisodes);
            this.seenEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);
            this.moreButton = (ImageButton) view.findViewById(R.id.moreButton);

            view.setTag(this);
        }
    }

    /* SeriesListener */

    private final SeriesListener seriesListener = new SeriesListener() {
        @Override
        public void onChangeNumberOfSeenEpisodes(Series series) {
            MySeriesAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onChangeNextEpisodeToSee(Series series) { }

        @Override
        public void onChangeNextNonSpecialEpisodeToSee(Series series) { }

        @Override
        public void onMarkAsSeen(Series series) { }

        @Override
        public void onMarkAsNotSeen(Series series) { }
    };

    /* SeriesFollowingListener */

    private final SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {
        @Override
        public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
            for (Series s : allUnfollowedSeries) {
                s.deregister(MySeriesAdapter.this.seriesListener);
            }

            MySeriesAdapter.this.reloadData();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            unfollowedSeries.deregister(MySeriesAdapter.this.seriesListener);

            MySeriesAdapter.this.reloadData();
        }

        @Override
        public void onFollowingStart(Series seriesToFollow) {}

        @Override
        public void onFollowingFailure(Series series, Exception e) {}

        @Override
        public void onFollowing(Series followedSeries) {
            followedSeries.register(MySeriesAdapter.this.seriesListener);

            MySeriesAdapter.this.reloadData();
        }
    };

    /* UpdateFinishListener */

    private final UpdateFinishListener updateListener = new UpdateFinishListener() {
        @Override
        public void onUpdateFinish() {
            MySeriesAdapter.this.reloadData();
        }
    };

    /* BackupListener */

    private final BackupListener backupListener = new BackupListener() {
        @Override
        public void onBackupSucess() { }

        @Override
        public void onRestoreSucess() {
            MySeriesAdapter.this.reloadData();
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
            MySeriesAdapter.this.reloadData();
        }
    };

    /* MySeriesAdapter.Listener */

    public static interface Listener {
        public void onStartLoading();
        public void onFinishLoading();
        public void onItemContextRequest(int seriesId);
    }

    private boolean isLoading;
    private final ListenerSet<MySeriesAdapter.Listener> listeners = new ListenerSet<MySeriesAdapter.Listener>();

    public boolean isLoading() {
        return this.isLoading;
    }

    @Override
    public boolean register(MySeriesAdapter.Listener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(MySeriesAdapter.Listener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyStartLoading() {
        for (MySeriesAdapter.Listener listener : this.listeners) {
            listener.onStartLoading();
        }
    }

    private void notifyFinishLoading() {
        for (MySeriesAdapter.Listener listener : this.listeners) {
            listener.onFinishLoading();
        }
    }

    private void notifyOnItemContextRequest(int seriesId) {
        for (MySeriesAdapter.Listener listener : this.listeners) {
            listener.onItemContextRequest(seriesId);
        }
    }
}
