/*
 *   SeriesSearchActivity.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.gui;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;

public class SeriesSearchActivity extends SherlockListActivity {
    private final SeriesProvider seriesProvider = App.environment().seriesProvider();

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.search_series);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);


        this.setupSearchButtonClickListener();
        this.setupItemClickListener();
        this.setupSearchFieldActionListeners();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch( item.getItemId() ) {
            case android.R.id.home:
                this.up();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void up() {
        Intent i = new Intent(this, MySeriesActivity.class);
        this.startActivity(i);
    }

    private void setupSearchButtonClickListener() {
        final ImageButton searchButton = (ImageButton) this.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        final EditText searchField = (EditText) SeriesSearchActivity.this
                .findViewById(R.id.searchField);
        try {
            final List<Series> searchResultsArray = App.environment().seriesProvider()
                    .searchSeries(searchField.getText().toString());

            SeriesSearchActivity.this.setListAdapter(new TextOnlyViewAdapter(
                    SeriesSearchActivity.this, SeriesSearchActivity.this,
                    R.layout.text_only_list_item, searchResultsArray));

        } catch (final Exception e) {
            Log.e(SeriesSearchActivity.class.getName(), e.getMessage());
            final AlertDialog.Builder builder = new AlertDialog.Builder(SeriesSearchActivity.this);
            builder.setMessage(e.getMessage());
            builder.create().show();
        }

    }

    private void setupSearchFieldActionListeners() {
        final EditText searchField = (EditText) findViewById(R.id.searchField);

        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    performSearch();
                    return true;
                }

                return false;
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    performSearch();
                }

                return false;
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

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                this.selectedItem = (Series) parent.getItemAtPosition(position);

                this.dialog = new Dialog(SeriesSearchActivity.this);
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

                final boolean userFollowsSeries = seriesProvider.follows(this.selectedItem);

                if (userFollowsSeries) {
                    followButton.setText(R.string.stop_following);
                } else {
                    followButton.setText(R.string.follow);
                }

                followButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (userFollowsSeries) {
                            seriesProvider.unfollow(selectedItem);
                        } else {
                            seriesProvider.follow(selectedItem);

                            String message = String.format(SeriesSearchActivity.this
                                    .getString(R.string.series_will_be_added), selectedItem.name());

                            this.showToastWith(message);
                        }

                        dialog.dismiss();
                    }

                    private void showToastWith(String message) {
                        Toast toast = Toast.makeText(App.environment().context(), message,
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }

            /**
             * Updates the overviewTextView and the title of the dialog.
             */
            private void updateDialogText() {
                TextView seriesOverview = (TextView) this.dialog
                        .findViewById(R.id.overviewTextView);

                this.dialog.setTitle(this.selectedItem.name());
                seriesOverview.setText(this.selectedItem.overview());
            }
        });
    }

    @Override
    public final boolean onSearchRequested() {
        final EditText searchField = (EditText) SeriesSearchActivity.this.findViewById(R.id.searchField);
        searchField.requestFocus();
        return true;
    }

    @Override
    public final void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.onSearchRequested();
    }
}
