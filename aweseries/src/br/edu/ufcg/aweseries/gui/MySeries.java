package br.edu.ufcg.aweseries.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.SeriesProviderListener;
import br.edu.ufcg.aweseries.model.DomainEntityListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;

public class MySeries extends ListActivity {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final SeriesComparator comparator = new SeriesComparator();

    private SeriesItemViewAdapter dataAdapter;

    //Series comparator-------------------------------------------------------------------------------------------------

    private static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series seriesA, Series seriesB) {
            return seriesA.getName().compareTo(seriesB.getName());
        }
    }

    //Series item view adapter------------------------------------------------------------------------------------------

    private class SeriesItemViewAdapter extends ArrayAdapter<Series> implements
            DomainEntityListener<Series> {

        public SeriesItemViewAdapter(Context context, int seriesItemResourceId, List<Series> objects) {
            super(context, seriesItemResourceId, objects);
            for (Series series : objects) {
                series.addListener(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) MySeries.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.my_series_list_item, null);
            }

            // get views for the series fields
            final ImageView image = (ImageView) itemView.findViewById(R.id.seriesImageView);
            final TextView name = (TextView) itemView.findViewById(R.id.nameTextView);
            final TextView status = (TextView) itemView.findViewById(R.id.statusTextView);
            final TextView network = (TextView) itemView.findViewById(R.id.networkTextView);
            final TextView airTime = (TextView) itemView.findViewById(R.id.airTimeTextView);
            final TextView nextToSee = (TextView) itemView.findViewById(R.id.nextToSeeTextView);
            final TextView latestToAir = (TextView) itemView.findViewById(R.id.latestToAirTextView);
            final TextView latestToAirLabel = (TextView) itemView
                    .findViewById(R.id.latestToAirLabelTextView);

            // load series data
            final Series item = this.getItem(position);
            image.setImageBitmap(seriesProvider.getPosterOf(item));
            name.setText(item.getName());

            status.setText(item.getStatus());
            network.setText(item.getNetwork());
            airTime.setText(item.getAirsDayAndTime());

            // next episode to see
            final Episode nextEpisodeToSee = item.getSeasons().getNextEpisodeToSee();
            if (nextEpisodeToSee != null) {
                nextToSee.setText(nextEpisodeToSee.toString());
            } else {
                nextToSee.setText(R.string.upToDate);
            }

            // latest episode to air
            if (item.isContinuing()) {
                latestToAirLabel.setText(R.string.nextEpisodeToAir);
                final Episode nextEpisodeToAir = item.getSeasons().getNextEpisodeToAir();
                if (nextEpisodeToAir != null) {
                    latestToAir.setText(nextEpisodeToAir.toString());
                } else {
                    latestToAir.setText(R.string.upToDate);
                }
            }

            if (item.isEnded()) {
                latestToAirLabel.setText(R.string.lastEpisodeAired);
                final Episode e = item.getSeasons().getLastAiredEpisode();
                if (e != null) {
                    latestToAir.setText(e.toString());
                } else {
                    latestToAir.setText(R.string.noEpisodeAired);
                }
            }

            return itemView;
        }

        @Override
        public void onUpdate(Series series) {
            this.remove(series);
            this.add(series);
            this.sort(comparator);
        }
    }

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list);
        this.adjustContentView();
        this.setAdapter();
        this.setUpRecentAndUpcomingButtonClickListener();
        this.setupItemClickListener();
        this.setupItemLongClickListener();
        this.dataAdapter.sort(comparator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.my_series_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSeriesMenuItem:
                this.showSearchActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void adjustContentView() {
        final TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText("My Series");

        final TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText("No series followed");
    }

    private void setAdapter() {
        this.dataAdapter = new SeriesItemViewAdapter(this, R.layout.my_series_list_item,
                new ArrayList<Series>(seriesProvider.followedSeries()));
        this.setListAdapter(this.dataAdapter);
    }

    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), SeriesView.class);
                final Series series = (Series) parent.getItemAtPosition(position);
                intent.putExtra("series id", series.getId());
                intent.putExtra("series name", series.getName());
                MySeries.this.startActivity(intent);
            }
        });
    }

    private void setupItemLongClickListener() {
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MySeries.this.showUnfollowingDialog((Series) parent.getItemAtPosition(position));
                return true;
            }
        });
    }

    private void showUnfollowingDialog(final Series series) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        seriesProvider.unfollow(series);
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(
                        String.format(getString(R.string.doYouWantToStopFollowing),
                                series.getName()))
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchView.class);
        this.startActivity(intent);
    }

    private void setUpRecentAndUpcomingButtonClickListener() {
        ImageButton recentAndUpcomingEpisodesButton
                = (ImageButton) this.findViewById(R.id.recentAndUpcomingEpisodesButton);

        recentAndUpcomingEpisodesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MySeries.this.showRecentAndUpcomingEpisodesActivity();
            }
        });
    }

    private void showRecentAndUpcomingEpisodesActivity() {
        final Intent intent = new Intent(this, RecentAndUpcomingEpisodesActivity.class);
        this.startActivity(intent);
    }
}
