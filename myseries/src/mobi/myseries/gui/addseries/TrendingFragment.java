package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.search.SeriesSearchListener;
import mobi.myseries.domain.model.Series;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrendingFragment extends AddSeriesFragment {

    private SeriesSearchListener listener;

    @Override
    protected int layoutResource() {
        return R.layout.addseries_trending;
    }

    @Override
    protected void setUp() {
        this.setUpTrendingSeriesListener();
    }

    @Override
    protected void onStartFired() {
        App.seriesSearch().registerForSearchByTrending(this.listener);

        if (this.results == null) {
            App.seriesSearch().byTrending();
        } else if (this.isLoading) {
            this.listener.onStart();
        } else {
            this.setUpNumberOfResults(this.results.size());
            this.showContent();
        }
    }

    @Override
    protected void onStopFired() {
        App.seriesSearch().deregisterForSearchByTrending(this.listener);
    }

    private void setUpTrendingSeriesListener() {
        this.listener = this.newTrendingSeriesListener();
    }

    private SeriesSearchListener newTrendingSeriesListener() {
        return new SeriesSearchListener() {
            @Override
            public void onStart() {
                TrendingFragment.this.showProgress();
                TrendingFragment.this.isLoading = true;
            }

            @Override
            public void onFinish() {
                TrendingFragment.this.showContent();
                TrendingFragment.this.isLoading = false;
            }

            @Override
            public void onSucess(List<Series> results) {
                TrendingFragment.this.setUpContent(results);
            }

            @Override
            public void onFailure(Exception exception) { }
        };
    }

    private void setUpContent(List<Series> result) {
        this.setUpNumberOfResults(result.size());
        this.setResults(result);
    }

    private void setUpNumberOfResults(int n) {
        String format = this.getString(R.string.number_of_trending_results);

        this.numberOfResults().setText(String.format(format, n));
    }

    private void showProgress() {
        this.content().setVisibility(View.INVISIBLE);
        this.progress().setVisibility(View.VISIBLE);
    }

    private void showContent() {
        this.content().setVisibility(View.VISIBLE);
        this.progress().setVisibility(View.INVISIBLE);
    }

    private ProgressBar progress() {
        return (ProgressBar) this.getView().findViewById(R.id.progress);
    }

    private View content() {
        return this.getView().findViewById(R.id.content);
    }

    private TextView numberOfResults() {
        return (TextView) this.getView().findViewById(R.id.numberOfResults);
    }
}
