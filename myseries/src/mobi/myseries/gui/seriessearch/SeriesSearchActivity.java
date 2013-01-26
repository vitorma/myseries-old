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

package mobi.myseries.gui.seriessearch;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.search.SearchSeriesListener;
import mobi.myseries.application.search.SearchSeriesService;
import mobi.myseries.application.search.SeriesSearchException;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.gui.shared.ButtonOnClickListener;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import mobi.myseries.gui.shared.MessageLauncher;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class SeriesSearchActivity extends SherlockListActivity {
    private static final SearchSeriesService SEARCH_SERIES_SERVICE = App.searchSeriesService();
    private static final FollowSeriesService FOLLOW_SERIES_SERVICE = App.followSeriesService();

    private StateHolder state;
    private SearchSeriesListener listener;
    private MessageLauncher messageLauncher;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        this.setContentView(R.layout.add_series);

        ActionBar ab = this.getSupportActionBar();
        ab.setTitle(R.string.add_series);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        this.setSupportProgressBarIndeterminateVisibility(false);

        this.setupSearchListener();
        this.setUpClearButtonClickListener();
        this.setUpSearchButtonClickListener();
        this.setupItemClickListener();
        this.setupSearchFieldActionListeners();

        Object retained = this.getLastNonConfigurationInstance();
        if (retained != null && retained instanceof StateHolder) {
            this.state = (StateHolder) retained;
        } else {
            this.state = new StateHolder();
            this.setupMessageLauncher();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.loadState();
    }

    private void setupMessageLauncher() {
        this.messageLauncher = new MessageLauncher(this);
        this.state.messageLauncher = this.messageLauncher;
    }

    private void loadState() {
        this.messageLauncher = this.state.messageLauncher;

        if (this.state.isSearching) {
            if (SEARCH_SERIES_SERVICE.getLastValidSearchResult() != null) {
                this.setupListOnAdapter(SEARCH_SERIES_SERVICE.getLastValidSearchResult());
            }
            this.listener.onStart();
            SEARCH_SERIES_SERVICE.registerListener(this.listener);

        } else {
            if (this.state.seriesFound != null) {
                this.setupListOnAdapter(this.state.seriesFound);

                EditText searchField = (EditText) SeriesSearchActivity.this.findViewById(R.id.searchField);
                TextView numberOfResults = (TextView) SeriesSearchActivity.this.findViewById(R.id.numberOfResults);
                String format = this.getString(R.string.number_of_search_results);
                numberOfResults.setVisibility(View.VISIBLE);
                numberOfResults.setText(String.format(format, this.state.seriesFound.size(), searchField.getText()));
            }

            if (this.state.isShowingDialog){
                    this.state.dialog.show();
            }

            this.messageLauncher.loadState();
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return this.state;
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.messageLauncher.onStop();
        SEARCH_SERIES_SERVICE.deregisterListener(this.listener);
        if (this.state.dialog != null && this.state.dialog.isShowing()) {
            this.state.isShowingDialog = true;
            this.state.dialog.dismiss();
        } else {
            this.state.isSearching = false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            this.finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void setUpClearButtonClickListener() {
        final ImageButton clearButton = (ImageButton) this.findViewById(R.id.clearButton);
        final EditText searchField = (EditText) this.findViewById(R.id.searchField);
        final TextView numberOfResults = (TextView) this.findViewById(R.id.numberOfResults);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchField.setText("");
                numberOfResults.setVisibility(View.INVISIBLE);
                SeriesSearchActivity.this.setListAdapter(null);
                SeriesSearchActivity.this.state.seriesFound = null;
            }
        });
    }

    private void setUpSearchButtonClickListener() {
        final ImageButton searchButton = (ImageButton) this.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SeriesSearchActivity.this.performSearch();
            }
        });
    }

    private void setupSearchListener() {
        final EditText searchField = (EditText) this.findViewById(R.id.searchField);
        final View buttonPanel = this.findViewById(R.id.buttonPanel);
        final TextView numberOfResults = (TextView) this.findViewById(R.id.numberOfResults);
        final String format = this.getString(R.string.number_of_search_results);

        this.listener = new SearchSeriesListener() {

            @Override
            public void onSucess(List<Series> series) {
                SeriesSearchActivity.this.state.seriesFound = series;
                SeriesSearchActivity.this.setupListOnAdapter(series);

                numberOfResults.setVisibility(View.VISIBLE);
                numberOfResults.setText(String.format(format, series.size(), searchField.getText()));
            }

            @Override
            public void onFaluire(Throwable exception) {
                if (exception instanceof SeriesSearchException) {
                    FailureDialogBuilder dialogBuilder = new FailureDialogBuilder(
                            SeriesSearchActivity.this);

                    if (exception.getCause() instanceof ConnectionFailedException) {
                        dialogBuilder.setTitle(R.string.connection_failed_title);
                        dialogBuilder.setMessage(R.string.connection_failed_message);

                    } else if (exception.getCause() instanceof InvalidSearchCriteriaException) {
                        dialogBuilder.setTitle(R.string.invalid_criteria_title);
                        dialogBuilder.setMessage(R.string.invalid_criteria_message);

                    } else if (exception.getCause() instanceof SeriesNotFoundException) {
                        dialogBuilder.setTitle(R.string.no_results_title);
                        dialogBuilder.setMessage(R.string.no_results_message);

                    } else if (exception.getCause() instanceof ParsingFailedException) {
                        dialogBuilder.setTitle(R.string.parsing_failed_title);
                        dialogBuilder.setMessage(R.string.parsing_failed_message);
                    } else if (exception.getCause() instanceof ConnectionTimeoutException){
                        dialogBuilder.setTitle(R.string.connection_timeout_title);
                        dialogBuilder.setMessage(R.string.connection_timeout_message);
                    }
                    Dialog dialog = dialogBuilder.build();
                    dialog.show();
                    SeriesSearchActivity.this.state.dialog = dialog;
                }
            }

            @Override
            public void onStart() {
                SeriesSearchActivity.this.state.isSearching = true;
                SeriesSearchActivity.this.setSupportProgressBarIndeterminateVisibility(true);
                searchField.setEnabled(false);
                buttonPanel.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFinish() {
                SeriesSearchActivity.this.state.isSearching = false;
                SeriesSearchActivity.this.setSupportProgressBarIndeterminateVisibility(false);
                searchField.setEnabled(true);
                buttonPanel.setVisibility(View.VISIBLE);
            }
        };
    }

    private void performSearch() {
        final EditText searchField = (EditText) this.findViewById(R.id.searchField);
        final TextView numberOfResults = (TextView) this.findViewById(R.id.numberOfResults);

        numberOfResults.setVisibility(View.INVISIBLE);
        this.setListAdapter(null);
        this.state.seriesFound = null;

        SEARCH_SERIES_SERVICE.registerListener(this.listener);
        SEARCH_SERIES_SERVICE.search(searchField.getText().toString());
    }

    private void setupListOnAdapter(List<Series> series) {
        ArrayAdapter<Series> adapter = new SeriesSearchItemAdapter(
                SeriesSearchActivity.this, SeriesSearchActivity.this,
                R.layout.seriessearch_item, series);
        SeriesSearchActivity.this.setListAdapter(adapter);
    }

    private void setupSearchFieldActionListeners() {
        final EditText searchField = (EditText) this.findViewById(R.id.searchField);
        final View buttons = this.findViewById(R.id.buttonPanel);
        final TextView numberOfResults = (TextView) this.findViewById(R.id.numberOfResults);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buttons.setVisibility(View.VISIBLE);
                } else {
                    buttons.setVisibility(View.INVISIBLE);
                    numberOfResults.setVisibility(View.INVISIBLE);
                    SeriesSearchActivity.this.setListAdapter(null);
                    SeriesSearchActivity.this.state.seriesFound = null;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                    KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SeriesSearchActivity.this.performSearch();
                    return true;
                }

                return false;
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    SeriesSearchActivity.this.performSearch();
                }
                return false;
            }
        });

    }

    /**
     * Sets up a listener to item click events.
     */
    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    private Series selectedItem;
                    private boolean userFollowsSeries;
                    private Dialog dialog;

                    @Override
                    public void onItemClick(final AdapterView<?> parent,
                            final View view, final int position, final long id) {
                        this.selectedItem = (Series) parent
                                .getItemAtPosition(position);
                        this.userFollowsSeries = FOLLOW_SERIES_SERVICE.follows(this.selectedItem);

                        this.dialog = new ConfirmationDialogBuilder(
                                SeriesSearchActivity.this)
                                .setTitle(this.selectedItem.name())
                                .setMessage(this.selectedItem.overview())
                                .setSurrogateMessage(R.string.overview_unavailable)
                                .setPositiveButton(this.followButtonTextResourceId(),
                                                   this.followButtonClickListener())
                                .setNegativeButton(R.string.back, null).build();

                        this.dialog.show();
                        SeriesSearchActivity.this.state.dialog = this.dialog;
                    }

                    private int followButtonTextResourceId() {
                        return this.userFollowsSeries ? R.string.stop_following
                                : R.string.follow;
                    }

                    private ButtonOnClickListener followButtonClickListener() {
                        return new ButtonOnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                if (userFollowsSeries) {
                                    FOLLOW_SERIES_SERVICE.stopFollowing(selectedItem);
                                } else {
                                    FOLLOW_SERIES_SERVICE.follow(selectedItem);
                                }
                                dialog.dismiss();
                            }
                        };
                    }
                });
    }

    @Override
    public final boolean onSearchRequested() {
        final EditText searchField = (EditText) SeriesSearchActivity.this
                .findViewById(R.id.searchField);
        searchField.requestFocus();
        return true;
    }

    @Override
    public final void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        this.onSearchRequested();
    }

    private static class StateHolder {
        public boolean isSearching;
        Dialog dialog;
        boolean isShowingDialog;
        List<Series> seriesFound;
        MessageLauncher messageLauncher;
    }
}
