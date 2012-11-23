package mobi.myseries.application.update;

import java.util.Collection;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Validate;

public class SeriesIdInCollectionSpecification extends AbstractSpecification<Series> {
    private final Collection<Integer> collection;

    public SeriesIdInCollectionSpecification(Collection<Integer> collection) {
        Validate.isNonNull(collection, "collection cannot be null");
        this.collection = collection;
    }

    @Override
    public boolean isSatisfiedBy(Series t) {
        return collection.contains(t.id());
    }
}
