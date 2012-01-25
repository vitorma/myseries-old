/*
 *   DefaultSeriesFactory.java
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

package br.edu.ufcg.aweseries.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.KeyValueParser.KeyValuePair;
import br.edu.ufcg.aweseries.util.Numbers;

public class DefaultSeriesFactory {
	private static final int DEFAULT_ID = 0;

	private KeyValueParser keyValueParser = new KeyValueParser();

	public Series createSeries(String... attributes) {
		Map<String, String> values = this.defaultValues();

		for (String attribute : attributes) {
			// parse attribute
			KeyValuePair attributePair = this.keyValueParser.parse(attribute);

			// set attribute
			if (!values.containsKey(attributePair.key))
				throw new IllegalArgumentException("Nonexistent attribute key");
			values.put(attributePair.key, attributePair.value);
		}

		return new Series.Builder()
		.withId(Integer.valueOf(values.get("id")))
		.withName(values.get("name"))
		.withStatus(values.get("status"))
		.withAirsDay(values.get("airsOn"))
		.withAirsTime(values.get("airsAt"))
		.withFirstAired(values.get("firstAired"))
		.withRuntime(values.get("runtime"))
		.withNetwork(values.get("network"))
		.withOverview(values.get("overview"))
		.withGenres(values.get("genres"))
		.withActors(values.get("actors"))
		.build();
	}

	private Map<String, String> defaultValues() {
		Map<String, String> defaultValues = new HashMap<String, String>();

		defaultValues.put("id", this.createRandomId());
		defaultValues.put("name", "Default Series");
		defaultValues.put("status", "Continuing");
		defaultValues.put("airsOn", "Monday");
		defaultValues.put("airsAt", "8:00 PM");
		defaultValues.put("firstAired", "1996-01-01");
		defaultValues.put("runtime", "60");
		defaultValues.put("network", "BBC");
		defaultValues.put("overview", "A default series that has been created");
		defaultValues.put("genres", "Action");
		defaultValues.put("actors", "Wile E. Coyote, Road Runner");
		//String poster

		return defaultValues;
	}

	private String createRandomId() {
		return String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
	}
}
