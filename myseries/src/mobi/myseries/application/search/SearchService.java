package mobi.myseries.application.search;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.trakttv.SearchSource;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.Handler;

public class SearchService {
    private final SearchSource source;
    private final ListenerSet<SearchListener> listeners;
    private final ExecutorService executor;
    private Handler handler;

    public SearchService(SearchSource source) {
        Validate.isNonNull(source, "source");

        this.source = source;
        this.listeners = new ListenerSet<SearchListener>();
        this.executor = Executors.newSingleThreadExecutor();
    }

    public SearchService withHandler(Handler handler) {
        this.handler = handler;

        return this;
    }

    public void search(String query) {
        this.executor.execute(new SearchTask(query));
    }

    public boolean register(SearchListener listener) {
        return this.listeners.register(listener);
    }

    public boolean deregister(SearchListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyOnStart() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners) {
                    l.onStart();
                }
            }
        });
    }

    private void notifyOnSucess(final List<ParcelableSeries> list) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners) {
                    l.onSucess(list);
                }
            }
        });
    }

    private void notifyOnFailure(final Exception failure) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners) {
                    l.onFailure(failure);
                }
            }
        });
    }

    private void notifyOnFinish() {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SearchListener l : SearchService.this.listeners) {
                    l.onFinish();
                }
            }
        });
    }

    private class SearchTask implements Runnable {
        private final String query;

        private SearchTask(String query) {
            this.query = query;
        }

        @Override
        public void run() {
            SearchService.this.notifyOnStart();

            try {
                SearchService.this.notifyOnSucess(SearchService.this.source.search(this.query));
            } catch (Exception e) {
                SearchService.this.notifyOnFailure(e);
            }

            SearchService.this.notifyOnFinish();
        }
    }
}
