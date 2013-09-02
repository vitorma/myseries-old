package mobi.myseries.domain.source.trakttv;

import java.util.List;

import mobi.myseries.domain.model.SearchResult;

public interface TrendingSource {
    public List<SearchResult> listTrending();
}
