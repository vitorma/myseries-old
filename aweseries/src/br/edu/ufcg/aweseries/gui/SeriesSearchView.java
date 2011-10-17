package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.model.Series;

public class SeriesSearchView extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.search);

        this.listView = (ListView) SeriesSearchView.this.findViewById(R.id.searchResultsListView);

        this.setupSearchButtonClickListener();
        this.setupItemClickListener();
        this.setupSearchFieldReturnListener();
    }

    private void setupSearchFieldReturnListener() {
        final AutoCompleteTextView searchField = (AutoCompleteTextView) SeriesSearchView.this
                .findViewById(R.id.searchField);
            
    }

    /**
     * Sets up a listener to clicks at search button events.
     */
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

                    resultsListView.setAdapter(new TextOnlyViewAdapter(SeriesSearchView.this,
                            SeriesSearchView.this, R.layout.list_item, searchResultsArray));

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

    /**
     * Sets up a listener to item click events.
     */
    private void setupItemClickListener() {
        this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(SeriesSearchView.this);
                dialog.setContentView(R.layout.series_overview_dialog);

                final TextView seriesOverview = (TextView) dialog
                        .findViewById(R.id.overviewTextView);
                final Button backButton = (Button) dialog.findViewById(R.id.backButton);

                backButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        dialog.dismiss();
                    }
                });

                final Series selectedItem = (Series) parent.getItemAtPosition(position);

                dialog.setTitle(selectedItem.getName());
                seriesOverview.setText(selectedItem.getOverview());

                dialog.show();

            }
        });
    }
    
    @Override
    public boolean onSearchRequested() {
        final AutoCompleteTextView searchField = (AutoCompleteTextView) SeriesSearchView.this
                .findViewById(R.id.searchField);
        searchField.requestFocus();
        searchField.selectAll();
        return true;
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.onSearchRequested();
    }
}
