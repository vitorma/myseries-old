package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import android.R.id;
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

public class EpisodesView extends ListActivity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final EpisodeComparator comparator = new EpisodeComparator();

    private Series series;
    private Season season;
    private EpisodeItemViewAdapter dataAdapter;

    //Episode comparator------------------------------------------------------------------------------------------------

    private static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode episodeA, Episode episodeB) {
            return episodeA.getNumber() - episodeB.getNumber();
        }
    };

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements SeriesProviderListener {

        public EpisodeItemViewAdapter(Context context, int episodeItemResourceId, List<Episode> objects) {
            super(context, episodeItemResourceId, objects);
            seriesProvider.addListener(this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li =
                    (LayoutInflater) EpisodesView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.episode_list_item, null);
            }

            // get views for the episodes fields
            final TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
            final TextView numberTextView = (TextView) itemView.findViewById(R.id.episodeNumberTextView);
            final TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
            final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            // load episode data
            final Episode episode = this.getItem(position);
            nameTextView.setText(episode.getName());
            numberTextView.setText(String.format("Episode %02d", episode.getNumber()));
            dateTextView.setText(episode.getFirstAired());
            isViewedCheckBox.setChecked(episode.isViewed());

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        seriesProvider.markEpisodeAsViewed(episode);
                    } else {
                        seriesProvider.markEpisodeAsNotViewed(episode);
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
            this.remove(episode);
            this.add(episode);
            this.sort(comparator);
        }

        @Override
        public void onEpisodeMarkedAsNotViewed(Episode episode) {
            this.remove(episode);
            this.add(episode);
            this.sort(comparator);
        }

        @Override
        public void onSeasonMarkedAsViewed(Season season) {/* Not my business */}

        @Override
        public void onSeasonMarkedAsNotViewed(Season season) {/* Not my business */}
    }

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_with_checkbox);
        this.populateView();
        this.setupItemClickListener();
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void getExtras() {
        final Bundle extras = this.getIntent().getExtras();
        this.series = seriesProvider.getSeries(extras.getString("series id"));
        this.season = this.series.getSeasons().getSeason(extras.getInt("season number"));
    }

    private void adjustContentView() {
        final TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText(this.series.getName());

        final CheckBox isSeasonViewed = (CheckBox) this.findViewById(R.id.isSeasonViewedCheckBox);
        isSeasonViewed.setChecked(this.season.areAllViewed());

        final TextView seasonName = (TextView) this.findViewById(R.id.seasonTextView);
        seasonName.setText(this.season.toString());
        
        final TextView empty = (TextView) this.findViewById(id.empty);
        empty.setText("No episodes");
    }

    private void setAdapter() {
        this.dataAdapter = new EpisodeItemViewAdapter(this, R.layout.episode_list_item, this.season.getEpisodes());
        this.setListAdapter(this.dataAdapter);
    }

    private void populateView() {
        this.getExtras();
        this.adjustContentView();
        this.setAdapter();
        this.dataAdapter.sort(comparator);
    }

    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), EpisodeView.class);
                final Episode episode = (Episode) parent.getItemAtPosition(position);
                intent.putExtra("episode id", episode.getId());
                EpisodesView.this.startActivity(intent);
            }
        });
    }
}
