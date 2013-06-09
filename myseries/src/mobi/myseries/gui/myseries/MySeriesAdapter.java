package mobi.myseries.gui.myseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Strings;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySeriesAdapter extends BaseAdapter implements Publisher<MySeriesAdapter.Listener> {
    private List<Series> items;

    public MySeriesAdapter() {
        App.followSeriesService().register(this.seriesFollowingListener);
        App.updateSeriesService().register(this.updateListener);
        App.backupService().register(this.backupListener);

        this.items = new ArrayList<Series>();

        this.reload();
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
        View view = Objects.nullSafe(
            convertView,
            View.inflate(App.context(), R.layout.myseries_item, null));

        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Series series = this.items.get(position);

        this.setUpView(viewHolder, series);

        return view;
    }

    private void setUpView(ViewHolder viewHolder, Series series) {
        Bitmap poster = App.imageService().getSmallPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        viewHolder.poster.setImageBitmap(Objects.nullSafe(poster, genericPoster));

        String name = series.name();
        viewHolder.name.setText(name);

        String status = LocalText.of(series.status(), "");
        viewHolder.status.setText(status);

        String airDay = DatesAndTimes.toString(series.airDay(), Locale.getDefault(), "");
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(App.context()), "");
        String network = series.network();
        String airInfo = Strings.concat(airDay, airtime, ", ");
        airInfo = Strings.concat(airInfo, network, " - ");
        viewHolder.airInfo.setText(airInfo);

        String seenEpisodes = series.numberOfSeenEpisodes() + "/" + series.numberOfEpisodes();
        viewHolder.seenEpisodes.setText(seenEpisodes);

        viewHolder.seenEpisodesBar.updateWithEpisodesOf(series);
    }

    public void reload() {
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
        Map<Series,Boolean> filterOptions = App.preferences().forMySeries().seriesToShow();
        this.items = new LinkedList<Series>();

        for (Entry<Series,Boolean> option : filterOptions.entrySet()) {
            if (option.getValue()) {
                this.items.add(option.getKey());
            }
        }

        for (Series s : this.items) {
            s.register(this.seriesListener);
        }

        Collections.sort(this.items, new SeriesComparator());
    }

    /* ViewHolder */

    private static class ViewHolder {
        private final ImageView poster;
        private final TextView name;
        private final TextView status;
        private final TextView airInfo;
        private final TextView seenEpisodes;
        private final SeenEpisodesBar seenEpisodesBar;

        private ViewHolder(View view) {
            this.poster = (ImageView) view.findViewById(R.id.poster);
            this.name = (TextView) view.findViewById(R.id.name);
            this.status = (TextView) view.findViewById(R.id.status);
            this.airInfo = (TextView) view.findViewById(R.id.airInfo);
            this.seenEpisodes = (TextView) view.findViewById(R.id.seenEpisodes);
            this.seenEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);

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
        public void onChangeNextEpisodeToSee(Series series) {}

        @Override
        public void onChangeNextNonSpecialEpisodeToSee(Series series) {
            MySeriesAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onMarkAsSeen(Series series) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onMarkAsNotSeen(Series series) {
            // TODO Auto-generated method stub

        }
    };

    /* SeriesFollowingListener */

    private final SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {
        @Override
        public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
            for (Series s : allUnfollowedSeries) {
                s.deregister(MySeriesAdapter.this.seriesListener);
            }

            MySeriesAdapter.this.reload();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            unfollowedSeries.deregister(MySeriesAdapter.this.seriesListener);

            MySeriesAdapter.this.reload();
        }

        @Override
        public void onFollowingStart(Series seriesToFollow) {}

        @Override
        public void onFollowingFailure(Series series, Exception e) {}

        @Override
        public void onFollowing(Series followedSeries) {
            followedSeries.register(MySeriesAdapter.this.seriesListener);

            MySeriesAdapter.this.reload();
        }
    };

    /* UpdateFinishListener */

    private final UpdateFinishListener updateListener = new UpdateFinishListener() {
        @Override
        public void onUpdateFinish() {
            MySeriesAdapter.this.reload();
        }
    };

    /* BackupListener */

    private final BackupListener backupListener = new BackupListener() {
        @Override
        public void onBackupSucess() {}

        @Override
        public void onBackupFailure(Exception e) {}

        @Override
        public void onRestoreSucess() {
            MySeriesAdapter.this.reload();
        }

        @Override
        public void onRestoreFailure(Exception e) {}

        @Override
        public void onStart() {}
    };

    /* MySeriesAdapter.Listener */

    public static interface Listener {
        public void onStartLoading();
        public void onFinishLoading();
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
}
