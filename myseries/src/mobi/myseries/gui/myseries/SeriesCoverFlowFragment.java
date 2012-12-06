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

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.series.SeriesActivity;
import mobi.myseries.gui.shared.CoverFlow;
import mobi.myseries.gui.shared.ReflectingImageAdapter;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SeriesCoverFlowFragment extends SherlockFragment implements SeriesListener {
    /* TODO(Reul): Use MessageService instead of UpdateService*/
    private static final FollowSeriesService FOLLOW_SERIES_SERVICE = App.followSeriesService();
    private static final UpdateService UPDATE_SERIES_SERVICE = App.updateSeriesService();

    private static final Comparator<Series> COMPARATOR = new SeriesComparator();

    private SeriesCoverFlowAdapter seriesAdapter;
    private ReflectingImageAdapter adapter;
    private CoverFlow coverFlow;
    private SeriesItemViewHolder seriesItemViewHolder;

    private SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {

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

    private UpdateListener updateListener = new UpdateListener() {

        @Override
        public void onUpdateSuccess() {
            this.reload();
        }

        @Override
        public void onUpdateFailure(Exception e) {
            // TODO(Gabriel) Should we really do something here?
            // May the series have been partially updated after a failure?
            this.reload();
        }

        @Override
        public void onUpdateStart() {}

        @Override
        public void onUpdateNotNecessary() {}

        private void reload() {
            if (SeriesCoverFlowFragment.this.getActivity() != null) {
                SeriesCoverFlowFragment.this.reload();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UPDATE_SERIES_SERVICE.register(this.updateListener);
        FOLLOW_SERIES_SERVICE.registerSeriesFollowingListener(this.seriesFollowingListener);
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
        this.seriesAdapter = new SeriesCoverFlowAdapter().sort(COMPARATOR);
        this.adapter = new ReflectingImageAdapter(this.seriesAdapter);
        this.coverFlow = (CoverFlow) this.getActivity().findViewById(R.id.coverflow);
        this.coverFlow.setAdapter(this.adapter);

        if (this.seriesAdapter.isEmpty()) {
            ((TextView) this.getActivity().findViewById(R.id.coverflow_empty)).setVisibility(View.VISIBLE);
            this.getActivity().findViewById(R.id.seriesData).setVisibility(View.INVISIBLE);
            return;
        }

        this.seriesItemViewHolder = new SeriesItemViewHolder();
        this.seriesItemViewHolder.name = (TextView) this.getActivity().findViewById(R.id.nameTextView);
        this.seriesItemViewHolder.bar = (SeenEpisodesBar) this.getActivity().findViewById(R.id.seenEpisodesBar);

        this.seriesAdapter.registerSeriesListener(this);
        this.setUpListeners();
        this.coverFlow.selectMiddleItem();
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
        this.seriesItemViewHolder.bar.updateWithEpisodesOf(item);
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
        private SeenEpisodesBar bar;
    }
}
