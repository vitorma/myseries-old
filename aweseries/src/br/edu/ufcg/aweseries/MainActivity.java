package br.edu.ufcg.aweseries;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import br.edu.ufcg.aweseries.thetvdb.TheTVDB;

public class MainActivity extends Activity {
    private TextView seriesTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.seriesTextView = (TextView) this.findViewById(R.id.series_textview);
        try {
            this.seriesTextView.setText(new TheTVDB("6F2B5A871C96FB05").getSeries(80348).getName());
        } catch (Exception e) {
            this.seriesTextView.setText(e.getMessage());
        }
    }
}