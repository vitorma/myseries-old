package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.season.SeasonActivity;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

public class SeasonsFragment extends Fragment implements SeriesListener {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    public static SeasonsFragment newInstance(int seriesId) {
        SeasonsFragment seasonsFragment = new SeasonsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seasonsFragment.setArguments(arguments);

        return seasonsFragment;
    }

    private SeasonsExpandableAdapter adapter;
    private ExpandableListView list;
    private TextView name;
    private TextView seenEpisodes;
    private SeenEpisodesBar seenEpisodesBar;
    private SeenMark seenMark;
    private int seriesId;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Series series = SeasonsFragment.SERIES_PROVIDER.getSeries(this.seriesId);

        this.adapter = new SeasonsExpandableAdapter(this.getActivity(), series);

        this.list = (ExpandableListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);

        this.list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                int childPosition, long id) {
                SeasonsFragment.this.showDetailsOf(groupPosition, childPosition);
                return true;
            }
        });

        this.list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int numberOfGroups = SeasonsFragment.this.adapter.getGroupCount();

                for (int i = 0; i < numberOfGroups; i++) {
                    if (i != groupPosition) {
                        SeasonsFragment.this.list.collapseGroup(i);
                    }
                }
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        // TODO(Reul): if user wants special items to be displayed…
        this.seenEpisodesBar.updateWithEpisodesOf(series);
        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());
        this.updateSeenEpisodes();
    }

    @Override
    public void onDestroy() {
        SeasonsFragment.SERIES_PROVIDER.getSeries(this.seriesId).deregister(this);

        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        final View view = inflater.inflate(R.layout.series_seasons, container, false);

        Series series = SeasonsFragment.SERIES_PROVIDER.getSeries(this.seriesId);

        this.name = (TextView) view.findViewById(R.id.name);
        this.name.setText(series.name());

        this.seenEpisodes = (TextView) view.findViewById(R.id.seenEpisodes);

        this.seenMark = (SeenMark) view.findViewById(R.id.seenMark);
        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());

        this.seenEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);

        this.seenMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SeasonsFragment.this.seenMark.isChecked()) {
                    Series series = SeasonsFragment.SERIES_PROVIDER
                        .getSeries(SeasonsFragment.this.seriesId);
                    SeasonsFragment.SERIES_PROVIDER.markSeriesAsSeen(series);

                } else {
                    Series series = SeasonsFragment.SERIES_PROVIDER
                        .getSeries(SeasonsFragment.this.seriesId);
                    SeasonsFragment.SERIES_PROVIDER.markSeriesAsNotSeen(series);
                }
            }
        });

        this.seenEpisodesBar.updateWithEpisodesOf(series);
        this.updateSeenEpisodes();

        series.register(this);


        return view;
    }

    @Override
    public void onMarkAsNotSeen(Series series) {
        this.seenEpisodesBar.updateWithEpisodesOf(series);
        SeasonsFragment.this.updateSeenEpisodes();
    }

    @Override
    public void onMarkAsSeen(Series series) {
        this.seenEpisodesBar.updateWithEpisodesOf(series);
        SeasonsFragment.this.updateSeenEpisodes();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SERIES_ID, this.seriesId);
        super.onSaveInstanceState(outState);
    }

    private void showDetailsOf(int groupPosition, int childPosition) {
        int seasonNumber = this.adapter.season(groupPosition).number();
//        int episodeNumber = this.adapter.episode(groupPosition, childPosition).number();

        Intent intent = SeasonActivity.newIntent(this.getActivity(), this.seriesId, seasonNumber);
//        Intent intent = EpisodeActivity.newIntent(this.getActivity(), this.seriesId, seasonNumber,
//            episodeNumber);
        this.startActivity(intent);
    }

    private void updateSeenEpisodes() {
        final Series series = SeasonsFragment.SERIES_PROVIDER.getSeries(this.seriesId);
        String fraction = String.format(this.getString(R.string.fraction),
            series.numberOfSeenEpisodes(), series.numberOfEpisodes());
        String pluralOfEpisode = this.getResources().getQuantityString(R.plurals.plural_episode,
            series.numberOfEpisodes());
        String pluralOfWasSeen = this.getResources().getQuantityString(R.plurals.plural_was_seen,
            series.numberOfSeenEpisodes());
        this.seenEpisodes.setText(fraction + " " + pluralOfEpisode + " " + pluralOfWasSeen);

    }
}
