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
import mobi.myseries.gui.shared.ToastBuilder;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
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
    private TextView watchedEpisodes;
    private TextView unwatchedEpisodes;
    private TextView unairedEpisodes;
    private SeenEpisodesBar seenEpisodesBar;
    private SeenMark seenMark;
    private ImageButton sortButton;
    private int seriesId;
    private SeasonsAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
    }

    @Override
    public void onStart() {
        super.onStart();

        App.preferences().forActivities().register(this.adapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forActivities().deregister(this.adapter);
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

        App.seriesProvider().getSeries(this.seriesId).register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.series_seasons, container, false);

        this.watchedEpisodes = (TextView) view.findViewById(R.id.watchedEpisodes);
        this.unwatchedEpisodes = (TextView) view.findViewById(R.id.unwatchedEpisodes);
        this.unairedEpisodes = (TextView) view.findViewById(R.id.unairedEpisodes);
        this.seenMark = (SeenMark) view.findViewById(R.id.seenMark);
        this.sortButton = (ImageButton) view.findViewById(R.id.sort);
        this.seenEpisodesBar = (SeenEpisodesBar) view.findViewById(R.id.seenEpisodesBar);

        final Series series = App.seriesProvider().getSeries(this.seriesId);

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

        this.sortButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeasonsFragment.this.showSortingDialog();
            }
        });

        this.seenEpisodesBar.updateWithEpisodesOf(series);
        this.updateSeenEpisodes();

        int numberOfUnairedEpisodes = series.numberOfEpisodes(new UnairedEpisodeSpecification());
        String pluralOfUnaired = App.resources().getQuantityString(R.plurals.plural_unaired, numberOfUnairedEpisodes);
        String allAired = App.resources().getString(R.string.all_aired);

        this.unairedEpisodes.setText(
            numberOfUnairedEpisodes > 0 ?
            numberOfUnairedEpisodes + " " + pluralOfUnaired :
            allAired);

        return view;
    }

    private void showSortingDialog() {
        if (this.adapter.isEmpty()) {
            new ToastBuilder(this.getActivity()).setMessage(R.string.no_seasons_to_sort).build().show();
        } else {
            new SeasonSortingDialogFragment().show(this.getFragmentManager(), "sortingDialog");
        }
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) { }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) { }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        Log.d("SeasonsFragment", "called");
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
        Series series = App.seriesProvider().getSeries(this.seriesId);

        int numberOfUnwatchedEpisodes = series.numberOfUnwatchedEpisodes();
        String pluralOfUnwatched = this.getResources().getQuantityString(R.plurals.plural_unwatched, numberOfUnwatchedEpisodes);
        String allWatched = this.getResources().getString(R.string.all_watched);

        this.watchedEpisodes.setText(series.numberOfSeenEpisodes() + "/" + series.numberOfEpisodes());
        this.unwatchedEpisodes.setText(
            numberOfUnwatchedEpisodes > 0 ?
            numberOfUnwatchedEpisodes + " " + pluralOfUnwatched :
            allWatched);
    }
}
