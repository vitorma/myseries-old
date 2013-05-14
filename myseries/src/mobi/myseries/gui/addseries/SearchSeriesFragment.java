/*
 *   SearchSeriesFragment.java
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

package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SearchSeriesListener;
import mobi.myseries.application.search.SeriesSearchException;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockListFragment;

public class SearchSeriesFragment extends SherlockListFragment {

    private SearchSeriesListener searchListener;
    private boolean isSearching;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpSearchListener();
        this.setUpSearchFieldActionListeners();
        this.setUpClearButtonClickListener();
        this.setUpSearchButtonClickListener();
        this.setUpItemClickListener();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.addseries_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        App.searchSeriesService().registerListener(this.searchListener);

        if (this.isSearching) {
            this.searchListener.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        App.searchSeriesService().deregisterListener(this.searchListener);
    }

    private void setUpSearchFieldActionListeners() {
        final EditText searchField = (EditText) this.activity().findViewById(R.id.searchField);
        final View buttons = this.activity().findViewById(R.id.buttonPanel);
        final TextView numberOfResults = (TextView) this.activity().findViewById(R.id.numberOfResults);

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    buttons.setVisibility(View.VISIBLE);
                } else {
                    buttons.setVisibility(View.INVISIBLE);
                    numberOfResults.setVisibility(View.INVISIBLE);
                    SearchSeriesFragment.this.setListAdapter(null);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchField.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SearchSeriesFragment.this.performSearch();
                    return true;
                }

                return false;
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    SearchSeriesFragment.this.performSearch();
                    return true;
                }

                return false;
            }
        });
    }

    private void setUpClearButtonClickListener() {
        final ImageButton clearButton = (ImageButton) this.activity().findViewById(R.id.clearButton);
        final EditText searchField = (EditText) this.activity().findViewById(R.id.searchField);
        final TextView numberOfResults = (TextView) this.activity().findViewById(R.id.numberOfResults);

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                searchField.setText("");
                numberOfResults.setVisibility(View.INVISIBLE);
                SearchSeriesFragment.this.setListAdapter(null);
            }
        });
    }

    private void setUpSearchButtonClickListener() {
        final ImageButton searchButton = (ImageButton) this.activity().findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SearchSeriesFragment.this.performSearch();
            }
        });
    }

    void performSearch() {
        final EditText searchField = (EditText) this.activity().findViewById(R.id.searchField);
        final TextView numberOfResults = (TextView) this.activity().findViewById(R.id.numberOfResults);

        numberOfResults.setVisibility(View.INVISIBLE);
        this.setListAdapter(null);

        App.searchSeriesService().search(searchField.getText().toString());
    }

    private void setUpSearchListener() {
        this.searchListener = this.newSearchListener();
    }

    private SearchSeriesListener newSearchListener() {
        final EditText searchField = (EditText) this.activity().findViewById(R.id.searchField);
        final View buttonPanel = this.activity().findViewById(R.id.buttonPanel);
        final TextView numberOfResults = (TextView) this.activity().findViewById(R.id.numberOfResults);
        final String format = this.getString(R.string.number_of_search_results);

        return new SearchSeriesListener() {
            @Override
            public void onSucess(List<Series> series) {
                SearchSeriesFragment.this.setUpListOnAdapter(series);
                numberOfResults.setVisibility(View.VISIBLE);
                numberOfResults.setText(String.format(format, series.size(), searchField.getText()));
            }

            @Override
            public void onFaluire(Throwable exception) {
                if (exception instanceof SeriesSearchException) {
                    if (exception.getCause() instanceof ConnectionFailedException) {
                        SearchSeriesFragment.this.activity().onSearchFailure(
                                R.string.connection_failed_title,
                                R.string.connection_failed_message);
                    } else if (exception.getCause() instanceof InvalidSearchCriteriaException) {
                        SearchSeriesFragment.this.activity().onSearchFailure(
                                R.string.invalid_criteria_title,
                                R.string.invalid_criteria_message);
                    } else if (exception.getCause() instanceof SeriesNotFoundException) {
                        SearchSeriesFragment.this.activity().onSearchFailure(
                                R.string.no_results_title,
                                R.string.no_results_message);
                    } else if (exception.getCause() instanceof ParsingFailedException) {
                        SearchSeriesFragment.this.activity().onSearchFailure(
                                R.string.parsing_failed_title,
                                R.string.parsing_failed_message);
                    } else if (exception.getCause() instanceof ConnectionTimeoutException){
                        SearchSeriesFragment.this.activity().onSearchFailure(
                                R.string.connection_timeout_title,
                                R.string.connection_timeout_message);
                    }
                }
            }

            @Override
            public void onStart() {
                searchField.setEnabled(false);
                buttonPanel.setVisibility(View.INVISIBLE);

                SearchSeriesFragment.this.isSearching = true;
                SearchSeriesFragment.this.activity().onSearchStart();
            }

            @Override
            public void onFinish() {
                searchField.setEnabled(true);
                buttonPanel.setVisibility(View.VISIBLE);

                SearchSeriesFragment.this.isSearching = false;
                SearchSeriesFragment.this.activity().onSearchFinish();
            }
        };
    }

    private void setUpListOnAdapter(List<Series> series) {
        this.setListAdapter(
            new SeriesSearchItemAdapter(
                this.activity(),
                this.activity(),
                R.layout.addseries_search_item, series
            )
        );
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Series selectedItem = (Series) parent.getItemAtPosition(position);
                    SearchSeriesFragment.this.activity().onRequestAdd(selectedItem);
                }
            }
        );
    }

    private AddSeriesActivity activity() {
        return (AddSeriesActivity) this.getSherlockActivity();
    }
}
