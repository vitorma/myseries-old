/*
 *   AndSpecification.java
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

package mobi.myseries.shared;

public class AndSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> specification1;
    private Specification<T> specification2;

    public AndSpecification(Specification<T> specification1, Specification<T> specification2) {
        Validate.isNonNull(specification1, "specification1 should be non-null");
        Validate.isNonNull(specification2, "specification2 should be non-null");

        this.specification1 = specification1;
        this.specification2 = specification2;
    }

    @Override
    public boolean isSatisfiedBy(final T t) {
        return specification1.isSatisfiedBy(t) && specification2.isSatisfiedBy(t);
    }
}
