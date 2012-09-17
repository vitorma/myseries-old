/*
 *   SeriesElementHandler.java
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

package mobi.myseries.domain.source;

import java.util.HashSet;
import java.util.Set;

import mobi.myseries.domain.constant.Invalid;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Validate;


import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Log;

public class SeriesUpdateElementHandler {
    private static final String SERIES = "Series";
    private static final String ID = "id";

    private Element seriesElement;
    private Set<Integer> results;
    private int currentResult;

    private SeriesUpdateElementHandler(RootElement rootElement) {
        Validate.isNonNull(rootElement, "rootElement");

        this.seriesElement = rootElement.getChild(SERIES);
        this.results = new HashSet<Integer>();

        this.storeTheCurrentResultAtTheEndOfEachSeriesElement();
    }

    public static SeriesUpdateElementHandler from(RootElement rootElement) {
        return new SeriesUpdateElementHandler(rootElement);
    }

    private void storeTheCurrentResultAtTheEndOfEachSeriesElement() {
        this.seriesElement.setEndElementListener(new EndElementListener() {
            @Override
            public void end() {
                SeriesUpdateElementHandler.this.results.add(SeriesUpdateElementHandler.this.currentResult());
            }
        });
    }

    public SeriesUpdateElementHandler handlingId() {
        this.seriesElement.getChild(ID).setEndTextElementListener(new EndTextElementListener() {
            @Override
            public void end(String body) {
                int id = Numbers.parseInt(body, Invalid.SERIES_ID);
                SeriesUpdateElementHandler.this.currentResult = id;                
            }
        });

        return this;
    }

    public Integer currentResult() {
        return this.currentResult;
    }

    public Set<Integer> allResults() {
        return this.results;
    }
}