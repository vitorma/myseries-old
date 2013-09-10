package mobi.myseries.domain.source.trakttv;

import java.util.List;

import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;

public interface TrendingSource {
    public List<SearchResult> listTrending()
            throws ConnectionFailedException, ParsingFailedException;
}
