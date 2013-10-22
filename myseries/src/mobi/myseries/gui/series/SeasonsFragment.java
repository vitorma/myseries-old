package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.EpisodeWatchMarkSpecification;
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

public class SeasonsFragment extends Fragment {
    public static SeasonsFragment newInstance(int seriesId) {
        SeasonsFragment seasonsFragment = new SeasonsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seasonsFragment.setArguments(arguments);

        return seasonsFragment;
    }

    private int mSeriesId;
    private SeasonsAdapter adapter;

    private ListView list;

    private SeenMark mSeenMark;
    private ImageButton sortButton;
    private CheckedTextView statisticsButton;
    private View statisticsPanel;
    private View divider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSeriesId = this.getArguments().getInt(Extra.SERIES_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.series_seasons, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        this.mSeenMark = (SeenMark) this.getActivity().findViewById(R.id.seenMark);
        this.sortButton = (ImageButton) this.getActivity().findViewById(R.id.sort);
        this.statisticsButton = (CheckedTextView) this.getActivity().findViewById(R.id.statistics);
        this.statisticsPanel = this.getActivity().findViewById(R.id.statisticsPanel);

        final Series series = App.seriesFollowingService().getFollowedSeries(this.mSeriesId);

        boolean checked = series.numberOfEpisodes(new EpisodeWatchMarkSpecification(true)) == series.numberOfEpisodes();

        this.mSeenMark.setChecked(checked);
        this.mSeenMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SeasonsFragment.this.mSeenMark.isChecked()) {
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

        if (this.adapter == null) {
            this.adapter = new SeasonsAdapter(this.mSeriesId);
        }

        this.list = (ListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);
        this.list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Season season = (Season) SeasonsFragment.this.adapter.getItem(position);

                Intent intent = EpisodesActivity.newIntent(
                        SeasonsFragment.this.getActivity(),
                        SeasonsFragment.this.mSeriesId,
                        season.number());

                SeasonsFragment.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        App.preferences().forActivities().register(this.adapter);
        App.markingService().register(mMarkingListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forActivities().deregister(this.adapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        App.markingService().deregister(mMarkingListener);
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

    private void showSortingDialog() {
        new SeasonSortingDialogFragment().show(this.getFragmentManager(), "sortingDialog");
    }

    private void updateStatistics() {
        Series series = App.seriesFollowingService().getFollowedSeries(this.mSeriesId);

        int numberOfUnwatchedEpisodes = series.numberOfEpisodes(new EpisodeWatchMarkSpecification(false));
        if (numberOfUnwatchedEpisodes == 0) {
            this.statisticsButton.setText("");
        } else {
            String pluralOfRemaining = this.getResources().getQuantityString(
                    R.plurals.plural_remaining,
                    numberOfUnwatchedEpisodes,
                    numberOfUnwatchedEpisodes);
            this.statisticsButton.setText(pluralOfRemaining);
        }

        TextView allEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.allEpisodes);
        allEpisodes.setText("/" + series.numberOfEpisodes());

        int numberOfWatchedEpisodes = series.numberOfEpisodes() - numberOfUnwatchedEpisodes;
        TextView watchedEpisodes = (TextView) this.statisticsPanel.findViewById(R.id.watchedEpisodes);
        watchedEpisodes.setText(String.valueOf(numberOfWatchedEpisodes));

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
            int numberOfSpecials = series.season(Season.SPECIAL_SEASON_NUMBER).numberOfEpisodes();
            String pluralOfSpecial = App.resources().getQuantityString(
                R.plurals.plural_special,
                numberOfSpecials,
                numberOfSpecials);
            specialEpisodes.setText(pluralOfSpecial);
        } else {
            specialEpisodes.setText(R.string.none_special);
        }
    }

    /* MarkingListener */

    private final MarkingListener mMarkingListener = new MarkingListener() {
        @Override
        public void onMarked(Series s) {
            onChangeNumberOfWatchedEpisodes(s);
        }

        @Override
        public void onMarked(Season s) {
            onChangeNumberOfWatchedEpisodes(App.seriesFollowingService().getFollowedSeries(s.seriesId()));
        }

        @Override
        public void onMarked(Episode e) {
            onChangeNumberOfWatchedEpisodes(App.seriesFollowingService().getFollowedSeries(e.seriesId()));
        }

        private void onChangeNumberOfWatchedEpisodes(Series series) {
            if (series.id() != mSeriesId) { return; }

            mSeenMark.setChecked(series.numberOfEpisodes() == series.numberOfEpisodes(new EpisodeWatchMarkSpecification(true)));

            updateStatistics();
            adapter.notifyDataSetChanged();
        }
    };
}
