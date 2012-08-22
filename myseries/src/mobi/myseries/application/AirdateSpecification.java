package mobi.myseries.application;

import java.util.Date;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.AbstractSpecification;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public abstract class AirdateSpecification extends AbstractSpecification<Episode> {

    public static Specification<Episode> on(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compareByNullLast(episode.airDate(), date) == 0;
            }
        };
    }

    public static Specification<Episode> before(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compareByNullLast(episode.airDate(), date) < 0;
            }
        };
    }

    public static Specification<Episode> after(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compareByNullLast(episode.airDate(), date) > 0;
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(Episode episode) {
        Validate.isNonNull(episode, "episode");

        return this.isSatisfiedByNonNull(episode);
    }

    protected abstract boolean isSatisfiedByNonNull(Episode episode);
}