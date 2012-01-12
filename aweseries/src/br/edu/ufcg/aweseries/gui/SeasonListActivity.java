/*
 *   SeasonListActivity.java
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


package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.DomainObjectListener;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

/**
 * GUI representation of the list of seasons for a series.
 */
public class SeasonListActivity extends ListActivity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final SeasonComparator SEASON_COMPARATOR = new SeasonComparator();

    private Series series;

    private static final class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season seasonA, Season seasonB) {
            if (seasonA.number() == 0) {
                return -seasonB.number();
            }

            if (seasonB.number() == 0) {
                return seasonA.number();
            }

            return seasonB.number() - seasonA.number();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        this.loadSeries();

        //set view title
        TextView listingTitle = (TextView) findViewById(R.id.listingTitleTextView);
        listingTitle.setText(String.format(this.getString(R.string.seasons_of_series),
                this.series.getName()));

        populateSeasonsList();
        setUpSeasonItemClickListener();
    }

    private void loadSeries() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String seriesId = extras.getString("series id");
            this.series = seriesProvider.getSeries(seriesId);
        }
    }

    private void setUpSeasonItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), EpisodeListActivity.class);
                final Season season = (Season) parent.getItemAtPosition(position);
                intent.putExtra("season number", season.number());
                intent.putExtra("series id", series.getId());
                SeasonListActivity.this.startActivity(intent);
            }
        });

    }

    //XXX: Use TextViewAdapter instead.
    private class SeasonItemViewAdapter extends ArrayAdapter<Season> implements
            DomainObjectListener<Season> {
        public SeasonItemViewAdapter(Context context, int seasonsItemResourceId,
                List<Season> objects) {
            super(context, seasonsItemResourceId, objects);

            for (Season s : objects) {
                s.addListener(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = itemViewFrom(convertView);

            Season season = getItem(position);

            this.showSeasonDataOn(season, itemView);
            this.setUpSeenSeasonCheckBoxListenerFor(season, itemView);

            return itemView;
        }

        private View itemViewFrom(View convertView) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) SeasonListActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.season_list_item, null);
            }

            return itemView;
        }

        private void showSeasonDataOn(Season season, View itemView) {
            TextView name = (TextView) itemView.findViewById(R.id.itemName);

            if (season.number() == 0) {
                name.setText(getString(R.string.special_episodes));
            }

            else {
                name.setText(String.format(getString(R.string.season_number_format),
                        season.number()));
            }

            CheckBox isSeasonViewed = (CheckBox) itemView.findViewById(R.id.isSeasonViewedCheckBox);
            isSeasonViewed.setChecked(season.areAllSeen());
        }

        private void setUpSeenSeasonCheckBoxListenerFor(final Season season, View itemView) {
            final CheckBox isSeasonViewed = (CheckBox) itemView
                    .findViewById(R.id.isSeasonViewedCheckBox);

            isSeasonViewed.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isSeasonViewed.isChecked()) {
                        seriesProvider.markSeasonAsSeen(season);
                    } else {
                        seriesProvider.markSeasonAsNotSeen(season);
                    }
                }
            });
        }

        @Override
        public void onUpdate(Season season) {
            this.notifyDataSetChanged();
        }
    }

    private void populateSeasonsList() {
        SeasonItemViewAdapter dataAdapter = new SeasonItemViewAdapter(this,
                R.layout.season_list_item, this.series.getSeasons().toList());
        this.setListAdapter(dataAdapter);
        dataAdapter.sort(SEASON_COMPARATOR);
    }
}
