package br.edu.ufcg.aweseries.gui;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.SeriesProviderListener;
import br.edu.ufcg.aweseries.model.Series;

public class MySeries extends ListActivity {
    private MySeriesViewAdapter dataAdapter;

    //View Adapter----------------------------------------------------------------------------------

    private class MySeriesViewAdapter extends ArrayAdapter<Series>
            implements SeriesProviderListener {
        private final SeriesProvider seriesProvider = App.environment().seriesProvider();

        public MySeriesViewAdapter(
                Context context, int seriesItemResourceId, List<Series> objects) {
            super(context, seriesItemResourceId, objects);
            this.seriesProvider.addListener(this);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                LayoutInflater li = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.list_item, null);
            }

            // get views for the series fields
            ImageView image = (ImageView) itemView.findViewById(R.id.itemSeriesImage);
            TextView name = (TextView) itemView.findViewById(R.id.itemSeriesName);
            TextView status = (TextView) itemView.findViewById(R.id.itemSeriesStatus);

            // load series data
            Series item = this.getItem(position);
            name.setText(item.getName());
            status.setText(item.getStatus());
            image.setImageBitmap(App.environment().seriesProvider().getPosterOf(item));

            return itemView;
        }

        @Override
        public void onUnfollowing(Series series) {
            this.remove(series);
        }

        @Override
        public void onFollowing(Series series) {
            this.add(series);
        }
    }

    //Interface-------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.my_series);
        this.initAdapter();
        this.setupItemClickListener();
        this.setupItemLongClickListener();
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

    private void initAdapter() {
        this.dataAdapter = new MySeriesViewAdapter(
                this, R.layout.list_item, App.environment().seriesProvider().mySeries());
        this.setListAdapter(this.dataAdapter);
    }

    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), SeriesView.class);
                Series series = (Series) parent.getItemAtPosition(position);
                intent.putExtra("series id", series.getId());
                intent.putExtra("series name", series.getName());
                MySeries.this.startActivity(intent);
            }
        });
    }

    private void setupItemLongClickListener() {
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                    AdapterView<?> parent, View view, int position, long id) {
                MySeries.this.showUnfollowingDialog((Series) parent.getItemAtPosition(position));
                return true;
            }
        });
    }

    private void showUnfollowingDialog(final Series series) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
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
           .setNegativeButton("No", dialogClickListener)
           .show();
    }

    private void showSearchActivity() {
        Intent intent = new Intent(this, SeriesSearchView.class);
        this.startActivity(intent);
    }
}
