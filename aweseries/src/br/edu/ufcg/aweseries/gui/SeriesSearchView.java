package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Series;

public class SeriesSearchView extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.search);

        this.setupSearchButtonClickListener();
    }

    private void setupSearchButtonClickListener() {
        final ImageButton searchButton = (ImageButton) this.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AutoCompleteTextView searchField = (AutoCompleteTextView) SeriesSearchView.this
                        .findViewById(R.id.searchField);

                final ListView resultsListView = (ListView) SeriesSearchView.this
                        .findViewById(R.id.searchResultsListView);

                try {
                    final Series[] searchResultsArray = App.environment().seriesProvider()
                    .searchSeries(searchField.getText().toString());

                    resultsListView.setAdapter(new SeriesItemViewAdapter(SeriesSearchView.this,
                            R.layout.list_item, searchResultsArray));

                } catch (final Exception e) {
                    Log.e(SeriesSearchView.class.getName(), e.getMessage());
                    final AlertDialog.Builder builder = new AlertDialog.Builder(
                            SeriesSearchView.this);
                    builder.setMessage(e.getMessage());
                    builder.create().show();
                }
            }
        });
    }

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
                final LayoutInflater vi = (LayoutInflater) SeriesSearchView.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = vi.inflate(R.layout.list_item, null);
            }

            // get views for the series fields
            final ImageView image = (ImageView) itemView.findViewById(R.id.itemSeriesImage);
            final TextView name = (TextView) itemView.findViewById(R.id.itemSeriesName);
            final TextView status = (TextView) itemView.findViewById(R.id.itemSeriesStatus);

            // load series data
            final Series item = this.getItem(position);

            name.setText(item.getName());
            status.setText(item.getStatus());

            final Bitmap poster = this.seriesProvider.getPosterOf(item);
            image.setImageBitmap(poster);

            return itemView;
        }
    }

}
