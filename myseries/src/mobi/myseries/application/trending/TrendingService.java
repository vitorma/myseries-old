package mobi.myseries.application.trending;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.trakttv.TrendingSource;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.Handler;

public class TrendingService {
    private final TrendingSource source;
    private final ListenerSet<TrendingListener> listeners;
    private final ExecutorService executor;
    private Handler handler;

    public TrendingService(TrendingSource source) {
        Validate.isNonNull(source, "source");

        this.source = source;
        this.listeners = new ListenerSet<TrendingListener>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public TrendingService withHandler(Handler handler) {
        this.handler = handler;

        return this;
    }

    public void listTrending() {
        this.executor.execute(new TrendingTask());
    }

    public boolean register(TrendingListener listener) {
        return this.listeners.register(listener);
    }

    public boolean deregister(TrendingListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyOnStart() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners) {
                    l.onStart();
                }
            }
        });
    }

    private void notifyOnFinish() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners) {
                    l.onFinish();
                }
            }
        });
    }

    private void notifyOnSucess(final List<ParcelableSeries> results) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners) {
                    l.onSucess(results);
                }
            }
        });
    }

    private void notifyOnFailure(final Exception failure) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (TrendingListener l : TrendingService.this.listeners) {
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
                TrendingService.this.notifyOnSucess(TrendingService.this.source.listTrending());
            } catch (Exception e) {
                TrendingService.this.notifyOnFailure(e);
            }

            TrendingService.this.notifyOnFinish();
        }
    }
}
