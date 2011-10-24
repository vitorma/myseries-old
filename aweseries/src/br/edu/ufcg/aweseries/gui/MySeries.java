package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import android.R.id;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.SeriesProviderListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;

public class MySeries extends ListActivity {
    private MySeriesViewAdapter dataAdapter;
    private final static SeriesComparator comparator = new SeriesComparator();

    //Series Comparator

    private static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series seriesA, Series seriesB) {
            return seriesA.getName().compareTo(seriesB.getName());
        }
    }

    //View Adapter----------------------------------------------------------------------------------

    private class MySeriesViewAdapter extends ArrayAdapter<Series> implements
            SeriesProviderListener {
        private final SeriesProvider seriesProvider = App.environment().seriesProvider();

        public MySeriesViewAdapter(Context context, int seriesItemResourceId, List<Series> objects) {
            super(context, seriesItemResourceId, objects);
            this.seriesProvider.addListener(this);
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
            final ImageView image = (ImageView) itemView.findViewById(R.id.itemSeriesImage);
            final TextView name = (TextView) itemView.findViewById(R.id.itemSeriesName);
            final TextView status = (TextView) itemView.findViewById(R.id.itemSeriesStatus);
            final TextView network = (TextView) itemView.findViewById(R.id.networkTextView);
            final TextView airTime = (TextView) itemView.findViewById(R.id.airTimeTextView);
            final TextView nextToView = (TextView) itemView.findViewById(R.id.nextToViewTextView);
            final TextView latestToAirs = (TextView) itemView
                    .findViewById(R.id.latestToAirsTextView);

            // load series data
            final Series item = this.getItem(position);
            image.setImageBitmap(App.environment().seriesProvider().getPosterOf(item));
            name.setText(item.getName());
            nextToView.setText(item.getSeasons().getNextEpisodeToView().toString());
            latestToAirs.setText(item.getSeasons().getLatestEpisodeToAirs().toString());
            status.setText(item.getStatus());
            network.setText(item.getNetwork());
            airTime.setText(item.getAirsDayAndTime());

            return itemView;
        }
        
        @Override
        public void onUnfollowing(Series series) {
            this.remove(series);
        }

        @Override
        public void onFollowing(Series series) {
            this.add(series);
            this.sort(comparator);
        }
        
        @Override
        public void onEpisodeMarkedAsViewed(Episode episode) {
            Series series = seriesProvider.getSeries(episode.getSeriesId());
            this.remove(series);
            this.add(series);
            this.sort(comparator);
        }

        @Override
        public void onEpisodeMarkedAsNotViewed(Episode episode) {
            Series series = seriesProvider.getSeries(episode.getSeriesId());
            this.remove(series);
            this.add(series);
            this.sort(comparator);
        }
    }

    //Interface-------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.list_activity_layout);
        this.adjustContentView();
        this.initAdapter();
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

    //Private---------------------------------------------------------------------------------------

    private void adjustContentView() {
        final TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText("My Series");
        
        final TextView empty = (TextView) this.findViewById(id.empty);
        empty.setText("No series followed");
    }

    private void initAdapter() {
        this.dataAdapter = new MySeriesViewAdapter(this, R.layout.list_item, App.environment()
                .seriesProvider().mySeries());
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
                        App.environment().seriesProvider().unfollow(series);
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want unfollow " + series.getName() + "?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchView.class);
        this.startActivity(intent);
    }
}
