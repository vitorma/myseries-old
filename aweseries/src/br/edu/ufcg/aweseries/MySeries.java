package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays current followed series.
 */
public class MySeries extends Activity {
    private final int chuckId = 80348;
    private ListView listView;

    /*
     * (non-Javadoc)
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
        listView.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item,
                mySeries()));
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
                intent.putExtra("api key", "6F2B5A871C96FB05");
                intent.putExtra("series id", chuckId);
                intent.putExtra("series name",
                        ((TextView) parent.getChildAt(position)).getText());
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

    /**
     * Returns an array with the names of all followed series.
     * 
     * @return followed series.
     */
    private String[] mySeries() {
        try {
            String[] series = new String[1];
            series[0] =
                    new TheTVDB("6F2B5A871C96FB05").getSeries(chuckId)
                            .getName();
            return series;
        } catch (Exception e) {
            return new String[] { "" };
        }
    }
}
