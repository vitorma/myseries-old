package br.edu.ufcg.aweseries;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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

        Button mySeries = (Button) findViewById(R.id.button1);
        mySeries.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MySeries.class));
            }
        });
    }
}