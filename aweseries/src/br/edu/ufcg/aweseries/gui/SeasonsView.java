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
import br.edu.ufcg.aweseries.SeriesProviderListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

/**
 * GUI representation of the list of seasons for a series.
 */
public class SeasonsView extends ListActivity {
    private static final SeasonComparator comparator = new SeasonComparator();

    private Series series;

    private SeasonItemViewAdapter dataAdapter;

    private static final class SeasonComparator implements Comparator<Season> {
        @Override
        public int compare(Season seasonA, Season seasonB) {
            return seasonA.getNumber() - seasonB.getNumber();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String seriesId = extras.getString("series id");
            this.series = seriesProvider().getSeries(seriesId);
        }

        //set view title
        TextView listingTitle = (TextView) findViewById(R.id.listingTitleTextView);
        listingTitle.setText(this.series.getName() + "'s Seasons");

        populateSeasonsList();
        setupItemClickListener();
    }

    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), EpisodesView.class);
                final Season season = (Season) parent.getItemAtPosition(position);
                intent.putExtra("season number", season.getNumber());
                intent.putExtra("series id", series.getId());
                SeasonsView.this.startActivity(intent);
            }
        });

    }

    //XXX: Use TextViewAdapter instead.
    private class SeasonItemViewAdapter extends ArrayAdapter<Season> implements
            SeriesProviderListener {
        public SeasonItemViewAdapter(Context context, int seasonsItemResourceId,
                List<Season> objects) {
            super(context, seasonsItemResourceId, objects);
            seriesProvider().addListener(this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            final Season season = getItem(position);

            // if no view was passed, create one for the item
            if (itemView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = vi.inflate(R.layout.season_list_item, null);
            }

            // get views for the series fields
            TextView name = (TextView) itemView.findViewById(R.id.itemName);

            // load series data
            name.setText(this.getItem(position).toString());

            final CheckBox isSeasonViewed = (CheckBox) itemView
                    .findViewById(R.id.isSeasonViewedCheckBox);

            boolean allEpisodesViewed = true;
            for (Episode episode : season.getEpisodes()) {
                if (!episode.wasSeen()) {
                    allEpisodesViewed = false;
                    break;
                }
            }

            isSeasonViewed.setChecked(allEpisodesViewed);

            isSeasonViewed.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (isSeasonViewed.isChecked()) {
                        seriesProvider().markSeasonAsSeen(season);
                    } else {
                        seriesProvider().markSeasonAsNotSeen(season);
                    }

                }
            });

            return itemView;
        }

        @Override
        public void onUnfollowing(Series series) {/* Not my business */
        }

        @Override
        public void onFollowing(Series series) {/* Not my business */
        }

        @Override
        public void onMarkedAsSeen(Episode episode) {
            final String seriesId = episode.getSeriesId();
            final int seasonNumber = episode.getSeasonNumber();

            final Season season = seriesProvider().getSeries(seriesId).getSeasons()
                    .getSeason(seasonNumber);

            if (season.areAllViewed()) {
                this.remove(season);
                this.add(season);
                this.sort(comparator);
            }
        }

        @Override
        public void onMarkedAsNotSeen(Episode episode) {
            final String seriesId = episode.getSeriesId();
            final int seasonNumber = episode.getSeasonNumber();

            final Season season = seriesProvider().getSeries(seriesId).getSeasons()
                    .getSeason(seasonNumber);

            this.remove(season);
            this.add(season);
            this.sort(comparator);
        }

        @Override
        public void onMarkedAsSeen(Season season) {
            this.remove(season);
            this.add(season);
            this.sort(comparator);
        }

        @Override
        public void onMarkedAsNotSeen(Season season) {
            this.remove(season);
            this.add(season);
            this.sort(comparator);
        }
    }

    private void populateSeasonsList() {

        this.dataAdapter = new SeasonItemViewAdapter(this, R.layout.season_list_item, this.series
                .getSeasons().toList());
        this.setListAdapter(this.dataAdapter);
        this.dataAdapter.sort(comparator);
    }

    /**
     * @return the app's series provider
     */
    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }
}
