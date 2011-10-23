package br.edu.ufcg.aweseries.gui;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.util.Strings;

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
        this.setupItemClickListener();
    }

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> {
        public EpisodeItemViewAdapter(Context context, int episodesItemResourceId,
                List<Episode> list) {
            super(context, episodesItemResourceId, list);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            final Episode episode = this.getItem(position);

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
            final CheckBox isViewedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.episodeIsViewedCheckBox);

            // load episode data
            final String episodeName = episode.getName();
            final String episodeNumberRep = String.format("Episode %02d", episode.getNumber());
            if ((episodeName != null) && !Strings.isBlank(episodeName)) {
                nameTextView.setText(episodeName);
                numberTextView.setText(episodeNumberRep);
            }

            else {
                nameTextView.setText(episodeNumberRep);
                numberTextView.setText(episodeNumberRep);
            }

            if (episode.getFirstAired() != null) {
                dateTextView.setText(episode.getFirstAired());
            }

            isViewedCheckBox.setChecked(episode.isViewed());
            isViewedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton commandButton, boolean checked) {

                    episode.setViewed(checked);

                    if (checked) {
                        EpisodesView.this.seriesProvider().markEpisodeAsViewed(episode);
                    } else {
                        EpisodesView.this.seriesProvider().markEpisodeAsNotViewed(episode);
                    }

                    Log.d(this.getClass().getName(),
                            String.format("%s viewed: %b", episode.getName(), episode.isViewed()));
                }
            });

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
        return App.environment().seriesProvider();
    }

    /**
     * Sets up a listener to item click events.
     */
    private void setupItemClickListener() {
        this.episodesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), EpisodeView.class);
                intent.putExtra("episode id",
                        ((Episode) parent.getItemAtPosition(position)).getId());
                intent.putExtra("episode name",
                        ((Episode) parent.getItemAtPosition(position)).getName());
                try {
                    EpisodesView.this.startActivity(intent);
                } catch (final Exception e) {
                    new AlertDialog.Builder(EpisodesView.this).setMessage(e.getMessage()).create()
                            .show();
                }
            }
        });
    }
}
