package mobi.myseries.application.trending;

import java.util.List;

import mobi.myseries.application.ApplicationService;
import mobi.myseries.application.Environment;
import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.trakttv.TrendingSource;

public class TrendingService extends ApplicationService<TrendingListener> {

    public TrendingService(Environment environment) {
        super(environment);
    }

    public void listTrending() {
        this.run(new TrendingTask());
    }

    private void notifyOnStart() {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners()) {
                    l.onStart();
                }
            }
        });
    }

    private void notifyOnFinish() {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners()) {
                    l.onFinish();
                }
            }
        });
    }

    private void notifyOnSucess(final List<ParcelableSeries> results) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners()) {
                    l.onSucess(results);
                }
            }
        });
    }

    private void notifyOnFailure(final Exception failure) {
        this.runInMainThread(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners()) {
                    l.onFailure(failure);
                }
            }
        });
    }

    private class TrendingTask implements Runnable {
        @Override
        public void run() {
            TrendingService.this.notifyOnStart();

            try {
                TrendingSource source = TrendingService.this.environment().trendingSource();
                List<ParcelableSeries> result = source.listTrending();

                TrendingService.this.notifyOnSucess(result);
            } catch (Exception e) {
                TrendingService.this.notifyOnFailure(e);
            }

            TrendingService.this.notifyOnFinish();
        }
    }
}
