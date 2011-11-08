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
import br.edu.ufcg.aweseries.model.DomainEntityListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class EpisodesView extends ListActivity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final EpisodeComparator EPISODE_COMPARATOR = new EpisodeComparator();

    private Series series;
    private Season season;
    private CheckBox isSeasonViewed;

    //Episode comparator------------------------------------------------------------------------------------------------

    private static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode episodeA, Episode episodeB) {
            return episodeA.getNumber() - episodeB.getNumber();
        }
    };

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements DomainEntityListener<Episode> {
        public EpisodeItemViewAdapter(Context context, int episodeItemResourceId, List<Episode> objects) {
            super(context, episodeItemResourceId, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = this.itemViewFrom(convertView);

            Episode episode = this.getItem(position);
            episode.addListener(this);

            this.showEpisodesDataOn(episode, itemView);
            this.setUpSeenEpisodeCheckBoxFor(episode, itemView);

            return itemView;
        }

        private View itemViewFrom(View convertView) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li =
                    (LayoutInflater) EpisodesView.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.episode_list_item, null);
            }

            return itemView;
        }

        private void showEpisodesDataOn(Episode episode, View itemView) {
            TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
            TextView numberTextView = (TextView) itemView.findViewById(R.id.episodeNumberTextView);
            TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
            CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            nameTextView.setText(episode.getName());
            numberTextView.setText(String.format("Episode %02d", episode.getNumber()));
            dateTextView.setText(episode.getFirstAiredAsString());
            isViewedCheckBox.setChecked(episode.wasSeen());
        }

        private void setUpSeenEpisodeCheckBoxFor(final Episode episode, View itemView) {
            final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        seriesProvider.markEpisodeAsSeen(episode);
                    } else {
                        seriesProvider.markEpisodeAsNotSeen(episode);
                    }
                }
            });
        }

        @Override
        public void onUpdate(Episode episode) {
            this.notifyDataSetChanged();
        };
    }

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_with_checkbox);

        this.populateView();

        this.setUpEpisodeItemClickListener();
        this.setUpSeenSeasonCheckBoxClickListener();
        this.setUpListenerForSeason();
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void populateView() {
        this.loadSeason();

        this.setUpListActivityParameters();

        this.loadSeasonDataOnView();

        this.setUpEpisodeListAdapter();
    }

    private void loadSeason() {
        final Bundle extras = this.getIntent().getExtras();

        this.series = seriesProvider.getSeries(extras.getString("series id"));
        this.season = this.series.getSeasons().getSeason(extras.getInt("season number"));
    }

    private void setUpListActivityParameters() {
        TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText(this.series.getName());
        
        TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText("No episodes");
    }

    private void loadSeasonDataOnView() {
        this.isSeasonViewed = (CheckBox) this.findViewById(R.id.isSeasonViewedCheckBox);
        this.isSeasonViewed.setChecked(this.season.areAllSeen());

        TextView seasonName = (TextView) this.findViewById(R.id.seasonTextView);
        seasonName.setText(this.season.toString());
    }
    
    private void setUpEpisodeListAdapter() {
        EpisodeItemViewAdapter dataAdapter = new EpisodeItemViewAdapter(this, R.layout.episode_list_item, this.season.getEpisodes());
        this.setListAdapter(dataAdapter);
        dataAdapter.sort(EPISODE_COMPARATOR);
    }

    private void setUpEpisodeItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode episode = (Episode) parent.getItemAtPosition(position);

                Intent intent = new Intent(view.getContext(), EpisodeView.class);
                intent.putExtra("series id", episode.getSeriesId());
                intent.putExtra("season number", episode.getSeasonNumber());
                intent.putExtra("episode number", episode.getNumber());

                EpisodesView.this.startActivity(intent);
            }
        });
    }

    private void setUpSeenSeasonCheckBoxClickListener() {
        this.isSeasonViewed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isSeasonViewed.isChecked()) {
                    seriesProvider.markSeasonAsSeen(EpisodesView.this.season);
                } else {
                    seriesProvider.markSeasonAsNotSeen(EpisodesView.this.season);
                }
            }
        });
    }

    private void setUpListenerForSeason() {
        this.season.addListener(new DomainEntityListener<Season>() {
            @Override
            public void onUpdate(Season entity) {
                EpisodesView.this.loadSeasonDataOnView();
            }
        });
    }
}
