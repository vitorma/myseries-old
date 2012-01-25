/*
 *   AppDriver.java
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.model.Season;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.util.SampleSeries;

import com.jayway.android.robotium.solo.Solo;

public class AppDriver {

	// TODO: it should iterate all over the SeriesSample samples, storing their data
	private Map<String, Integer> seriesNameToId = new HashMap<String, Integer>();
	{
		this.seriesNameToId.put(SampleSeries.CHUCK.series().name(),
				SampleSeries.CHUCK.series().id()); 
	}

	private Solo solo;
	private Map<String, Series> series;

	public AppDriver(Solo robotiumSolo) {
		if (robotiumSolo == null)
			throw new IllegalArgumentException("robotiumSolo should not be null");
		this.solo = robotiumSolo;

		this.series = new HashMap<String, Series>();
	}

	// Full Actions ------------------------------------------------------------
	public void follow(String seriesName) {
		this.validateInputName(seriesName, "seriesName");

		//FIXME How can you get a series from the repository before the insertion?
		Series series = this.retrieveSeriesNamed(seriesName);

		this.seriesProvider().follow(series);
		this.saveReferenceTo(series, seriesName);

		// XXX: This is a workarond because there is no way to navigate through the acivities in
		// order to start following an activity. It should be resolved as soon as there is a way
		// to do it through user interaction
		this.restartCurrentActivity();
	}

	private void restartCurrentActivity() {
		Activity currentActivity = this.solo.getCurrentActivity();
		Intent currentActivityIntent = currentActivity.getIntent();

		currentActivity.finish();
		currentActivity.startActivity(currentActivityIntent);
	}

	private Series retrieveSeriesNamed(String seriesName) {
		return this.seriesProvider().getSeries(this.seriesNameToId.get(seriesName));
	}

	private void saveReferenceTo(Series series, String seriesName) {
		this.series.put(seriesName, series);
	}

	private Series seriesReferencedAs(String seriesName) {
		if (!this.series.containsKey(seriesName))
			throw new IllegalArgumentException("Series not followed yet");

		return this.series.get(seriesName);
	}

	// Navigation --------------------------------------------------------------
	public void viewMyFollowedSeries() {
		if (!"SeriesListActivity".equals(this.solo.getCurrentActivity().getClass().getSimpleName())) {
			this.solo.goBackToActivity("SeriesListActivity");
		}
	}
	public void viewDetailsOf(String seriesName) {
		this.validateInputName(seriesName, "seriesName");

		this.viewMyFollowedSeries();
		this.solo.clickOnText(this.seriesReferencedAs(seriesName).name());
	}

	public void viewSeasonsOf(String seriesName) {
		this.validateInputName(seriesName, "seriesName");

		this.viewDetailsOf(seriesName);
		this.solo.clickOnText("Seasons");
	}

	public void viewEpisodesOf(String seriesName, String seasonName) {
		this.validateInputName(seriesName, "seriesName");
		this.validateInputName(seasonName, "seasonName");

		this.viewSeasonsOf(seriesName);
		this.solo.clickOnText(seasonName);
	}

	// Verification ------------------------------------------------------------
	public SeriesAccessor assertThatSeries(String seriesName) {
		this.validateInputName(seriesName, "seriesName");

		return new SeriesAccessor(this.seriesReferencedAs(seriesName));
	}

	private abstract class Accessor {
		protected TextAsserter asserterTo(String text) {
			return new TextAsserter(text);
		}
	}

	public class SeriesAccessor extends Accessor {

		private Series series;

		public SeriesAccessor(Series series) {
			if (series == null)
				throw new IllegalArgumentException("series should not be null");

			this.series = series;
		}

		public TextAsserter name() {
			return this.asserterTo(this.series.name()); 
		}

		public TextAsserter status() {
			return this.asserterTo(this.series.status()); 
		}

		public SeasonAccessor season(String seasonName) {
			return new SeasonAccessor(seasonName, this.series);
		}
	}

	public class SeasonAccessor extends Accessor  {

		private Season season;

		public SeasonAccessor(String seasonName, Series series) {
			AppDriver.this.validateInputName(seasonName, "seasonName");

			this.season = series.seasons().season(this.nameToNumber(seasonName));
		}

		public TextAsserter name() {
			return this.asserterTo(this.numberToName(this.season.number()));
		}

		private Integer nameToNumber(String seasonName) {
			if ("Special Episodes".equals(seasonName))
				return 0;
			try {
				Integer seriesNumber = Integer.parseInt(seasonName);

				if (seriesNumber == null || seriesNumber < 0)
					throw new IllegalArgumentException("seasonName is not a season name");

				return seriesNumber;
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("seasonName is not a season name");
			}
		}

		private String numberToName(int seasonNumber) {
			return seasonNumber == 0 ? "Special Episodes"
					: "Season " + this.season.number() ;
		}
	}

	public class TextAsserter {
		private String text;

		public TextAsserter(String text) {
			this.text = text;
		}

		public void isShown() {
			assertThat(AppDriver.this.solo.searchText(this.text), equalTo(true));
		}

		public String text() {
			return this.text;
		}
	}

	// Private tools -----------------------------------------------------------
	private SeriesProvider seriesProvider() {
		return App.environment().seriesProvider();
	}

	private void validateInputName(String name, String parameterName) {
		if (name == null)
			throw new IllegalArgumentException(parameterName + " should not be null");
		if (name.trim().equals(""))
			throw new IllegalArgumentException(parameterName + " should not be empty");
	}
}
