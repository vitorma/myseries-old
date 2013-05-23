package mobi.myseries.application.search;

import java.util.List;
import java.util.Locale;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.domain.source.TrendingSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;

public class SeriesSearch {

    private SeriesSource seriesSource;
    private TrendingSource trendingSource;
    private ListenerSet<SeriesSearchListener> searchByNameListeners;
    private ListenerSet<SeriesSearchListener> searchByTrendingListeners;

    public SeriesSearch(SeriesSource seriesSource, TrendingSource trendingSource) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(trendingSource, "trendingSource");

        this.seriesSource = seriesSource;
        this.trendingSource = trendingSource;
        this.searchByNameListeners = new ListenerSet<SeriesSearchListener>();
        this.searchByTrendingListeners = new ListenerSet<SeriesSearchListener>();
    }

    public void byName(String seriesName) {
        String language = Locale.getDefault().getLanguage();

        new SearchByNameTask().execute(seriesName, language);
    }

    public void byTrending() {
        new SearchByTrendingTask().execute();
    }

    public boolean registerForSearchByName(SeriesSearchListener listener){
        return this.searchByNameListeners.register(listener);
    }

    public boolean deregisterForSearchByName(SeriesSearchListener listener){
        return this.searchByNameListeners.deregister(listener);
    }

    public boolean registerForSearchByTrending(SeriesSearchListener listener){
        return this.searchByTrendingListeners.register(listener);
    }

    public boolean deregisterForSearchByTrending(SeriesSearchListener listener){
        return this.searchByTrendingListeners.deregister(listener);
    }

    private void notifyOnStart(ListenerSet<SeriesSearchListener> listeners) {
        for (SeriesSearchListener l : listeners) {
            l.onStart();
        }
    }

    private void notifyOnSucess(ListenerSet<SeriesSearchListener> listeners, List<Series> results) {
        for (SeriesSearchListener l : listeners) {
            l.onSucess(results);
        }
    }

    private void notifyOnFailure(ListenerSet<SeriesSearchListener> listeners, Exception failure) {
        for (SeriesSearchListener l : listeners) {
            l.onFailure(failure);
        }
    }

    private void notifyOnFinish(ListenerSet<SeriesSearchListener> listeners) {
        for (SeriesSearchListener l : listeners) {
            l.onFinish();
        }
    }

    private abstract class SearchTask extends AsyncTask<String, Void, AsyncTaskResult<List<Series>>> {
        @Override
        protected final void onPreExecute() {
            SeriesSearch.this.notifyOnStart(this.listeners());
        }

        @Override
        protected AsyncTaskResult<List<Series>> doInBackground(String... params) {
            try {
                return new AsyncTaskResult<List<Series>>(this.downloadSeriesList(params));
            } catch (SeriesSearchException e) {
                return new AsyncTaskResult<List<Series>>(e);
            }
        }

        @Override
        protected final void onPostExecute(AsyncTaskResult<List<Series>> taskResult) {
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
        @Override
        protected List<Series> downloadSeriesList(String... params) {
            String seriesName = params[0];
            String language = params[1];

            try {
                return SeriesSearch.this.seriesSource.searchFor(seriesName, language);
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
