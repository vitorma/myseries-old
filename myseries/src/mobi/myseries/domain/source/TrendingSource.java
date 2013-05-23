package mobi.myseries.domain.source;

import java.util.List;

import mobi.myseries.domain.model.Series;

public interface TrendingSource {
    public List<Series> listTrending();
}
