package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SeriesSearchListener;
import mobi.myseries.domain.model.Series;

public class TrendingFragment extends AddSeriesFragment {

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
    protected boolean shouldPerformSearchOnStartLifeCycle() {
        return true;
    }

    @Override
    protected void performSearch() {
        App.seriesSearch().byTrending();
    }

    @Override
    protected void registerListenerForSeriesSearch() {
        App.seriesSearch().registerForSearchByTrending(this.seriesSearchListener);
    }

    @Override
    protected void deregisterListenerForSeriesSearch() {
        App.seriesSearch().deregisterForSearchByTrending(this.seriesSearchListener);
    }

    @Override
    protected SeriesSearchListener seriesSearchListener() {
        return new SeriesSearchListener() {
            @Override
            public void onStart() {
                TrendingFragment.this.isSearching = true;
                TrendingFragment.this.showProgress();
            }

            @Override
            public void onFinish() {
                TrendingFragment.this.isSearching = false;
                TrendingFragment.this.showResults();
            }

            @Override
            public void onSucess(List<Series> results) {
                TrendingFragment.this.setResults(results);
            }

            @Override
            public void onFailure(Exception exception) { }
        };
    }
}
