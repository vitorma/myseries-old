package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.application.LocalizationProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AsyncTaskResult;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.Validate;
import android.os.AsyncTask;
import android.util.Log;

public class SeriesSearch implements Publisher<SeriesSearchListener> {
    private static final String TAG = SeriesSearch.class.getSimpleName();

    private SeriesSource seriesSource;
    private LocalizationProvider localizationProvider;
    private ListenerSet<SeriesSearchListener> listenerSet;

    public SeriesSearch(SeriesSource seriesSource, LocalizationProvider localizationProvider) {
        Validate.isNonNull(seriesSource, "seriesSource");
        Validate.isNonNull(localizationProvider, "localizationProvider");

        this.seriesSource = seriesSource;
        this.localizationProvider = localizationProvider;
        this.listenerSet = new ListenerSet<SeriesSearchListener>();
    }

    public void bySeriesName(String seriesName) {
        String language = this.localizationProvider.language();

        new SearchTask().execute(seriesName, language);
    }

    @Override
    public boolean register(SeriesSearchListener listener){
        return this.listenerSet.register(listener);
    }

    @Override
    public boolean deregister(SeriesSearchListener listener){
        return this.listenerSet.deregister(listener);
    }

    private void notifyOnStart() {
        for (SeriesSearchListener l : this.listenerSet) {
            l.onStart();
        }
    }

    private void notifyOnSucess(List<Series> results) {
        for (SeriesSearchListener l : this.listenerSet) {
            l.onSucess(results);
        }
    }

    private void notifyOnFailure(Exception failure) {
        for (SeriesSearchListener l : this.listenerSet) {
            l.onFailure(failure);
        }
    }

    private void notifyOnFinish() {
        for (SeriesSearchListener l : this.listenerSet) {
            l.onFinish();
        }
    }

    private abstract class BaseTask extends AsyncTask<String, Void, AsyncTaskResult<List<Series>>> {
        @Override
        protected final void onPreExecute() {
            SeriesSearch.this.notifyOnStart();
        }

        @Override
        protected final void onPostExecute(AsyncTaskResult<List<Series>> taskResult) {
            if (taskResult.error() == null) {
                SeriesSearch.this.notifyOnSucess(taskResult.result());
            } else {
                SeriesSearch.this.notifyOnFailure(taskResult.error());
            }

            SeriesSearch.this.notifyOnFinish();
        }
    }

    private class SearchTask extends BaseTask {
        @Override
        protected AsyncTaskResult<List<Series>> doInBackground(String... params) {
            String seriesName = params[0];
            String language = params[1];

            try {
                Log.d(TAG, "trying to search for " + seriesName + "...");
                List<Series> seriesList = SeriesSearch.this.seriesSource.searchFor(seriesName, language);

                Log.d(TAG, "found " + seriesList.size() + " results");
                return new AsyncTaskResult<List<Series>>(seriesList);
            } catch (Exception e) {
                Log.e(TAG, "search failure caused by " + e.getClass().getSimpleName());
                return new AsyncTaskResult<List<Series>>(new SeriesSearchException(e));
            }
        }
    }
}
