package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.R.id;
import br.edu.ufcg.aweseries.R.layout;
import br.edu.ufcg.aweseries.thetvdb.Series;

/**
 * Displays current followed series.
 */
public class MySeries extends Activity {

    private final SeriesProvider seriesProvider = new SeriesProvider();
    private ListView listView;

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.my_series);

        populateListView();
        setupItemClickListener();
    }

    /**
     * Fills mySeriesListView with the current followed series.
     */
    private void populateListView() {
        listView = (ListView) this.findViewById(R.id.mySeriesListView);
        listView.setAdapter(new ArrayAdapter<Series>(this, R.layout.list_item,
                this.seriesProvider.mySeries()));
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
                        Integer.parseInt(
                        ((Series) parent.getItemAtPosition(position)).getId()));
                intent.putExtra("series name",
                        ((Series) parent.getItemAtPosition(position)).getName());
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    TextView tv =
                            (TextView) MySeries.this
                                    .findViewById(R.id.textView1);
                    tv.setText(e.getClass() + " " + e.getMessage());
                }
            }
        });
    }
}
