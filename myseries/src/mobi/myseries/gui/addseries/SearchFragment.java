package mobi.myseries.gui.addseries;

import java.util.ArrayList;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SearchListener;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import android.os.Bundle;

public class SearchFragment extends AddSeriesFragment {
    private SearchListener searchListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.searchListener = this.newSearchListener();
    }

    @Override
    protected boolean hasSearchPanel() {
        return true;
    }

    @Override
    protected int sourceTextResource() {
        return R.string.powered_by_trakt;
    }

    @Override
    protected int numberOfResultsFormatResource() {
        return R.string.number_of_results_for_search_by_name;
    }

    @Override
    protected boolean shouldServiceRunOnStartLifeCycle() {
        return false;
    }

    @Override
    protected void runService() {
        App.searchService().search(this.searchField.getText().toString());
    }

    @Override
    protected void registerListenerForService() {
        App.searchService().register(this.searchListener);
    }

    @Override
    protected void deregisterListenerForService() {
        App.searchService().deregister(this.searchListener);
    }

    @Override
    protected void onServiceStartRunning() {
        this.searchListener.onStart();
    }

    private SearchListener newSearchListener() {
        return new SearchListener() {
            private boolean showButtons;

            @Override
            public void onStart() {
                SearchFragment.this.disableSearch();

                SearchFragment.this.isServiceRunning = true;
                SearchFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                SearchFragment.this.enableSearch(this.showButtons);

                SearchFragment.this.isServiceRunning = false;
            }

            @Override
            public void onSucess(List<SearchResult> results) {
                this.showButtons = true;

                SearchFragment.this.setResults(results);

                if (SearchFragment.this.hasResultsToShow()) {
                    SearchFragment.this.showResults();
                } else {
                    SearchFragment.this.hideProgress();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                this.showButtons = !(exception instanceof InvalidSearchCriteriaException);

                SearchFragment.this.setResults(new ArrayList<SearchResult>());

                SearchFragment.this.setError(exception);
                SearchFragment.this.showError();
            }
        };
    }
}
