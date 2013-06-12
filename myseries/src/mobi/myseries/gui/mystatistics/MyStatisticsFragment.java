package mobi.myseries.gui.mystatistics;

import java.util.Collection;
import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.preferences.MyStatisticsPreferences;
import mobi.myseries.application.update.listener.UpdateFinishListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SeasonSet;
import mobi.myseries.domain.model.Series;
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
    private UpdateFinishListener updateListener;
    private TextView watchedRuntime;
    private ProgressBar timeOfWatchedEpisodesProgressBar;
    private OnSharedPreferenceChangeListener onSharedPreferenceChangeListener;
    private TextView totalRuntime;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        this.prepareViews();

        this.setupFollowingSeriesListener();
        this.setupUpdateFinishedListener();
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

        this.totalRuntime = (TextView) this.getActivity().findViewById(R.id.totalRuntime);
    }

    private void setupBackupListener() {
        this.backupListener = new BackupListener() {
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
        };

        App.backupService().register(this.backupListener);

    }

    private void setupFollowingSeriesListener() {

        this.followListener = new SeriesFollowingListener() {
            @Override
            public void onFollowing(Series followedSeries) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onFollowingFailure(Series series, Exception e) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onFollowingStart(Series seriesToFollow) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onStopFollowing(Series unfollowedSeries) {
                MyStatisticsFragment.this.update();
            }

            @Override
            public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
                MyStatisticsFragment.this.update();
            }
        };

        App.followSeriesService().register(this.followListener);

    }

    private void setupUpdateFinishedListener() {
        this.updateListener = new UpdateFinishListener() {
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

        final Collection<Series> series = App.seriesProvider().followedSeries();

        for (Series s : series) {
            int currentSeriesEpisodes = 0;
            int currentSeriesWatchedEpisodes = 0;

            if (!preferences.countSeries(s.id())) {
                continue;
            }

            ++nSeries;

            final SeasonSet seasons = s.seasons();

            for (int i = 0; i < seasons.numberOfSeasons(); ++i) {
                if (seasons.seasonAt(i).number() == 0 && !preferences.countSpecialEpisodes()) {
                    continue;
                }

                if (seasons.seasonAt(i).numberOfEpisodes() == seasons.seasonAt(i)
                    .numberOfSeenEpisodes()) {
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

                    if (e.wasSeen()) {
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
        int hours = ((watchedRuntime / 60) % 24);
        int days = ((watchedRuntime / 3600));

        this.watchedRuntime.setText(String.format(
            this.getString(R.string.watched_runtime_format), days, hours,
            minutes));

        minutes = (totalRuntime % 60);
        hours = ((totalRuntime / 60) % 24);
        days = ((totalRuntime / 3600));

        this.totalRuntime.setText(String.format(
            this.getString(R.string.total_runtime_format), days, hours,
            minutes));

        this.timeOfWatchedEpisodesProgressBar.setMax(totalRuntime);
        this.timeOfWatchedEpisodesProgressBar.setProgress(watchedRuntime);
    }
}