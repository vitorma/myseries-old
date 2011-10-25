package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
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
public class SeasonsView extends Activity {
    private ListView seasonsList;
    private Series series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listing);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String seriesId = extras.getString("series id");
            this.series = seriesProvider().getSeries(seriesId);
        }

        // set view title
        TextView listingTitle = (TextView) findViewById(R.id.listingTitleTextView);
        listingTitle.setText(this.series.getName() + "'s Seasons");

        populateSeasonsList();
        setupItemClickListener();
    }

    private void setupItemClickListener() {
        this.seasonsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(view.getContext(), EpisodesView.class);

                intent.putExtra("season number",
                        ((Season) parent.getItemAtPosition(position)).getNumber());
                intent.putExtra("series id", series.getId());

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.e(SeasonsView.class.getName(),
                            "Unable to start intent: " + intent.toString() + "\n");
                    for (StackTraceElement el : e.getStackTrace()) {
                        Log.e(SeasonsView.class.getName(),
                                "\t" + el.getClassName() + ": " + el.getMethodName() + " - "
                                        + el.getLineNumber());
                    }
                }
            }
        });

    }

    //XXX: Use TextViewAdapter instead.
    private class SeasonItemViewAdapter extends ArrayAdapter<Season> implements
            SeriesProviderListener {
        public SeasonItemViewAdapter(Context context, int seasonsItemResourceId, Season[] objects) {
            super(context, seasonsItemResourceId, objects);
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

            final CheckBox isSeasonViewed = (CheckBox) itemView.findViewById(R.id.isSeasonViewedCheckBox);
            
            boolean allEpisodesViewed = true;
            for (Episode episode : season.getEpisodes()) {
                if (!episode.isViewed()) {
                    allEpisodesViewed = false;
                    break;
                }
            }
            
            isSeasonViewed.setChecked(allEpisodesViewed);
            
            isSeasonViewed.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (isSeasonViewed.isChecked()) {
                        seriesProvider().markSeasonAsViewed(season);
                    } else {
                        seriesProvider().markSeasonAsNotViewed(season);
                    }

                }
            });

            return itemView;
        }

        @Override
        public void onUnfollowing(Series series) {/* Not my business */}

        @Override
        public void onFollowing(Series series) {/* Not my business */}

        @Override
        public void onEpisodeMarkedAsViewed(Episode episode) {
            // TODO Implement

        }

        @Override
        public void onEpisodeMarkedAsNotViewed(Episode episode) {
            // TODO Implement

        }

        @Override
        public void onSeasonMarkedAsViewed(Season season) {
            // TODO Implement

        }

        @Override
        public void onSeasonMarkedAsNotViewed(Season season) {
            // TODO Implement

        }

    }

    private void populateSeasonsList() {
        this.seasonsList = (ListView) findViewById(R.id.listView);
        this.seasonsList.setAdapter(new SeasonItemViewAdapter(this, R.layout.list_item, this.series
                .getSeasons().toArray()));
    }

    /**
     * @return the app's series provider
     */
    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }
}
