package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SeasonsFragment extends Fragment implements SeriesListener {
    public static SeasonsFragment newInstance(int seriesId) {
        SeasonsFragment seasonsFragment = new SeasonsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seasonsFragment.setArguments(arguments);

        return seasonsFragment;
    }

    private ListView list;
    private TextView name;
    private TextView seenEpisodes;
    private SeenEpisodesBar seenEpisodesBar;
    private SeenMark seenMark;
    private int seriesId;
    private SeasonsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
    }

    @Override
    public void onStart() {
        super.onStart();

        App.seriesProvider().getSeries(this.seriesId).register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.seriesProvider().getSeries(this.seriesId).deregister(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.adapter = new SeasonsAdapter(this.seriesId);

        this.list = (ListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);
        this.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Season season = (Season) SeasonsFragment.this.adapter.getItem(position);

                Intent intent = SeasonActivity.newIntent(
                        SeasonsFragment.this.getActivity(),
                        SeasonsFragment.this.seriesId,
                        season.number());

                SeasonsFragment.this.startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.series_seasons, container, false);

        this.name = (TextView) view.findViewById(R.id.name);
        this.seenEpisodes = (TextView) view.findViewById(R.id.seenEpisodes);
        this.seenMark = (SeenMark) view.findViewById(R.id.seenMark);
        this.seenEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);

        final Series series = App.seriesProvider().getSeries(this.seriesId);

        this.name.setText(series.name());
        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());
        this.seenMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SeasonsFragment.this.seenMark.isChecked()) {
                    App.seriesProvider().markSeriesAsSeen(series);
                } else {
                    App.seriesProvider().markSeriesAsNotSeen(series);
                }
            }
        });
        this.seenEpisodesBar.updateWithEpisodesOf(series);
        this.updateSeenEpisodes();

        series.register(this);

        return view;
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) { }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) { }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        // TODO(Reul): if user wants special items to be displayed…
        this.seenEpisodesBar.updateWithEpisodesOf(series);
        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());
        this.updateSeenEpisodes();
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

    private void updateSeenEpisodes() {
        final Series series = App.seriesProvider().getSeries(this.seriesId);

        String fraction = String.format(this.getString(R.string.fraction),
            series.numberOfSeenEpisodes(), series.numberOfEpisodes());
        String pluralOfEpisode = this.getResources().getQuantityString(R.plurals.plural_episode,
            series.numberOfEpisodes());
        String pluralOfWasSeen = this.getResources().getQuantityString(R.plurals.plural_was_seen,
            series.numberOfSeenEpisodes());

        this.seenEpisodes.setText(fraction + " " + pluralOfEpisode + " " + pluralOfWasSeen);
    }
}
