/*
 *   AirdateSpecification.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.application.schedule;

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