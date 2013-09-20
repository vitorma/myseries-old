package mobi.myseries.gui.myschedule;

import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.Extra;
import android.app.ListFragment;
import android.os.Bundle;

public class ScheduleDetailItemFragment extends ListFragment {
    private int mSeriesId;
    private int mSeasonNumber;
    private int mEpisodeNumber;

    public static ScheduleDetailItemFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        ScheduleDetailItemFragment episodeFragment = new ScheduleDetailItemFragment();

        Bundle arguments = new Bundle();

        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);
        arguments.putInt(Extra.EPISODE_NUMBER, episodeNumber);

        episodeFragment.setArguments(arguments);

        return episodeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();

        mSeriesId = arguments.getInt(Extra.SERIES_ID);
        mSeasonNumber = arguments.getInt(Extra.SEASON_NUMBER);
        mEpisodeNumber = arguments.getInt(Extra.EPISODE_NUMBER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Episode episode = App.seriesFollowingService()
                .getFollowedSeries(mSeriesId)
                .season(mSeasonNumber)
                .episode(mEpisodeNumber);

        setListAdapter(new ScheduleDetailItemAdapter(getActivity(), episode));
        getListView().setDivider(null);
        getListView().setPadding(0, 0, 0, 0);
    }
}
