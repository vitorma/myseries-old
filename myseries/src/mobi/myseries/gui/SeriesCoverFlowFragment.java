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

package mobi.myseries.gui;

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.widget.CoverFlow;
import mobi.myseries.gui.widget.ReflectingImageAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.TextView;

public class SeriesCoverFlowFragment extends Fragment {
    private SeriesCoverFlowAdapter seriesAdapter;
    private ReflectingImageAdapter adapter;
    private CoverFlow coverFlow;
    private SeriesItemViewHolder seriesItemViewHolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.coverflow, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.seriesAdapter = new SeriesCoverFlowAdapter();
        this.adapter = new ReflectingImageAdapter(this.seriesAdapter);
        this.coverFlow = (CoverFlow) this.getActivity().findViewById(R.id.coverflow);
        this.coverFlow.setAdapter(this.adapter);

        this.seriesItemViewHolder = new SeriesItemViewHolder();
        this.seriesItemViewHolder.name = (TextView) this.getActivity().findViewById(R.id.coverflow_item_name);
        this.seriesItemViewHolder.nextToSee = (TextView) this.getActivity().findViewById(R.id.coverflow_item_next_to_see);
        this.seriesItemViewHolder.seenMark = (CheckBox) this.getActivity().findViewById(R.id.coverflow_item_seen_mark);

        this.setupListeners();
        this.coverFlow.setSelection(0);
    }

    private void setupListeners() {
        this.coverFlow.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
                Intent intent = new Intent(view.getContext(), SeriesDetailsActivity.class);
                Series series = seriesAdapter.itemOf(position);
                intent.putExtra("series id", series.id());
                intent.putExtra("series name", series.name());
                SeriesCoverFlowFragment.this.startActivity(intent);
            }
        });

        this.coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView< ? > parent, final View view, final int position, final long id) {
                Series item = seriesAdapter.itemOf(position);
                seriesItemViewHolder.name.setText(item.name());
                Episode next = item.nextEpisodeToSee(true);
                if (next != null) {
                    seriesItemViewHolder.nextToSee.setText(next.name());
                    seriesItemViewHolder.seenMark.setChecked(next.wasSeen());
                } else {
                    seriesItemViewHolder.nextToSee.setText(R.string.up_to_date);
                    seriesItemViewHolder.seenMark.setChecked(false);
                }
            }

            @Override
            public void onNothingSelected(final AdapterView< ? > parent) {
                //TODO Implement
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------

    private static class SeriesItemViewHolder {
        private TextView name;
        private TextView nextToSee;
        private CheckBox seenMark;
    }
}
