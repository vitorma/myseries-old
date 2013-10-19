package mobi.myseries.gui.addseries;

import java.util.ArrayList;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.trending.TrendingListener;
import mobi.myseries.domain.model.SearchResult;
import android.os.Bundle;

public class TrendingFragment extends AddSeriesFragment {
    private TrendingListener mTrendingListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTrendingListener = newTrendingListener();
    }

    @Override
    protected boolean hasSearchPanel() {
        return false;
    }

    @Override
    protected int sourceTextResource() {
        return R.string.powered_by_trakt;
    }

    @Override
    protected int numberOfResultsFormatResource() {
        return R.string.number_of_results_for_search_by_trending;
    }

    @Override
    protected boolean shouldServiceRunOnStartLifeCycle() {
        return true;
    }

    @Override
    protected void runService() {
        App.trendingService().listTrending();
    }

    @Override
    protected void registerListenerForService() {
        App.trendingService().register(mTrendingListener);
    }

    @Override
    protected void deregisterListenerForService() {
        App.trendingService().deregister(mTrendingListener);
    }

    @Override
    protected void onServiceStartRunning() {
        mTrendingListener.onStart();
    }

    private TrendingListener newTrendingListener() {
        return new TrendingListener() {
            @Override
            public void onStart() {
                TrendingFragment.this.mIsServiceRunning = true;
                TrendingFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                TrendingFragment.this.mIsServiceRunning = false;
            }

            @Override
            public void onSucess(List<SearchResult> results) {
                mError = null;
                TrendingFragment.this.setResults(results);
                TrendingFragment.this.showResults();
            }

            @Override
            public void onFailure(Exception exception) {
                mError = exception;
                TrendingFragment.this.setResults(new ArrayList<SearchResult>());
                hideResults();

                TrendingFragment.this.setError(exception);
                TrendingFragment.this.showError();
            }
        };
    }
}
