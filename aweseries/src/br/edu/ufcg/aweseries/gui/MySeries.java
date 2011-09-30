package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import br.edu.ufcg.aweseries.thetvdb.series.Series;

/**
 * Displays current followed series.
 */
public class MySeries extends Activity {

    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.listing);

        // set view title
        TextView listingTitle
            = ((TextView) findViewById(R.id.listingTitleTextView));
        listingTitle.setText("My Series");

        // rounded with try-catch block to debbug purposes
        try {
            populateListView();
        } catch (Exception e) {
            new AlertDialog.Builder(this).setMessage(e.getMessage()).create().show();
        }
        setupItemClickListener();
    }

    class SeriesItemViewAdapter extends ArrayAdapter<Series> {
		private final SeriesProvider seriesProvider = App.environment().getSeriesProvider();
		
		public SeriesItemViewAdapter(Context context, int seriesItemResourceId,
				Series[] objects) {
			super(context, seriesItemResourceId, objects);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;

			// if no view was passed, create one for the item
			if (itemView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

			Bitmap poster = seriesProvider.getSmallPoster(item);
			image.setImageBitmap(poster);

			return itemView;
		}
    }
    /**
     * Fills mySeriesListView with the current followed series.
     */
    private void populateListView() {
        listView = (ListView) this.findViewById(R.id.listView);

        listView.setAdapter(new SeriesItemViewAdapter(this, R.layout.list_item,
                App.environment().getSeriesProvider().mySeries()));
    }

    /**
     * Sets up a listener to item click events.
     */
    private void setupItemClickListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Intent intent = new Intent(view.getContext(), SeriesView.class);
                
                intent.putExtra("series id",
                        ((Series) parent.getItemAtPosition(position)).getId());
                intent.putExtra("series name",
                        ((Series) parent.getItemAtPosition(position)).getName());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    TextView tv =
                            (TextView) MySeries.this
                                    .findViewById(R.id.seasonsTextView);
                    tv.setText(e.getClass() + " " + e.getMessage());
                }
            }
        });
    }
}
