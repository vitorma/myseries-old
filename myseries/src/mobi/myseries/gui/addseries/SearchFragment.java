/*
 *   SearchFragment.java
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SearchFragment extends AddSeriesFragment {

    private SearchSeriesListener searchListener;
    private boolean isSearching;
    private AddSeriesAdapter adapter;

    @Override
    protected int layoutResource() {
        return R.layout.addseries_search;
    }

    @Override
    protected void setUp() {
        this.setUpSearchListener();
        this.setUpSearchFieldActionListeners();
        this.setUpClearButtonClickListener();
        this.setUpSearchButtonClickListener();
    }

    @Override
    protected void onStartFired() {
        App.searchSeriesService().registerListener(this.searchListener);

        if (this.adapter != null) {
            this.setUpNumberOfResults(this.adapter.getCount());
        } else if (this.isSearching) {
            this.searchListener.onStart();
        }
    }

    @Override
    protected void onStopFired() {
        App.searchSeriesService().deregisterListener(this.searchListener);
    }

    private void setUpSearchFieldActionListeners() {
        this.searchField().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    SearchFragment.this.showButtons();
                } else {
                    SearchFragment.this.onClear();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void afterTextChanged(Editable s) {}
        });

        this.searchField().setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SearchFragment.this.performSearch();
                    return true;
                }

                return false;
            }
        });

        this.searchField().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    SearchFragment.this.performSearch();
                    return true;
                }

                return false;
            }
        });
    }

    private void setUpClearButtonClickListener() {
        this.clearButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SearchFragment.this.searchField().setText("");
            }
        });
    }

    private void setUpSearchButtonClickListener() {
        this.searchButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                SearchFragment.this.performSearch();
            }
        });
    }

    private void performSearch() {
        App.searchSeriesService().search(this.searchField().getText().toString());
    }

    private void setUpSearchListener() {
        this.searchListener = new SearchSeriesListener() {
            @Override
            public void onStart() {
                SearchFragment.this.disableSearch();

                SearchFragment.this.isSearching = true;
                SearchFragment.this.activity().onSearchStart();
            }

            @Override
            public void onFinish() {
                SearchFragment.this.enableSearch();

                SearchFragment.this.isSearching = false;
                SearchFragment.this.activity().onSearchFinish();
            }

            @Override
            public void onSucess(List<Series> series) {
                SearchFragment.this.showResults(series);
            }

            @Override
            public void onFaluire(Throwable exception) {
                if (exception instanceof SeriesSearchException) {
                    if (exception.getCause() instanceof ConnectionFailedException) {
                        SearchFragment.this.activity().onSearchFailure(
                                R.string.connection_failed_title,
                                R.string.connection_failed_message);
                    } else if (exception.getCause() instanceof InvalidSearchCriteriaException) {
                        SearchFragment.this.activity().onSearchFailure(
                                R.string.invalid_criteria_title,
                                R.string.invalid_criteria_message);
                    } else if (exception.getCause() instanceof SeriesNotFoundException) {
                        SearchFragment.this.activity().onSearchFailure(
                                R.string.no_results_title,
                                R.string.no_results_message);
                    } else if (exception.getCause() instanceof ParsingFailedException) {
                        SearchFragment.this.activity().onSearchFailure(
                                R.string.parsing_failed_title,
                                R.string.parsing_failed_message);
                    } else if (exception.getCause() instanceof ConnectionTimeoutException){
                        SearchFragment.this.activity().onSearchFailure(
                                R.string.connection_timeout_title,
                                R.string.connection_timeout_message);
                    }
                }
            }
        };
    }

    private void showResults(List<Series> results) {
        this.setUpAdapter(results);
        this.setUpNumberOfResults(results.size());
    }

    private void setUpAdapter(List<Series> results) {
        this.adapter = new AddSeriesAdapter(this.activity(), R.layout.addseries_search_item, results);
        this.setListAdapter(this.adapter);
    }

    private void setUpNumberOfResults(int numberOfResults) {
        String format = this.getString(R.string.number_of_search_results);
        String seriesName = this.searchField().getText().toString();

        this.numberOfResults().setText(String.format(format, numberOfResults, seriesName));
    }

    private void disableSearch() {
        this.searchField().setEnabled(false);
        this.onClear();
    }

    private void enableSearch() {
        this.searchField().setEnabled(true);
        this.showButtons();
    }

    private void onClear() {
        this.hideButtons();
        this.numberOfResults().setText("");
        this.adapter = null;
        this.setListAdapter(null);
    }

    private void hideButtons() {
        this.buttonPanel().setVisibility(View.INVISIBLE);
    }

    private void showButtons() {
        this.buttonPanel().setVisibility(View.VISIBLE);
    }

    private EditText searchField() {
        return (EditText) this.getView().findViewById(R.id.searchField);
    }

    private View buttonPanel() {
        return this.getView().findViewById(R.id.buttonPanel);
    }

    private ImageButton searchButton() {
        return (ImageButton) this.getView().findViewById(R.id.searchButton);
    }

    private ImageButton clearButton() {
        return (ImageButton) this.getView().findViewById(R.id.clearButton);
    }

    private TextView numberOfResults() {
        return (TextView) this.getView().findViewById(R.id.numberOfResults);
    }
}
