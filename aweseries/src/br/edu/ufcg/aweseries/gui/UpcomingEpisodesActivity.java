package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.SeriesProviderListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class UpcomingEpisodesActivity extends ListActivity {

    private static final EpisodeComparator comparator = new EpisodeComparator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_without_toolbar);
        this.populateView();
        //        this.setupItemClickListener();
    }

    private void populateView() {
        this.setUpContentView();
        this.setUpListAdapter();
    }

    private void setUpContentView() {
        final TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText("No episodes");
    }

    private void setUpListAdapter() {
        EpisodeItemViewAdapter dataAdapter = new EpisodeItemViewAdapter(this,
                R.layout.episode_list_item, this.seriesProvider().upcoming());
        dataAdapter.sort(comparator);
        this.setListAdapter(dataAdapter);
    }

    /*
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
    */

    private SeriesProvider seriesProvider() {
        return App.environment().seriesProvider();
    }

    //Episode comparator------------------------------------------------------------------------------------------------

    private static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode episodeA, Episode episodeB) {
            return episodeB.compareByDateTo(episodeA);
        }
    };

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements
            SeriesProviderListener {

        public EpisodeItemViewAdapter(Context context, int episodeItemResourceId,
                List<Episode> objects) {
            super(context, episodeItemResourceId, objects);
            seriesProvider().addListener(this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) UpcomingEpisodesActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.episode_list_item, null);
            }

            // get views for the episodes fields
            final TextView nameTextView = (TextView) itemView
                    .findViewById(R.id.episodeNameTextView);
            final TextView numberTextView = (TextView) itemView
                    .findViewById(R.id.episodeNumberTextView);
            final TextView dateTextView = (TextView) itemView
                    .findViewById(R.id.episodeDateTextView);
            final CheckBox isViewedCheckBox = (CheckBox) itemView
                    .findViewById(R.id.episodeIsViewedCheckBox);

            // load episode data
            final Episode episode = this.getItem(position);
            nameTextView.setText(episode.getName());
            numberTextView.setText(String.format("Episode %02d", episode.getNumber()));
            dateTextView.setText(episode.getFirstAiredAsString());
            isViewedCheckBox.setChecked(episode.wasSeen());

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        seriesProvider().markEpisodeAsSeen(episode);
                    } else {
                        seriesProvider().markEpisodeAsNotSeen(episode);
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
            this.remove(episode);
        }

        @Override
        public void onMarkedAsNotSeen(Episode episode) {
            this.add(episode);
            this.sort(comparator);
        }

        @Override
        public void onMarkedAsSeen(Season season) {
            for (Episode e : season.getEpisodes()) {
                this.remove(e);
            }

            for (Episode e : season.getEpisodes()) {
                this.add(e);
            }

            this.sort(comparator);
        }

        @Override
        public void onMarkedAsNotSeen(Season season) {
            for (Episode e : season.getEpisodes()) {
                this.remove(e);
            }
        }
    }
}
