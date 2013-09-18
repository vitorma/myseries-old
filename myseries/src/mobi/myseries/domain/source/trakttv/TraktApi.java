package mobi.myseries.domain.source.trakttv;

import java.util.List;

import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;

public interface TraktApi {
    public List<SearchResult> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException;
    public List<SearchResult> listTrending()
            throws ConnectionFailedException, ParsingFailedException;
    public Series fetchSeries(int seriesId)
            throws ParsingFailedException, ConnectionFailedException;
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException;
}
