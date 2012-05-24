package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;

public class EpisodeFragment extends SherlockListFragment {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final String EPISODE_NUMBER = "episodeNumber";
    private static final String SEASON_NUMBER = "seasonNumber";
    private static final String SERIES_ID = "seriesId";

    private int seriesId;
    private int seasonNumber;
    private int episodeNumber;

    public static EpisodeFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        EpisodeFragment episodeFragment = new EpisodeFragment();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.episodes_fragment_deletemeasap, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Episode episode =
            SERIES_PROVIDER.getSeries(this.seriesId).season(this.seasonNumber).episode(this.episodeNumber);

        this.setListAdapter(new EpisodeAdapter(getActivity(), episode));
    }
}
