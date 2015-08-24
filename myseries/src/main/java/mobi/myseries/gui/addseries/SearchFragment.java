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
    private SearchListener mSearchListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSearchListener = newSearchListener();
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
        App.searchService().search(mSearchField.getText().toString());
    }

    @Override
    protected void registerListenerForService() {
        App.searchService().register(mSearchListener);
    }

    @Override
    protected void deregisterListenerForService() {
        App.searchService().deregister(mSearchListener);
    }

    @Override
    protected void onServiceStartRunning() {
        mSearchListener.onStart();
    }

    private SearchListener newSearchListener() {
        return new SearchListener() {

            private boolean mShowButtons;

            @Override
            public void onStart() {
                SearchFragment.this.disableSearch();

                SearchFragment.this.mIsServiceRunning = true;
                SearchFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                SearchFragment.this.enableSearch(mShowButtons);

                SearchFragment.this.mIsServiceRunning = false;
            }

            @Override
            public void onSucess(List<SearchResult> results) {
                mError = null;
                mShowButtons = true;
                SearchFragment.this.setResults(results);

                if (SearchFragment.this.hasResultsToShow()) {
                    SearchFragment.this.showResults();
                } else {
                    SearchFragment.this.hideProgress();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                mError = exception;
                mShowButtons = !(exception instanceof InvalidSearchCriteriaException);

                SearchFragment.this.setResults(new ArrayList<SearchResult>());
                SearchFragment.this.setError(exception);
                SearchFragment.this.showError();
            }
        };
    }
}
