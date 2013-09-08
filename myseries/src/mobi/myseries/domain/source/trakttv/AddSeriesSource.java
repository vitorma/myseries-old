package mobi.myseries.domain.source.trakttv;

import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.ParsingFailedException;

public interface AddSeriesSource {
    public Series fetchSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException;
}
