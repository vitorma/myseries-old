/*
 *   OrSpecification.java
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

package br.edu.ufcg.aweseries.model;

import br.edu.ufcg.aweseries.util.Validate;

public class OrSpecification<T> extends AbstractSpecification<T> {
    private Specification<T> specification1;
    private Specification<T> specification2;

    public OrSpecification(Specification<T> s1, Specification<T> s2) {
        Validate.isNonNull(s1, "specification1 should be non-null");
        Validate.isNonNull(s2, "specification2 should be non-null");

        this.specification1 = s1;
        this.specification2 = s2;
    }

    @Override
    public boolean isSatisfiedBy(T t) {
        return specification1.isSatisfiedBy(t) || specification2.isSatisfiedBy(t);
    }
}
