/*
 *   Language.java
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

package br.edu.ufcg.aweseries.thetvdb;

public enum Language {
    DA,
    FI,
    NL,
    DE,
    IT,
    ES,
    FR,
    PL,
    HU,
    EL,
    TR,
    RU,
    HE,
    JA,
    PT,
    ZH,
    CS,
    SL,
    HR,
    KO,
    EN,
    SV,
    NO;

    public static Language from(String abbreviation) {
        if (abbreviation == null) {
            throw new IllegalArgumentException("abbreviation should not be null");
        }

        return valueOf(abbreviation.toUpperCase());
    }

    public String abbreviation() {
        return this.toString().toLowerCase();
    }
}

