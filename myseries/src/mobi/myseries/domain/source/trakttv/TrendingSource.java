package mobi.myseries.domain.source.trakttv;

import java.util.List;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;

public interface TrendingSource {
    public List<ParcelableSeries> listTrending()
            throws ConnectionFailedException, ParsingFailedException;
}
