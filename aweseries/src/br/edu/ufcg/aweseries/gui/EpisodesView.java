package br.edu.ufcg.aweseries.gui;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class EpisodesView extends Activity {
    private ListView episodesList;
    private Season season;
    private Series series;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.listing);

        final Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            final String seriesId = extras.getString("series id");
            this.series = this.seriesProvider().getSeries(seriesId);
            final int seasonNumber = extras.getInt("season number");

            this.season = this.series.getSeasons().getSeason(seasonNumber);
        }

        final TextView listingTitle = (TextView) this.findViewById(R.id.listingTitleTextView);

        listingTitle.setText(this.series.getName()
                + String.format(" S%02d Episodes", this.season.getNumber()));
        this.populateEpisodesList();

    }

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> {
        public EpisodeItemViewAdapter(Context context, int episodesItemResourceId,
                List<Episode> list) {
            super(context, episodesItemResourceId, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater vi = (LayoutInflater) EpisodesView.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = vi.inflate(R.layout.episode_list_item, null);
            }

            final TextView nameTextView = (TextView) itemView
                    .findViewById(R.id.episodeNameTextView);
            final TextView numberTextView = (TextView) itemView
                    .findViewById(R.id.episodeNumberTextView);
            final TextView dateTextView = (TextView) itemView
                    .findViewById(R.id.episodeDateTextView);
            final TextView isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            // load episode data
            final String episodeName = this.getItem(position).getName();
            final String episodeNumberRep = String.format("Episode %02d", this.getItem(position)
                    .getNumber());
            if ((episodeName != null) && !episodeName.trim().isEmpty()) {
                nameTextView.setText(episodeName);
                numberTextView.setText(episodeNumberRep);
            }

            else {
                nameTextView.setText(episodeNumberRep);
                numberTextView.setText(episodeNumberRep);
            }

            if (this.getItem(position).getFirstAired() != null) {
                dateTextView.setText(this.getItem(position).getFirstAired());
            }

            isViewedCheckBox.setPressed(this.getItem(position).isViewed());

            return itemView;
        }

    }

    private void populateEpisodesList() {
        this.episodesList = (ListView) this.findViewById(R.id.listView);
        this.episodesList.setAdapter(new EpisodeItemViewAdapter(this, R.layout.episode_list_item,
                this.season.getEpisodes()));
    }

    /**
     * @return the app's series provider
     */
    private SeriesProvider seriesProvider() {
        return App.environment().getSeriesProvider();
    }
}
