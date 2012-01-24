/*
 *   TestStreamFactory.java
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

package br.edu.ufcg.aweseries.test.acceptance.util;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;

import br.edu.ufcg.aweseries.test.util.SampleSeries;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;

public class TestStreamFactory implements StreamFactory {

	private Set<SampleSeries> allSampleSeries = SampleSeries.allSamples;

	@Override
	public InputStream streamForFullSeries(String seriesId, String language) {
		// FIXME Use language
		this.checkIfItIsAValidUrlSuffix(seriesId, "seriesId");

		for (SampleSeries s : this.allSampleSeries) {
			if (s.id().equals(seriesId))
				return s.fullSeriesStream();
		}

		throw new RuntimeException(
				new FileNotFoundException("TestStream doesn't have data about that series"));
	}

	@Override
	public InputStream streamForSeriesSearch(String seriesName, String language) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream streamForSeriesPosterAt(String resourcePath) {
		this.checkIfItIsAValidUrlSuffix(resourcePath, "resourcePath");

		for (SampleSeries s : this.allSampleSeries) {
			if (s.posterResourcePath().equals(resourcePath))
				return s.posterStream();
		}

		throw new RuntimeException(
				new FileNotFoundException("TestStream doesn't have data about that series")); 
	}

	private void checkIfItIsAValidUrlSuffix(String suffix, String parameterName) {
		if (suffix == null)
			throw new IllegalArgumentException(parameterName + " should not be null");
		if (suffix.trim().equals(""))
			throw new IllegalArgumentException(parameterName + " should not be blank");
	}

}
