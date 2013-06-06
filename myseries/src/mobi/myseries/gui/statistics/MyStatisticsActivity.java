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
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO(Reul): cleanups
public class MyStatisticsActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MyStatisticsActivity.class);
    }

    private TextView episodesWatched;
    private ProgressBar episodesWatchedProgressBar;
    private TextView numberOfEpisodes;
    private TextView numberOfSeasons;
    private TextView numberOfSeries;
    private TextView seasonsWatched;
    private ProgressBar seasonsWatchedProgressBar;
    private TextView seriesWatched;
    private ProgressBar seriesWatchedProgressBar;
    private TextView watchedEpisodesRuntime;

    @Override
    protected void init() {
        this.setupViews();

        this.setupFollowingSeriesListener();
        this.setupUpdateFinishedListener();
        this.setupBackupListener();

        this.update();
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected int layoutResource() {
        return R.layout.statistics;
    }

    private void setupBackupListener() {
        App.backupService().register(new BackupListener() {

            @Override
            public void onBackupFailure(Exception e) {
                // I'm not interested }
            }

            @Override
            public void onBackupSucess() {
                // I'm not interested
            }

            @Override
            public void onRestoreFailure(Exception e) {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onRestoreSucess() {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onStart() {
                // I'm not interested
            }
        });
    }

    private void setupFollowingSeriesListener() {
        App.followSeriesService().register(new SeriesFollowingListener() {
            @Override
            public void onFollowing(Series followedSeries) {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onFollowingFailure(Series series, Exception e) {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onFollowingStart(Series seriesToFollow) {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onStopFollowing(Series unfollowedSeries) {
                MyStatisticsActivity.this.update();
            }

            @Override
            public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
                MyStatisticsActivity.this.update();
            }
        });
    }

    private void setupUpdateFinishedListener() {
        App.updateSeriesService().register(new UpdateFinishListener() {
            @Override
            public void onUpdateFinish() {
                MyStatisticsActivity.this.update();
            }
        });
    }

    private void setupViews() {
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
    }

    @Override
    protected CharSequence title() {
        return this.getString(R.string.my_statistics);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getString(R.string.nav_statistics);
    }

    private void update() {
        int nSeries = 0;
        int watchedSeries = 0;
        int nSeasons = 0;
        int watchedSeasons = 0;
        int nEpisodes = 0;
        int watchedEpisodes = 0;
        int watchedRuntime = 0;

        final Collection<Series> series = App.seriesProvider().followedSeries();

        nSeries = series.size();

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
                // Ignore missing runtimes
            }

            nEpisodes += s.numberOfEpisodes();
            watchedEpisodes += s.numberOfSeenEpisodes();
        }

        this.numberOfSeries.setText(String.format("%d", nSeries));
        this.numberOfSeasons.setText(String
            .format("%d", nSeasons));
        this.numberOfEpisodes.setText(String
            .format("%d", nEpisodes));

        this.seriesWatched.setText(String.format(
            this.getString(R.string.series_watched),
            watchedSeries));
        this.seasonsWatched.setText(String.format(
            this.getString(R.string.seasons_watched),
            watchedSeasons));
        this.episodesWatched.setText(String.format(
            this.getString(R.string.episodes_watched),
            watchedEpisodes));

        this.seriesWatchedProgressBar.setMax(nSeries);
        this.seriesWatchedProgressBar.setProgress(watchedSeries);

        this.seasonsWatchedProgressBar.setMax(nSeasons);
        this.seasonsWatchedProgressBar
            .setProgress(watchedSeasons);

        this.episodesWatchedProgressBar.setMax(nEpisodes);
        this.episodesWatchedProgressBar
            .setProgress(watchedEpisodes);

        int minutes = (watchedRuntime % 60);
        int hours = ((watchedRuntime / 60) % 24);
        int days = ((watchedRuntime / 3600));

        this.watchedEpisodesRuntime.setText(String.format(
            this.getString(R.string.total_runtime_format), days, hours,
            minutes));
    }
}
