package mobi.myseries.application.search;

import java.util.List;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ConnectionTimeoutException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.domain.source.SeriesNotFoundException;
import mobi.myseries.domain.source.SeriesSource;
import mobi.myseries.shared.AsyncTaskResult;
import android.util.Log;

public class SearchSeriestask implements Runnable {

    private SeriesSource seriesSource;
    private String seriesName;
    private String language;
    private AsyncTaskResult<List<Series>> result;

    public SearchSeriestask(SeriesSource seriesSource, String seriesName, String language) {
        this.seriesSource = seriesSource;
        this.seriesName = seriesName;
        this.language = language;
        this.result = null;
    }
    
    @Override
    public void run() {
        try {
            Log.d("Series Search", "trying to search for " + seriesName + "...");
            List<Series> seriesList = this.seriesSource.searchFor(seriesName, language);
            if (!seriesList.isEmpty()) {
                Log.d("Series Search", "found " + seriesList.size() + " results");
                result = new AsyncTaskResult<List<Series>>(seriesList);
                return;
            }
            Log.d("Series Search", "the search has falied, no series found.");
            result = new AsyncTaskResult<List<Series>>(new SeriesSearchException(new SeriesNotFoundException()));
            return;
        } catch (InvalidSearchCriteriaException e) {
            Log.d("Series Search", "the search has falied, invalid search criteria.");
            result = new AsyncTaskResult<List<Series>>(new SeriesSearchException(e));
            return;
        } catch (ConnectionFailedException e) {
            Log.d("Series Search", "the search has falied, the connection has failed.");
            result = new AsyncTaskResult<List<Series>>(new SeriesSearchException(e));
            return;
        } catch (ParsingFailedException e) {
            Log.d("Series Search", "the search has falied, the parsing has failed.");
            result = new AsyncTaskResult<List<Series>>(new SeriesSearchException(e));
            return;
        } catch (ConnectionTimeoutException e) {
            Log.d("Series Search", "the search has falied, the connection has timed out.");
            result = new AsyncTaskResult<List<Series>>(new SeriesSearchException(e));
            return;
        }
    }
    
    public AsyncTaskResult<List<Series>> result() {
        return this.result;
    }


}
