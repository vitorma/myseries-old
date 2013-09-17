package mobi.myseries.gui.mystatistics;

import java.util.Collection;
import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.application.following.BaseSeriesFollowingListener;
import mobi.myseries.application.following.SeriesFollowingListener;
import mobi.myseries.application.preferences.MyStatisticsPreferences;
import mobi.myseries.application.update.BaseUpdateListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SeasonSet;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.EpisodeWatchMarkSpecification;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyStatisticsFragment extends Fragment {

    private BackupListener backupListener;
    private TextView episodesWatched;
    private ProgressBar episodesWatchedProgressBar;
    private SeriesFollowingListener followListener;
    private TextView numberOfEpisodes;
    private TextView numberOfSeasons;
    private TextView numberOfSeries;
    private TextView seasonsWatched;
    private ProgressBar seasonsWatchedProgressBar;
    private TextView seriesWatched;
    private ProgressBar seriesWatchedProgressBar;
    private UpdateListener updateListener;
    private TextView watchedRuntime;
    private ProgressBar timeOfWatchedEpisodesProgressBar;
    private OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private TextView hours;
    private TextView minutes;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        this.prepareViews();

        this.setupFollowingSeriesListener();
        this.setupUpdateListener();
        this.setupBackupListener();
        this.setupPreferencesListener();

        this.update();
    }

    private void setupPreferencesListener() {
        this.onSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                MyStatisticsFragment.this.update();
            }
        };

        App.preferences().forActivities().register(this.onSharedPreferenceChangeListener);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        this.setRetainInstance(true);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mystatistics_fragment, container, false);
    }

    private void prepareViews() {
        this.numberOfSeries = (TextView) this.getActivity().findViewById(R.id.numberOfSeries);
        this.numberOfSeasons = (TextView) this.getActivity().findViewById(R.id.numberOfSeasons);
        this.numberOfEpisodes = (TextView) this.getActivity().findViewById(R.id.numberOfEpisodes);

        this.seriesWatched = (TextView) this.getActivity().findViewById(R.id.seriesWatched);
        this.seasonsWatched = (TextView) this.getActivity().findViewById(R.id.seasonsWatched);
        this.episodesWatched = (TextView) this.getActivity().findViewById(R.id.episodesWatched);

        this.seriesWatchedProgressBar = (ProgressBar) this.getActivity()
            .findViewById(R.id.seriesWatchedProgressBar);
        this.seasonsWatchedProgressBar = (ProgressBar) this.getActivity()
            .findViewById(R.id.seasonsWatchedProgressBar);
        this.episodesWatchedProgressBar = (ProgressBar) this.getActivity()
            .findViewById(R.id.episodesWatchedProgressBar);
        this.timeOfWatchedEpisodesProgressBar = (ProgressBar) this.getActivity()
            .findViewById(R.id.timeOfEpisodesWatchedProgressBar);

        this.watchedRuntime = (TextView) this.getActivity().findViewById(R.id.watchedRuntime);

        this.hours = (TextView) this.getActivity().findViewById(R.id.hours);
        this.minutes = (TextView) this.getActivity().findViewById(R.id.minutes);
    }

    private void setupBackupListener() {
        this.backupListener = new BackupListener() {
            @Override
            public void onBackupFailure(BackupMode mode, Exception e) {
                // I'm not interested }
            }

            @Override
            public void onBackupSucess() {
                // I'm not interested
            }

            @Override
            public void onRestoreFailure(BackupMode mode, Exception e) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onRestoreSucess() {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onStart() {
                // I'm not interested
            }

            @Override
            public void onBackupCompleted(BackupMode mode) {
                // I'm not interested
            }

            @Override
            public void onBackupRunning(BackupMode mode) {
                // I'm not interested
            }

            @Override
            public void onRestoreRunning(BackupMode mode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onRestoreCompleted(BackupMode mode) {
                MyStatisticsFragment.this.update();
            }
        };

        App.backupService().register(this.backupListener);
    }

    private void setupFollowingSeriesListener() {
        this.followListener = new BaseSeriesFollowingListener() {
            @Override
            public void onSuccessToFollow(Series followedSeries) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onSuccessToUnfollow(Series unfollowedSeries) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onSuccessToUnfollowAll(Collection<Series> allUnfollowedSeries) {
                MyStatisticsFragment.this.update();
            }
        };

        App.seriesFollowingService().register(this.followListener);
    }

    private void setupUpdateListener() {
        this.updateListener = new BaseUpdateListener() {
            @Override
            public void onUpdateFinish() {
                MyStatisticsFragment.this.update();
            }
        };

        App.updateSeriesService().register(this.updateListener);
    }

    private void update() {
        MyStatisticsPreferences preferences = App.preferences().forMyStatistics();

        int nSeries = 0;
        int watchedSeries = 0;
        int nSeasons = 0;
        int watchedSeasons = 0;
        int nEpisodes = 0;
        int watchedEpisodes = 0;
        int watchedRuntime = 0;
        int totalRuntime = 0;

        final Date now = new Date(System.currentTimeMillis());

        final Collection<Series> series = App.seriesFollowingService().getAllFollowedSeries();

        for (Series s : series) {
            int currentSeriesEpisodes = 0;
            int currentSeriesWatchedEpisodes = 0;

            if (!preferences.countSeries(s.id())) {
                continue;
            }

            ++nSeries;

            final SeasonSet seasons = s.seasons();

            for (int i = 0; i < seasons.numberOfSeasons(); ++i) {
                if (seasons.seasonAt(i).isSpecial() && !preferences.countSpecialEpisodes()) {
                    continue;
                }

                if (seasons.seasonAt(i).numberOfEpisodes() == seasons.seasonAt(i).numberOfEpisodes(new EpisodeWatchMarkSpecification(true))) {
                    ++watchedSeasons;
                }

                ++nSeasons;

                int currentSeasonSeenEpisodes = 0;
                int currentSeasonEpisodes = 0;

                for (Episode e : seasons.seasonAt(i).episodes()) {
                    // Assume episodes with null airDate are in the past
                    if (!preferences.countUnairedEpisodes()
                        && (e.airDate() != null && now.before(e.airDate()))) {
                        continue;
                    }

                    if (e.watched()) {
                        ++watchedEpisodes;
                        ++currentSeasonSeenEpisodes;
                        ++currentSeriesWatchedEpisodes;
                    }

                    ++nEpisodes;
                    ++currentSeasonEpisodes;
                    ++currentSeriesEpisodes;

                }

                try {
                    watchedRuntime += Integer.parseInt(s.runtime()) * currentSeasonSeenEpisodes;
                    totalRuntime += Integer.parseInt(s.runtime()) * currentSeasonEpisodes;
                } catch (Exception e) {
                    // Ignore missing runtimes
                }
            }

            if (currentSeriesEpisodes == currentSeriesWatchedEpisodes) {
                ++watchedSeries;
            }
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
        int hours = ((watchedRuntime / 60));

        this.watchedRuntime.setText(String.format(
            this.getString(R.string.watched_runtime_format), hours,
            minutes));

        minutes = (totalRuntime % 60);
        hours = ((totalRuntime / 60));

        this.hours.setText(String.valueOf(hours));
        this.minutes.setText(String.valueOf(minutes));

        this.timeOfWatchedEpisodesProgressBar.setMax(totalRuntime);
        this.timeOfWatchedEpisodesProgressBar.setProgress(watchedRuntime);
    }
}
