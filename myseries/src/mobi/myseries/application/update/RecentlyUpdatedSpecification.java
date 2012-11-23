package mobi.myseries.application.update;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.AbstractSpecification;

public class RecentlyUpdatedSpecification extends AbstractSpecification<Series> {
    @Override
    public boolean isSatisfiedBy(Series t) {
        return (timeSince(t.lastUpdate())) < UpdateService.automaticUpdateInterval();
    }

    public static long timeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }
}
