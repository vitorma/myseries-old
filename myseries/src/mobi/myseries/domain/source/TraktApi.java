package mobi.myseries.domain.source;

import java.util.List;

import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;

public interface TraktApi {
    public List<SearchResult> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;
    public List<SearchResult> listTrending()
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;
    public Series fetchSeries(int seriesId)
            throws ParsingFailedException, ConnectionFailedException, NetworkUnavailableException;
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;
}
