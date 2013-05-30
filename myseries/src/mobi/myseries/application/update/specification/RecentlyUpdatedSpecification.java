package mobi.myseries.application.update.specification;

import mobi.myseries.application.update.UpdatePolicy;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.AbstractSpecification;

public class RecentlyUpdatedSpecification extends AbstractSpecification<Series> {
    @Override
    public boolean isSatisfiedBy(Series t) {
        return (timeSince(t.lastUpdate())) < UpdatePolicy.automaticUpdateInterval();
    }

    public static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }
}
