package mobi.myseries.gui.statistics;

import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.SeasonSet;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.widget.ProgressBar;
import android.widget.TextView;

//TODO(Reul): Listen to update and addition of series, update itself when numbers change.

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

        this.update();
    }

    private void update() {
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

        this.numberOfSeries.setText(String.format("%d", nSeries));
        this.numberOfSeasons.setText(String.format("%d", nSeasons));
        this.numberOfEpisodes.setText(String.format("%d", nEpisodes));

        this.seriesWatched.setText(String.format(this.getString(R.string.series_watched),
            watchedSeries));
        this.seasonsWatched.setText(String.format(this.getString(R.string.seasons_watched),
            watchedSeasons));
        this.episodesWatched.setText(String.format(this.getString(R.string.episodes_watched),
            watchedEpisodes));

        this.seriesWatchedProgressBar.setMax(nSeries);
        this.seriesWatchedProgressBar.setProgress(watchedSeries);

        this.seasonsWatchedProgressBar.setMax(nSeasons);
        this.seasonsWatchedProgressBar.setProgress(watchedSeasons);

        this.episodesWatchedProgressBar.setMax(nEpisodes);
        this.episodesWatchedProgressBar.setProgress(watchedEpisodes);

        int minutes = (watchedRuntime % 60);
        int hours = ((watchedRuntime / 60) % 24);
        int days = ((watchedRuntime / 3600));

        this.watchedEpisodesRuntime.setText(String.format(
            this.getString(R.string.total_runtime_format), days, hours, minutes));
        // TODO Auto-generated method stub

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

}
