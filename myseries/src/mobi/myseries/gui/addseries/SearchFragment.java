package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SeriesSearchListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;

public class SearchFragment extends AddSeriesFragment {

    @Override
    protected boolean hasSearchPanel() {
        return true;
    }

    @Override
    protected int sourceTextResource() {
        return R.string.powered_by_thetvdb;
    }

    @Override
    protected int numberOfResultsFormatResource() {
        return R.string.number_of_results_for_search_by_name;
    }

    @Override
    protected boolean shouldPerformSearchOnStartLifeCycle() {
        return false;
    }

    @Override
    protected void performSearch() {
        App.seriesSearch().byName(this.searchField.getText().toString());
    }

    @Override
    protected void registerListenerForSeriesSearch() {
        App.seriesSearch().registerForSearchByName(this.seriesSearchListener);
    }

    @Override
    protected void deregisterListenerForSeriesSearch() {
        App.seriesSearch().deregisterForSearchByName(this.seriesSearchListener);
    }

    @Override
    protected SeriesSearchListener seriesSearchListener() {
        return new SeriesSearchListener() {
            private boolean showButtons;

            @Override
            public void onStart() {
                SearchFragment.this.disableSearch();

                SearchFragment.this.isSearching = true;
                SearchFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                SearchFragment.this.enableSearch(this.showButtons);

                SearchFragment.this.isSearching = false;

                if (SearchFragment.this.hasResultsToShow()) {
                    SearchFragment.this.showResults();
                } else {
                    SearchFragment.this.hideProgress();
                }
            }

            @Override
            public void onSucess(List<Series> results) {
                this.showButtons = true;

                SearchFragment.this.setResults(results);
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception.getCause() instanceof ConnectionFailedException) {
                    this.showButtons = true;

                    SearchFragment.this.activity().onSearchFailure(
                            R.string.connection_failed_title,
                            R.string.connection_failed_message);
                } else if (exception.getCause() instanceof InvalidSearchCriteriaException) {
                    this.showButtons = false;

                    SearchFragment.this.activity().onSearchFailure(
                            R.string.invalid_criteria_title,
                            R.string.invalid_criteria_message);
                } else if (exception.getCause() instanceof ParsingFailedException) {
                    this.showButtons = true;

                    SearchFragment.this.activity().onSearchFailure(
                            R.string.parsing_failed_title,
                            R.string.parsing_failed_message);
                } else if (exception.getCause() instanceof ConnectionTimeoutException){
                    this.showButtons = true;

                    SearchFragment.this.activity().onSearchFailure(
                            R.string.connection_timeout_title,
                            R.string.connection_timeout_message);
                }
            }
        };
    }
}
