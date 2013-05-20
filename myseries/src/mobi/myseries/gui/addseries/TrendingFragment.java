package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.TrendingSeriesListener;
import mobi.myseries.domain.model.Series;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class TrendingFragment extends AddSeriesFragment {

    private TrendingSeriesListener listener;
    private boolean isDownloadInProgress;
    private AddSeriesAdapter adapter;

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
        App.trendingSeriesService().register(this.listener);

        if (this.adapter == null) {
            App.trendingSeriesService().downloadTrendingList();
        } else if (this.isDownloadInProgress) {
            this.listener.onStartLoading();
        } else {
            this.setUpNumberOfResults(this.adapter.getCount());
            this.showContent();
        }
    }

    @Override
    protected void onStopFired() {
        App.trendingSeriesService().deregister(this.listener);
    }

    private void setUpTrendingSeriesListener() {
        this.listener = this.newTrendingSeriesListener();
    }

    private TrendingSeriesListener newTrendingSeriesListener() {
        return new TrendingSeriesListener() {
            @Override
            public void onStartLoading() {
                TrendingFragment.this.showProgress();
                TrendingFragment.this.isDownloadInProgress = true;
            }

            @Override
            public void onFinishLoading(List<Series> result) {
                TrendingFragment.this.setUpContent(result);
                TrendingFragment.this.showContent();
                TrendingFragment.this.isDownloadInProgress = false;
            }
        };
    }

    private void setUpContent(List<Series> result) {
        this.setUpNumberOfResults(result.size());
        this.setUpAdapter(result);
    }

    private void setUpNumberOfResults(int n) {
        this.numberOfResults().setText(String.valueOf(n));
    }

    private void setUpAdapter(List<Series> result) {
        this.adapter = new AddSeriesAdapter(this.getSherlockActivity(), R.layout.addseries_search_item, result);
        this.setListAdapter(this.adapter);
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
