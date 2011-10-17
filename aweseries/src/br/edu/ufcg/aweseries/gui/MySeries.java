package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Series;

/**
 * Displays current followed series.
 */
public class MySeries extends Activity {
    private ListView listView;

    //Adapter---------------------------------------------------------------------------------------

    class SeriesItemViewAdapter extends ArrayAdapter<Series> {
        private final SeriesProvider seriesProvider = App.environment().seriesProvider();

        public SeriesItemViewAdapter(Context context, int seriesItemResourceId, Series[] objects) {
            super(context, seriesItemResourceId, objects);
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
            ImageView image = (ImageView) itemView.findViewById(R.id.itemSeriesImage);
            TextView name = (TextView) itemView.findViewById(R.id.itemSeriesName);
            TextView status = (TextView) itemView.findViewById(R.id.itemSeriesStatus);

            // load series data
            Series item = this.getItem(position);
            name.setText(item.getName());
            status.setText(item.getStatus());
            Bitmap poster = this.seriesProvider.getPosterOf(item);
            image.setImageBitmap(poster);

            return itemView;
        }
    }

    //Interface methods-----------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        this.setContentView(R.layout.listing);
        this.setViewTitle();

        // rounded with try-catch block for debbug purposes
        try {
            this.populateListView();
        } catch (Exception e) {
            //TODO A better dialog with a OK button
            new AlertDialog.Builder(this).setMessage(e.getMessage()).create().show();
        }

        this.setupItemClickListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_series_options_menu, menu);
        return true;
        
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSeriesMenuItem:
                showSearchActivity();
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        showSearchActivity();
        return true;
    }

    //Extracted methods-----------------------------------------------------------------------------

    private void setViewTitle() {
        TextView listingTitle = ((TextView) findViewById(R.id.listingTitleTextView));
        listingTitle.setText("My Series");
    }

    private void populateListView() {
        this.listView = (ListView) this.findViewById(R.id.listView);
        this.listView.setAdapter(new SeriesItemViewAdapter(
                this, R.layout.list_item, App.environment().seriesProvider().mySeries()));
    }

    private void setupItemClickListener() {
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(view.getContext(), SeriesView.class);
                intent.putExtra("series id", ((Series) parent.getItemAtPosition(position)).getId());
                intent.putExtra("series name",
                        ((Series) parent.getItemAtPosition(position)).getName());

                try {
                    startActivity(intent);
                } catch (Exception e) {
                    TextView tv = (TextView) MySeries.this.findViewById(R.id.seasonsButton);
                    tv.setText(e.getClass() + " " + e.getMessage());
                }
            }
        });
    }

    private void showSearchActivity() {
        Intent intent = new Intent(this, SeriesSearchView.class);

        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e("MySeries", "Unable to start search activity"); //XXX
        }
    }
}
