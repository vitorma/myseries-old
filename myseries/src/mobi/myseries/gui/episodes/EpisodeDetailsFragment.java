package mobi.myseries.gui.episodes;

import mobi.myseries.application.App;
import mobi.myseries.gui.shared.Extra;
import android.app.ListFragment;
import android.os.Bundle;

//TODO (Cleber) Let this class be a simple Fragment instead of ListFragment
public class EpisodeDetailsFragment extends ListFragment {
    private EpisodeDetailsAdapter mAdapter;

    public static EpisodeDetailsFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);
        arguments.putInt(Extra.EPISODE_NUMBER, episodeNumber);

        EpisodeDetailsFragment instance = new EpisodeDetailsFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new EpisodeDetailsAdapter(App.seriesFollowingService()
                .getFollowedSeries(getArguments().getInt(Extra.SERIES_ID))
                .season(getArguments().getInt(Extra.SEASON_NUMBER))
                .episode(getArguments().getInt(Extra.EPISODE_NUMBER)));

        setListAdapter(mAdapter);
        getListView().setDivider(null);
        getListView().setPadding(0, 0, 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.registerServiceListeners();
    }

    @Override
    public void onPause() {
        super.onPause();

        mAdapter.deregisterServiceListeners();
    }
}
