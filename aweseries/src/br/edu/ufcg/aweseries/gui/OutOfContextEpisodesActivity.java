package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.DomainEntityListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * An abstract activity for showing a list of episodes out of their series context, as happens
 * when we come through Series->Season->Episodes.
 */
public abstract class OutOfContextEpisodesActivity extends ListActivity {

    protected abstract List<Episode> episodes();
    protected abstract Comparator<Episode> episodesComparator();

    private static final int LIST_VIEW_RESOURCE_ID = R.layout.list_without_toolbar;
    private static final String EMPTY_LIST_TEXT = "No episodes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(LIST_VIEW_RESOURCE_ID);

        this.setUpContentView();
        this.setUpListAdapter();

        this.setUpEpisodeItemClickListener();
    }

    private void setUpContentView() {
        TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText(EMPTY_LIST_TEXT);
    }

    private void setUpListAdapter() {
        EpisodeItemViewAdapter dataAdapter = new EpisodeItemViewAdapter(this, this.episodes());
        dataAdapter.sort(this.episodesComparator());
        this.setListAdapter(dataAdapter);
    }

    private void setUpEpisodeItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), EpisodeView.class);
                Episode episode = (Episode) parent.getItemAtPosition(position);
                intent.putExtra("series id", episode.getSeriesId());
                intent.putExtra("season number", episode.getSeasonNumber());
                intent.putExtra("episode number", episode.getNumber());
                startActivity(intent);
            }
        });
    }

    //Episode item view adapter-----------------------------------------------------------------------------------------

    private class EpisodeItemViewAdapter extends ArrayAdapter<Episode> implements DomainEntityListener<Episode> {

        private static final int EPISODE_ITEM_RESOURCE_ID = R.layout.episode_alone_list_item;

        private final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

        public EpisodeItemViewAdapter(Context context, List<Episode> objects) {
            super(context, EPISODE_ITEM_RESOURCE_ID, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = this.itemViewFrom(convertView);

            // load episode data
            Episode episode = this.getItem(position);
            Series series = this.SERIES_PROVIDER.getSeries(episode.getSeriesId());
            Season season = series.getSeasons().getSeason(episode.getSeasonNumber());

            // listening to episode updates
            episode.addListener(this);

            this.showData(episode, season, series, itemView);
            this.setUpSeenEpisodeCheckBoxListener(episode, itemView);

            return itemView;
        }

        private View itemViewFrom(View convertView) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li =
                    (LayoutInflater) OutOfContextEpisodesActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(EPISODE_ITEM_RESOURCE_ID, null);
            }

            return itemView;
        }

        private void showData(Episode episode, Season season, Series series, View itemView) {
            TextView nameTextView = (TextView) itemView.findViewById(R.id.episodeNameTextView);
            TextView seriesTextView = (TextView) itemView.findViewById(R.id.episodeSeriesTextView);
            TextView seasonEpisodeTextView = (TextView) itemView.findViewById(R.id.episodeSeasonEpisodeTextView);
            TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
            CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            nameTextView.setText(episode.getName());
            seriesTextView.setText(series.getName());
            seasonEpisodeTextView.setText(String.format("Season %02d - Episode %02d", season.getNumber(), episode.getNumber()));
            dateTextView.setText(episode.getFirstAiredAsString());
            isViewedCheckBox.setChecked(episode.wasSeen());
        }

        private void setUpSeenEpisodeCheckBoxListener(final Episode episode, View itemView) {
            final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

            isViewedCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (isViewedCheckBox.isChecked()) {
                        EpisodeItemViewAdapter.this.SERIES_PROVIDER.markEpisodeAsSeen(episode);
                    } else {
                        EpisodeItemViewAdapter.this.SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                    }
                }
            });
        }

        @Override
        public void onUpdate(Episode episode) {
            if (episode.wasSeen()) {
                this.remove(episode);
            } else {
                this.add(episode);
                this.sort(OutOfContextEpisodesActivity.this.episodesComparator());
            }
        }
    }
}
