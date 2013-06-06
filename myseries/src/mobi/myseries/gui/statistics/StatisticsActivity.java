package mobi.myseries.gui.statistics;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.domain.model.SeasonSet;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO(Reul): cleanups
public class StatisticsActivity extends BaseActivity {

    private TextView numberOfSeries;
    private TextView numberOfSeasons;
    private TextView numberOfEpisodes;
    private TextView seriesWatched;
    private TextView seasonsWatched;
    private TextView episodesWatched;
    private ProgressBar seasonsWatchedProgressBar;
    private ProgressBar seriesWatchedProgressBar;
    private ProgressBar episodesWatchedProgressBar;
    private TextView watchedEpisodesRuntime;

    private final SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {
        @Override
        public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
            StatisticsActivity.this.update();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            StatisticsActivity.this.update();
        }

        @Override
        public void onFollowingStart(Series seriesToFollow) {
            StatisticsActivity.this.update();
        }

        @Override
        public void onFollowingFailure(Series series, Exception e) {
            StatisticsActivity.this.update();
        }

        @Override
        public void onFollowing(Series followedSeries) {
            StatisticsActivity.this.update();
        }
    };

    private final UpdateFinishListener updateListener = new UpdateFinishListener() {
        @Override
        public void onUpdateFinish() {
            StatisticsActivity.this.update();
        }
    };

    private final BackupListener backupListener = new BackupListener() {

        @Override
        public void onStart() {
            // I'm not interested
        }

        @Override
        public void onRestoreSucess() {
            StatisticsActivity.this.update();
        }

        @Override
        public void onRestoreFailure(Exception e) {
            StatisticsActivity.this.update();
        }

        @Override
        public void onBackupSucess() {
            // I'm not interested
        }

        @Override
        public void onBackupFailure(Exception e) {
            // I'm not interested }
        }
    };

    @Override
    protected void init() {
        this.numberOfSeries = (TextView) this.findViewById(R.id.numberOfSeries);
        this.numberOfSeasons = (TextView) this.findViewById(R.id.numberOfSeasons);
        this.numberOfEpisodes = (TextView) this.findViewById(R.id.numberOfEpisodes);

        this.seriesWatched = (TextView) this.findViewById(R.id.seriesWatched);
        this.seasonsWatched = (TextView) this.findViewById(R.id.seasonsWatched);
        this.episodesWatched = (TextView) this.findViewById(R.id.episodesWatched);

        this.seriesWatchedProgressBar = (ProgressBar) this
            .findViewById(R.id.seriesWatchedProgressBar);
        this.seasonsWatchedProgressBar = (ProgressBar) this
            .findViewById(R.id.seasonsWatchedProgressBar);
        this.episodesWatchedProgressBar = (ProgressBar) this
            .findViewById(R.id.episodesWatchedProgressBar);

        this.watchedEpisodesRuntime = (TextView) this.findViewById(R.id.watchedRuntime);

        App.followSeriesService().register(this.seriesFollowingListener);
        App.updateSeriesService().register(this.updateListener);
        App.backupService().register(this.backupListener);

        this.update();
    }

    private void update() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final Collection<Series> series = App.seriesProvider().followedSeries();

                final int nSeries = series.size();
                int watchedSeries = 0;

                int nSeasons = 0;
                int watchedSeasons = 0;

                int nEpisodes = 0;
                int watchedEpisodes = 0;

                int watchedRuntime = 0;

                for (Series s : series) {
                    if (s.numberOfEpisodes() == s.numberOfSeenEpisodes()) {
                        ++watchedSeries;
                    }

                    nSeasons += s.seasons().numberOfSeasons();

                    final SeasonSet seasons = s.seasons();
                    for (int i = 0; i < seasons.numberOfSeasons(); ++i) {
                        if (seasons.seasonAt(i).numberOfEpisodes() == seasons.seasonAt(i)
                            .numberOfSeenEpisodes()) {
                            ++watchedSeasons;
                        }
                    }

                    int currentSeriesSeenEpisodes = s.numberOfSeenEpisodes();
                    s.numberOfSeenEpisodes();

                    try {
                        watchedRuntime += (Integer.parseInt(s.runtime()) * currentSeriesSeenEpisodes);
                    } catch (Exception e) {
                        // Parse or fuck off
                    }

                    nEpisodes += s.numberOfEpisodes();
                    watchedEpisodes += s.numberOfSeenEpisodes();
                }

                StatisticsActivity.this.numberOfSeries.setText(String.format("%d", nSeries));
                StatisticsActivity.this.numberOfSeasons.setText(String.format("%d", nSeasons));
                StatisticsActivity.this.numberOfEpisodes.setText(String.format("%d", nEpisodes));

                StatisticsActivity.this.seriesWatched.setText(String.format(
                    StatisticsActivity.this.getString(R.string.series_watched),
                    watchedSeries));
                StatisticsActivity.this.seasonsWatched.setText(String.format(
                    StatisticsActivity.this.getString(R.string.seasons_watched),
                    watchedSeasons));
                StatisticsActivity.this.episodesWatched.setText(String.format(
                    StatisticsActivity.this.getString(R.string.episodes_watched),
                    watchedEpisodes));

                StatisticsActivity.this.seriesWatchedProgressBar.setMax(nSeries);
                StatisticsActivity.this.seriesWatchedProgressBar.setProgress(watchedSeries);

                StatisticsActivity.this.seasonsWatchedProgressBar.setMax(nSeasons);
                StatisticsActivity.this.seasonsWatchedProgressBar.setProgress(watchedSeasons);

                StatisticsActivity.this.episodesWatchedProgressBar.setMax(nEpisodes);
                StatisticsActivity.this.episodesWatchedProgressBar.setProgress(watchedEpisodes);

                int minutes = (watchedRuntime % 60);
                int hours = ((watchedRuntime / 60) % 24);
                int days = ((watchedRuntime / 3600));

                StatisticsActivity.this.watchedEpisodesRuntime.setText(String.format(
                    StatisticsActivity.this.getString(R.string.total_runtime_format), days, hours,
                    minutes));

                return null;
            }
        }.execute();
    }

    @Override
    protected int layoutResource() {
        return R.layout.statistics;
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected CharSequence title() {
        return this.getString(R.string.statistics);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, StatisticsActivity.class);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getString(R.string.nav_statistics);
    }
}
