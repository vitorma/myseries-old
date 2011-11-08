package br.edu.ufcg.aweseries.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.model.Series;

/**
 * Search view. Allows user to find a series by its name and start/stop following it.
 */
public class SeriesSearchView extends ListActivity {

    private class FollowSeriesTask extends AsyncTask<Series, Void, Void> {
        private String followMessage;

        @Override
        protected Void doInBackground(Series... params) {
            final Series series = params[0];

            final boolean userFollowsSeries = App.environment().seriesProvider().follows(series);
            if (userFollowsSeries) {
                App.environment().seriesProvider().unfollow(series);
                this.followMessage = String.format(
                        SeriesSearchView.this.getString(R.string.you_no_longer_follow),
                        series.getName());
            } else {
                App.environment().seriesProvider().follow(series);
                this.followMessage = String.format(
                        SeriesSearchView.this.getString(R.string.now_you_follow_series),
                        series.getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            final Toast toast = Toast.makeText(SeriesSearchView.this, this.followMessage,
                    Toast.LENGTH_LONG);
            toast.show();
        }

    };

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.search);

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
            public void onClick(final View v) {
                final AutoCompleteTextView searchField = (AutoCompleteTextView) SeriesSearchView.this
                        .findViewById(R.id.searchField);

                try {
                    final Series[] searchResultsArray = App.environment().seriesProvider()
                            .searchSeries(searchField.getText().toString());

                    SeriesSearchView.this.setListAdapter(new TextOnlyViewAdapter(
                            SeriesSearchView.this, SeriesSearchView.this,
                            R.layout.text_only_list_item, searchResultsArray));

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
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /** Current selected item. */
            private Series selectedItem;

            /** Custom dialog to show the overview of a series. */
            private Dialog dialog;

            private boolean userFollowsSeries = false;

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
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
                    public void onClick(final View v) {
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

                this.userFollowsSeries = App.environment().seriesProvider()
                        .follows(this.selectedItem);

                if (this.userFollowsSeries) {
                    followButton.setText(R.string.unfollowSeries);
                } else {
                    followButton.setText(R.string.followSeries);
                }

                followButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        String message;
                        if (userFollowsSeries) {
                            userFollowsSeries = false;
                            followButton.setText(R.string.followSeries);
                            message = String.format(SeriesSearchView.this
                                    .getString(R.string.series_will_be_removed), selectedItem
                                    .getName());
                        } else {
                            userFollowsSeries = true;
                            followButton.setText(R.string.unfollowSeries);
                            message = String.format(
                                    SeriesSearchView.this.getString(R.string.series_will_be_added),
                                    selectedItem.getName());
                        }

                        final Series[] params = { selectedItem };
                        new FollowSeriesTask().execute(params);

                        dialog.dismiss();

                        final Toast toast = Toast.makeText(SeriesSearchView.this, message,
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });

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
    public final boolean onSearchRequested() {
        final AutoCompleteTextView searchField = (AutoCompleteTextView) SeriesSearchView.this
                .findViewById(R.id.searchField);
        searchField.requestFocus();
        searchField.selectAll();
        return true;
    }

    @Override
    public final void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.onSearchRequested();
    }
}
