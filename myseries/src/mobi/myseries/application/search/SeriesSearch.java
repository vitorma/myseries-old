package mobi.myseries.application.search;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TrendingSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.Handler;

public class SeriesSearch {

    private final SeriesSource seriesSource;
    private final TrendingSource trendingSource;
    private final ListenerSet<SeriesSearchListener> searchByNameListeners;
    private final ListenerSet<SeriesSearchListener> searchByTrendingListeners;
    private final ExecutorService trendingExecutor;
    private final ExecutorService searchExecutor;
    private Handler handler;

    public SeriesSearch(SeriesSource seriesSource, TrendingSource trendingSource) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(trendingSource, "trendingSource");

        this.seriesSource = seriesSource;
        this.trendingSource = trendingSource;
        this.searchByNameListeners = new ListenerSet<SeriesSearchListener>();
        this.searchByTrendingListeners = new ListenerSet<SeriesSearchListener>();
        this.trendingExecutor = Executors.newSingleThreadExecutor();
        this.searchExecutor = Executors.newSingleThreadExecutor();
    }

    public void byName(String seriesName) {
        String language = Locale.getDefault().getLanguage();

        this.searchExecutor.execute(
            new SearchByNameTask(new String[] { seriesName, language }));
    }

    public void byTrending() {
        this.trendingExecutor.execute(
            new SearchByTrendingTask());
    }

    public boolean registerForSearchByName(SeriesSearchListener listener) {
        return this.searchByNameListeners.register(listener);
    }

    public boolean deregisterForSearchByName(SeriesSearchListener listener) {
        return this.searchByNameListeners.deregister(listener);
    }

    public boolean registerForSearchByTrending(SeriesSearchListener listener) {
        return this.searchByTrendingListeners.register(listener);
    }

    public boolean deregisterForSearchByTrending(SeriesSearchListener listener) {
        return this.searchByTrendingListeners.deregister(listener);
    }

    public SeriesSearch withHandler(Handler handler) {
        this.handler = handler;

        return this;
    }

    private void notifyOnStart(final ListenerSet<SeriesSearchListener> listeners) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SeriesSearchListener l : listeners) {
                    l.onStart();
                }
            }
        });
    }

    private void notifyOnSucess(final ListenerSet<SeriesSearchListener> listeners,
        final List<Series> results) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SeriesSearchListener l : listeners) {
                    l.onSucess(results);
                }
            }
        });
    }

    private void notifyOnFailure(final ListenerSet<SeriesSearchListener> listeners,
        final Exception failure) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SeriesSearchListener l : listeners) {
                    l.onFailure(failure);
                }
            }
        });
    }

    private void notifyOnFinish(final ListenerSet<SeriesSearchListener> listeners) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                for (SeriesSearchListener l : listeners) {
                    l.onFinish();
                }
            }
        });
    }

    private abstract class SearchTask implements Runnable {
        private final String[] strings;

        public SearchTask(String... strings) {
            this.strings = strings;
        }

        @Override
        public void run() {

            AsyncTaskResult<List<Series>> taskResult;

            SeriesSearch.this.notifyOnStart(this.listeners());

            try {
                taskResult = new AsyncTaskResult<List<Series>>(
                    this.downloadSeriesList(this.strings));
            } catch (SeriesSearchException e) {
                taskResult = new AsyncTaskResult<List<Series>>(e);
            }

            if (taskResult.error() == null) {
                SeriesSearch.this.notifyOnSucess(this.listeners(), taskResult.result());
            } else {
                SeriesSearch.this.notifyOnFailure(this.listeners(), taskResult.error());
            }

            SeriesSearch.this.notifyOnFinish(this.listeners());

        }

        protected abstract List<Series> downloadSeriesList(String... params);

        protected abstract ListenerSet<SeriesSearchListener> listeners();
    }

    private class SearchByNameTask extends SearchTask {
        public SearchByNameTask(String... strings) {
            super(strings);
        }

        @Override
        protected List<Series> downloadSeriesList(String... params) {
            String seriesName = params[0];
            String language = params[1];

            try {
                List<Series> searchResult = SeriesSearch.this.seriesSource.searchFor(seriesName, language);

                for (Series s : searchResult) {
                    s.setPosterFilename(SeriesSearch.this.seriesSource.fetchSeriesPosterPath(s.id()));
                }

                return searchResult;
            } catch (Exception e) {
                throw new SeriesSearchException(e);
            }
        }

        @Override
        protected ListenerSet<SeriesSearchListener> listeners() {
            return SeriesSearch.this.searchByNameListeners;
        }
    }

    private class SearchByTrendingTask extends SearchTask {
        public SearchByTrendingTask(String... strings) {
            super(strings);
        }

        @Override
        protected List<Series> downloadSeriesList(String... params) {
            return SeriesSearch.this.trendingSource.listTrending();
        }

        @Override
        protected ListenerSet<SeriesSearchListener> listeners() {
            return SeriesSearch.this.searchByTrendingListeners;
        }
    }
}
