/*
 *   SeriesCoverFlowFragment.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui.myseries;

import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.UpdateFinishListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.series.SeriesActivity;
import mobi.myseries.gui.shared.CoverFlow;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Strings;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

public class SeriesCoverFlowFragment extends Fragment implements SeriesListener {
    /* TODO(Reul): Use MessageService instead of UpdateService*/
    private static final FollowSeriesService FOLLOW_SERIES_SERVICE = App.followSeriesService();
    private static final UpdateService UPDATE_SERIES_SERVICE = App.updateSeriesService();

    private static final Comparator<Series> COMPARATOR = new SeriesComparator();

    private SeriesCoverFlowAdapter seriesAdapter;
    private CoverFlow coverFlow;
    private SeriesItemViewHolder seriesItemViewHolder;

    private final SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {

        @Override
        public void onFollowing(Series followedSeries) {
            if (SeriesCoverFlowFragment.this.getActivity() != null) {
                SeriesCoverFlowFragment.this.reload();
            }
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            if (SeriesCoverFlowFragment.this.getActivity() != null) {
                SeriesCoverFlowFragment.this.reload();
            }
        }

        @Override
        public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
            if (SeriesCoverFlowFragment.this.getActivity() != null) {
                SeriesCoverFlowFragment.this.reload();
            }
        }

        @Override
        public void onFollowingStart(Series seriesToFollow) {}

        @Override
        public void onFollowingFailure(Series series, Exception e) {}
    };

    private final UpdateFinishListener updateListener = new UpdateFinishListener() {
        @Override
        public void onUpdateFinish() {
            this.reload();
        }

        private void reload() {
            if (SeriesCoverFlowFragment.this.getActivity() != null) {
                SeriesCoverFlowFragment.this.reload();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SeriesCoverFlowFragment.UPDATE_SERIES_SERVICE.register(this.updateListener);
        SeriesCoverFlowFragment.FOLLOW_SERIES_SERVICE.register(this.seriesFollowingListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myseries_item, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.reload();
    }

    public void reload() {
        this.seriesAdapter = new SeriesCoverFlowAdapter().sort(SeriesCoverFlowFragment.COMPARATOR);

        this.coverFlow = (CoverFlow) this.getActivity().findViewById(R.id.coverflow);
        this.coverFlow.setAdapter(this.seriesAdapter);

        if (this.seriesAdapter.isEmpty()) {
            ((TextView) this.getActivity().findViewById(R.id.empty)).setVisibility(View.VISIBLE);
            this.getActivity().findViewById(R.id.data).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) this.getActivity().findViewById(R.id.empty)).setVisibility(View.INVISIBLE);
            this.getActivity().findViewById(R.id.data).setVisibility(View.VISIBLE);

            //TODO Extract this block
            this.seriesItemViewHolder = new SeriesItemViewHolder();
            this.seriesItemViewHolder.name = (TextView) this.getActivity().findViewById(R.id.name);
            this.seriesItemViewHolder.status = (TextView) this.getActivity().findViewById(R.id.status);
            this.seriesItemViewHolder.airInfo = (TextView) this.getActivity().findViewById(R.id.airInfo);
            this.seriesItemViewHolder.seenEpisodes = (TextView) this.getActivity().findViewById(R.id.seenEpisodes);
            this.seriesItemViewHolder.seenEpisodesBar = (SeenEpisodesBar) this.getActivity().findViewById(R.id.seenEpisodesBar);

            this.seriesAdapter.registerSeriesListener(this);
            this.setUpListeners();
            this.coverFlow.selectMiddleItem();
        }
    }

    private void setUpListeners() {
        this.coverFlow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
                Series series = SeriesCoverFlowFragment.this.seriesAdapter.itemOf(position);

                if (!SeriesCoverFlowFragment.this.isSelected(series)) {return;}

                Intent intent = SeriesActivity.newIntent(view.getContext(), series.id());
                SeriesCoverFlowFragment.this.startActivity(intent);
            }
        });

        this.coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView< ? > parent, final View view, final int position, final long id) {
                Series item = SeriesCoverFlowFragment.this.seriesAdapter.itemOf(position);
                SeriesCoverFlowFragment.this.downloadDescription(item);
            }

            @Override
            public void onNothingSelected(final AdapterView< ? > parent) {}
        });
    }

    private void downloadDescription(Series item) {
        this.seriesItemViewHolder.name.setText(item.name());
        this.seriesItemViewHolder.status.setText(LocalText.of(item.status(), ""));

        String airDay = DatesAndTimes.toString(item.airDay(), Locale.getDefault(), "");
        String airtime = DatesAndTimes.toString(item.airtime(), DateFormat.getTimeFormat(App.context()), "");
        String network = item.network();
        String airInfo = Strings.concat(airDay, airtime, ", ");
        airInfo = Strings.concat(airInfo, network, " - ");
        this.seriesItemViewHolder.airInfo.setText(airInfo);

        String seenEpisodes = item.numberOfSeenEpisodes() + "/" + item.numberOfEpisodes();
        this.seriesItemViewHolder.seenEpisodes.setText(seenEpisodes);

        this.seriesItemViewHolder.seenEpisodesBar.updateWithEpisodesOf(item);
    }

    private boolean isSelected(Series series) {
        int pos = this.coverFlow.getSelectedItemPosition();
        if (pos == AdapterView.INVALID_POSITION) {return false;}
        return series.equals(this.seriesAdapter.itemOf(pos));
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        if (this.isSelected(series)) {
            this.downloadDescription(series);
        }
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        if (this.isSelected(series)) {
            this.downloadDescription(series);
        }
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {
        if (this.isSelected(series)) {
            this.downloadDescription(series);
        }
    }

    private static class SeriesItemViewHolder {
        private TextView name;
        private TextView status;
        private TextView airInfo;
        private TextView seenEpisodes;
        private SeenEpisodesBar seenEpisodesBar;
    }

    @Override
    public void onMarkAsSeen(Series series) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onMarkAsNotSeen(Series series) {
        // TODO Auto-generated method stub

    }
}
