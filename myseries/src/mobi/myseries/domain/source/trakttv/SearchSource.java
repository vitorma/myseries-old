package mobi.myseries.domain.source.trakttv;

import java.util.List;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;

public interface SearchSource {
    public List<ParcelableSeries> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException;
}
