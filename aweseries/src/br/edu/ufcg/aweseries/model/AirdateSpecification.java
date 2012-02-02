package br.edu.ufcg.aweseries.model;

import java.util.Date;

import br.edu.ufcg.aweseries.util.Dates;
import br.edu.ufcg.aweseries.util.Validate;

public abstract class AirdateSpecification extends AbstractSpecification<Episode> {

    public static Specification<Episode> on(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compare(episode.airDate(), date) == 0;
            }
        };
    }

    public static Specification<Episode> before(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compare(episode.airDate(), date) < 0;
            }
        };
    }

    public static Specification<Episode> after(final Date date) {
        return new AirdateSpecification() {
            @Override
            protected boolean isSatisfiedByNonNull(Episode episode) {
                return Dates.compare(episode.airDate(), date) > 0;
            }
        };
    }

    @Override
    public boolean isSatisfiedBy(Episode episode) {
        Validate.isNonNull(episode, "episode should be non-null");
        return this.isSatisfiedByNonNull(episode);
    }

    protected abstract boolean isSatisfiedByNonNull(Episode episode);
}