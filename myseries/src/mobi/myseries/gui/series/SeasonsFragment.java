package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
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
    private CheckedTextView statisticsButton;
    private View statisticsPanel;
    private SeenMark seenMark;
    private ImageButton sortButton;
    private int seriesId;
    private SeasonsAdapter adapter;
    private View divider;

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

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);

        this.seenMark = (SeenMark) this.getActivity().findViewById(R.id.seenMark);
        this.sortButton = (ImageButton) this.getActivity().findViewById(R.id.sort);
        this.statisticsButton = (CheckedTextView) this.getActivity().findViewById(R.id.statistics);
        this.statisticsPanel = this.getActivity().findViewById(R.id.statisticsPanel);

        final Series series = App.seriesFollowingService().getFollowedSeries(this.seriesId);

        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());
        this.seenMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SeasonsFragment.this.seenMark.isChecked()) {
                    App.markingService().markAsWatched(series);
                } else {
                    App.markingService().markAsUnwatched(series);
                }
            }
        });

        this.sortButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeasonsFragment.this.showSortingDialog();
            }
        });

        this.statisticsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeasonsFragment.this.statisticsButton.toggle();

                SeasonsFragment.this.updateVisibilityOfStatisticsPanel();
            }
        });

        this.updateStatistics();
        this.updateVisibilityOfStatisticsPanel();

        this.adapter = new SeasonsAdapter(this.seriesId);

        this.list = (ListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);
        this.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Season season = (Season) SeasonsFragment.this.adapter.getItem(position);

                Intent intent = EpisodesActivity.newIntent(
                        SeasonsFragment.this.getActivity(),
                        SeasonsFragment.this.seriesId,
                        season.number());

                SeasonsFragment.this.startActivity(intent);
            }
        });

        App.seriesFollowingService().getFollowedSeries(this.seriesId).register(this);
    }

    private void updateVisibilityOfStatisticsPanel() {
        this.divider = this.getView().findViewById(R.id.divider);

        if (SeasonsFragment.this.statisticsButton.isChecked()) {
            this.divider.setVisibility(View.GONE);
            SeasonsFragment.this.statisticsPanel.setVisibility(View.VISIBLE);
        } else {
            this.divider.setVisibility(View.VISIBLE);
            SeasonsFragment.this.statisticsPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.series_seasons, container, false);
    }

    private void showSortingDialog() {
        new SeasonSortingDialogFragment().show(this.getFragmentManager(), "sortingDialog");
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) { }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) { }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        this.seenMark.setChecked(series.numberOfEpisodes() == series.numberOfSeenEpisodes());
        this.updateStatistics();
    }

    @Override
    public void onMarkAsNotSeen(Series series) {
        SeasonsFragment.this.updateStatistics();
    }

    @Override
    public void onMarkAsSeen(Series series) {
        SeasonsFragment.this.updateStatistics();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SERIES_ID, this.seriesId);
        super.onSaveInstanceState(outState);
    }

    private void updateStatistics() {
        Series series = App.seriesFollowingService().getFollowedSeries(this.seriesId);

        int numberOfUnwatchedEpisodes = series.numberOfUnwatchedEpisodes();
        String pluralOfRemaining = this.getResources().getQuantityString(
                R.plurals.plural_remaining,
                numberOfUnwatchedEpisodes,
                numberOfUnwatchedEpisodes);
        this.statisticsButton.setText(pluralOfRemaining);

        TextView allEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.allEpisodes);
        allEpisodes.setText("/" + series.numberOfEpisodes());

        TextView watchedEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.watchedEpisodes);
        watchedEpisodes.setText(String.valueOf(series.numberOfSeenEpisodes()));

        SeenEpisodesBar bar = (SeenEpisodesBar) this.statisticsPanel.findViewById(R.id.seenEpisodesBar);
        bar.updateWithEpisodesOf(series);

        TextView unairedEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.unairedEpisodes);
        int numberOfUnaired = series.numberOfEpisodes(new UnairedEpisodeSpecification());
        String pluralOfUnaired = App.resources().getQuantityString(R.plurals.plural_unaired, numberOfUnaired);
        String allAired = App.resources().getString(R.string.all_aired);
        unairedEpisodes.setText(
            numberOfUnaired > 0 ?
            numberOfUnaired + " " + pluralOfUnaired :
            allAired);

        TextView specialEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.specialEpisodes);
        if (series.hasSpecialEpisodes()) {
            int numberOfSpecials = series.season(Season.SPECIAL_EPISODES_SEASON_NUMBER).numberOfEpisodes();
            String pluralOfSpecial = App.resources().getQuantityString(
                R.plurals.plural_special,
                numberOfSpecials,
                numberOfSpecials);
            specialEpisodes.setText(pluralOfSpecial);
        } else {
            specialEpisodes.setText(R.string.none_special);
        }
    }
}
