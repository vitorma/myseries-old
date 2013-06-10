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
import mobi.myseries.gui.shared.FailureDialogBuilder;
import android.app.Dialog;

public class SearchByNameFragment extends AddSeriesFragment {

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
                SearchByNameFragment.this.disableSearch();

                SearchByNameFragment.this.isSearching = true;
                SearchByNameFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                SearchByNameFragment.this.enableSearch(this.showButtons);

                SearchByNameFragment.this.isSearching = false;

                if (SearchByNameFragment.this.hasResultsToShow()) {
                    SearchByNameFragment.this.showResults();
                } else {
                    SearchByNameFragment.this.hideProgress();
                }
            }

            @Override
            public void onSucess(List<Series> results) {
                this.showButtons = true;

                SearchByNameFragment.this.setResults(results);
            }

            @Override
            public void onFailure(Exception exception) {
                if (exception.getCause() instanceof ConnectionFailedException) {
                    this.showButtons = true;

                    SearchByNameFragment.this.onSearchFailure(
                            R.string.connection_failed_title,
                            R.string.connection_failed_message);
                } else if (exception.getCause() instanceof InvalidSearchCriteriaException) {
                    this.showButtons = false;

                    SearchByNameFragment.this.onSearchFailure(
                            R.string.invalid_criteria_title,
                            R.string.invalid_criteria_message);
                } else if (exception.getCause() instanceof ParsingFailedException) {
                    this.showButtons = true;

                    SearchByNameFragment.this.onSearchFailure(
                            R.string.parsing_failed_title,
                            R.string.parsing_failed_message);
                } else if (exception.getCause() instanceof ConnectionTimeoutException){
                    this.showButtons = true;

                    SearchByNameFragment.this.onSearchFailure(
                            R.string.connection_timeout_title,
                            R.string.connection_timeout_message);
                }
            }
        };
    }

    private void onSearchFailure(int searchFailureTitleResourceId, int searchFailureMessageResourceId) {
        Dialog dialog = new FailureDialogBuilder(this.getActivity())
            .setTitle(searchFailureTitleResourceId)
            .setMessage(searchFailureMessageResourceId)
            .build();

        this.activity().showDialog(dialog);
    }
}
