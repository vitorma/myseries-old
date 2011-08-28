package br.edu.ufcg.aweseries;

import br.edu.ufcg.aweseries.thetvdb.Series;
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
    private final int tbbtId = 80379;
    private final int gotID = 121361;
    private final int houseID = 73255;

    private final String apiKey = "6F2B5A871C96FB05";
    private final TheTVDB db = new TheTVDB(apiKey);
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
                
                intent.putExtra("api key", apiKey);
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

    /**
     * Returns an array with the names of all followed series.
     * 
     * @return followed series.
     */
    private Series[] mySeries() {
        try {
            Series[] series = new Series[4];
            series[0] = db.getSeries(chuckId);
            series[1] = db.getSeries(gotID);
            series[2] = db.getSeries(houseID);
            series[3] = db.getSeries(tbbtId);
            
            return series;
        } catch (Exception e) {
            return new Series[] { };
        }
    }
}
