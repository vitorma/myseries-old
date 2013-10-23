package mobi.myseries.gui.series;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.EpisodeWatchMarkSpecification;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.UnairedEpisodeSpecification;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
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

    private SeenMark mWatchMark;
    private ImageButton mSortButton;
    private CheckedTextView mStatisticsButton;
    private SeasonsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.series_seasons, container, false);

        mWatchMark = (SeenMark) view.findViewById(R.id.seenMark);
        mSortButton = (ImageButton) view.findViewById(R.id.sort);
        mStatisticsButton = (CheckedTextView) view.findViewById(R.id.statistics);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViews();
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshViews();

        App.markingService().register(mMarkingListener);
        App.preferences().forActivities().register(mOnSharedPreferenceChangeListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        App.markingService().deregister(mMarkingListener);
        App.preferences().forActivities().deregister(mOnSharedPreferenceChangeListener);
    }

    /* Auxiliary */

    private void setUpViews() {
        Series series = App.seriesFollowingService().getFollowedSeries(seriesId());

        setUpWatchMark(series);
        setUpSortButton();
        setUpStatisticsButton(series);
        setUpSeasonList(series.seasons().seasons());
    }

    private void refreshViews() {
        refreshViews(App.seriesFollowingService().getFollowedSeries(seriesId()));
    }

    private void refreshViews(Series series) {
        refreshWatchMark(series);
        refreshStatisticViews(series);
        mAdapter.notifyDataSetChanged();
    }

    private void setUpWatchMark(final Series series) {
        mWatchMark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWatchMark.isChecked()) {
                    App.markingService().markAsWatched(series);
                } else {
                    App.markingService().markAsUnwatched(series);
                }
            }
        });
    }

    private void setUpSortButton() {
        mSortButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new SeasonSortingDialogFragment().show(getFragmentManager(), "sortingDialog");
            }
        });
    }

    private void setUpStatisticsButton(final Series series) {
        mStatisticsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mStatisticsButton.toggle();
                refreshStatisticViews(series);
            }
        });
    }

    private void setUpSeasonList(List<Season> seasons) {
        mAdapter = new SeasonsAdapter(seasons);
        ListView list = (ListView) getView().findViewById(R.id.seasons);

        list.setAdapter(mAdapter);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(EpisodesActivity.newIntent(
                        App.context(),
                        seriesId(),
                        mAdapter.getSeason(position).number()));
            }
        });
    }

    private void refreshWatchMark(Series series) {
        mWatchMark.setChecked(series.numberOfEpisodes() == series.numberOfEpisodes(new EpisodeWatchMarkSpecification(true)));
    }

    //TODO (Cleber) Extract code
    private void refreshStatisticViews(Series series) {
        int numberOfUnwatchedEpisodes = series.numberOfEpisodes(new EpisodeWatchMarkSpecification(false));
        mStatisticsButton.setText(
                numberOfUnwatchedEpisodes == 0 ?
                "" :
                LocalText.getPlural(R.plurals.plural_remaining, numberOfUnwatchedEpisodes, numberOfUnwatchedEpisodes));

        View statisticsPanel = getView().findViewById(R.id.statisticsPanel);
        View divider = getView().findViewById(R.id.divider);

        if (!mStatisticsButton.isChecked()) {
            divider.setVisibility(View.VISIBLE);
            statisticsPanel.setVisibility(View.GONE);
            return;
        }

        divider.setVisibility(View.GONE);
        statisticsPanel.setVisibility(View.VISIBLE);

        TextView allEpisodes = (TextView) statisticsPanel.findViewById(R.id.allEpisodes);
        allEpisodes.setText("/" + series.numberOfEpisodes());

        int numberOfWatchedEpisodes = series.numberOfEpisodes() - numberOfUnwatchedEpisodes;
        TextView watchedEpisodes = (TextView) statisticsPanel.findViewById(R.id.watchedEpisodes);
        watchedEpisodes.setText(String.valueOf(numberOfWatchedEpisodes));

        SeenEpisodesBar watchProgress = (SeenEpisodesBar) statisticsPanel.findViewById(R.id.seenEpisodesBar);
        watchProgress.updateWithEpisodesOf(series);

        int numberOfUnairedEpisodes = series.numberOfEpisodes(new UnairedEpisodeSpecification());
        TextView unairedEpisodes = (TextView) statisticsPanel.findViewById(R.id.unairedEpisodes);
        unairedEpisodes.setText(
            numberOfUnairedEpisodes > 0 ?
            numberOfUnairedEpisodes + " " + LocalText.getPlural(R.plurals.plural_unaired, numberOfUnairedEpisodes) :
            LocalText.get(R.string.all_aired));

        TextView specialEpisodes = (TextView) statisticsPanel.findViewById(R.id.specialEpisodes);
        if (series.hasSpecialEpisodes()) {
            int numberOfSpecials = series.season(Season.SPECIAL_SEASON_NUMBER).numberOfEpisodes();
            specialEpisodes.setText(LocalText.getPlural(R.plurals.plural_special, numberOfSpecials, numberOfSpecials));
        } else {
            specialEpisodes.setText(R.string.none_special);
        }
    }

    private int seriesId() {
        return getArguments().getInt(Extra.SERIES_ID);
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
            if (series.id() != seriesId()) { return; }

            refreshViews(series);
        }
    };

    /* OnSharedPreferenceChangedListener */
    //TODO (Cleber) Create SeriesPreferencesListener and let this guy implement it

    private final OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            mAdapter.sortItems();
            mAdapter.notifyDataSetChanged();
        }
    };
}
