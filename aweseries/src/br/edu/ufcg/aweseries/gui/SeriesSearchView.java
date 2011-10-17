package br.edu.ufcg.aweseries.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
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
            /** Current selected item. */
            private Series selectedItem;

            /** Custom dialog to show the overview of a series. */
            private Dialog dialog;

            private boolean userFollowsSeries = false;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                this.selectedItem = (Series) parent.getItemAtPosition(position);

                this.dialog = new Dialog(SeriesSearchView.this);
                this.dialog.setContentView(R.layout.series_overview_dialog);

                this.updateDialogText();
                this.setBackButtonClickListener();
                this.setFollowButtonClickListener();

                this.dialog.show();

            }

            /**
             * Sets a listener to 'Back' button click events.
             */
            private void setBackButtonClickListener() {
                final Button backButton = (Button) this.dialog.findViewById(R.id.backButton);

                backButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }

            /**
             * Sets a listener to 'Follow' button click events.
             */
            // TODO Auto-generated method stub

            private void setFollowButtonClickListener() {
                final Button followButton = (Button) this.dialog.findViewById(R.id.followButton);

                for (final Series s : App.environment().seriesProvider().mySeries()) {
                    if (s.equals(this.selectedItem)) {
                        this.userFollowsSeries = true;
                        break;
                    }
                }

                if (!this.userFollowsSeries) {

                    followButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.environment().seriesProvider().follow(selectedItem);
                        }
                    });

                }

                else {
                    followButton.setText(R.string.unfollowSeries);
                    
                    followButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            App.environment().seriesProvider().unfollow(selectedItem);
                        }
                    });
                }

            }

            /**
             * Updates the overviewTextView and the title of the dialog.
             */
            private void updateDialogText() {
                final TextView seriesOverview = (TextView) this.dialog
                        .findViewById(R.id.overviewTextView);

                this.dialog.setTitle(this.selectedItem.getName());
                seriesOverview.setText(this.selectedItem.getOverview());
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
