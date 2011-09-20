package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.Environment;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.thetvdb.Season;
import br.edu.ufcg.aweseries.thetvdb.Series;

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
        populateSeasonsList();
    }

    private class SeasonItemViewAdapter extends ArrayAdapter<Season> {
        public SeasonItemViewAdapter(Context context,
                int seasonsItemResourceId, Season[] objects) {
            super(context, seasonsItemResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                LayoutInflater vi =
                        (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = vi.inflate(R.layout.list_item, null);
            }

            // get views for the series fields
            // ImageView image =
            // (ImageView) itemView.findViewById(R.id.itemSeriesImage);
            TextView name =
                    (TextView) itemView.findViewById(R.id.itemSeriesName);

            // load series data
            name.setText(this.getItem(position).toString());

            return itemView;
        }

    }

    private void populateSeasonsList() {
        this.seasonsList = (ListView) findViewById(R.id.listView);
        this.seasonsList.setAdapter(new SeasonItemViewAdapter(this,
        		R.layout.list_item, Environment.instance().getSeriesProvider()
        		.getSeasons(series)));
    }
}
