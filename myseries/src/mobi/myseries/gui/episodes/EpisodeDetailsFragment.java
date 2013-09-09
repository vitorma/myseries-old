package mobi.myseries.gui.episodes;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import android.app.ListFragment;
import android.os.Bundle;

public class EpisodeDetailsFragment extends ListFragment {
    private static final String EPISODE_NUMBER = "episodeNumber";
    private static final String SEASON_NUMBER = "seasonNumber";
    private static final String SERIES_ID = "seriesId";

    private int seriesId;
    private int seasonNumber;
    private int episodeNumber;

    public static EpisodeDetailsFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        EpisodeDetailsFragment episodeFragment = new EpisodeDetailsFragment();

        Bundle arguments = new Bundle();

        arguments.putInt(SERIES_ID, seriesId);
        arguments.putInt(SEASON_NUMBER, seasonNumber);
        arguments.putInt(EPISODE_NUMBER, episodeNumber);

        episodeFragment.setArguments(arguments);

        return episodeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = this.getArguments();

        this.seriesId = arguments.getInt(SERIES_ID);
        this.seasonNumber = arguments.getInt(SEASON_NUMBER);
        this.episodeNumber = arguments.getInt(EPISODE_NUMBER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Episode episode =
                App.seriesFollowingService().getFollowedSeries(this.seriesId).season(this.seasonNumber).episode(this.episodeNumber);

        this.setListAdapter(new EpisodeDetailsAdapter(this.getActivity(), episode));
        this.getListView().setDivider(null);
        this.getListView().setPadding(0, 0, 0, 0);
    }
}
