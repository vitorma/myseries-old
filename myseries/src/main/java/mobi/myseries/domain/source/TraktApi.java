package mobi.myseries.domain.source;

import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;

import java.util.List;

public interface TraktApi {
    List<SearchResult> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;

    List<SearchResult> listTrending()
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;

    Series fetchSeries(int seriesId)
            throws ParsingFailedException, ConnectionFailedException, NetworkUnavailableException;

    List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException;
}
