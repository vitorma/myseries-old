package br.edu.ufcg.aweseries.test.matchers;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesMatchers {

	public static SeriesIdMatcher hasId(int seriesId) {
		return new SeriesIdMatcher(seriesId);
	}

	public static SeriesNameMatcher namedAs(String seriesName) {
		return new SeriesNameMatcher(seriesName);
	}

	public static SeriesOverviewMatcher overviewedAs(String seriesOverview) {
		return new SeriesOverviewMatcher(seriesOverview);
	}

	public static class SeriesIdMatcher extends TypeSafeMatcher<Series> {

		private int seriesId;

		public SeriesIdMatcher(int seriesId) {
			this.seriesId = seriesId;
		}

		@Override
		protected boolean matchesSafely(Series item) {
			return item.id() == this.seriesId;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a Series with id ");
			description.appendValue(this.seriesId);
		}
	}

	public static class SeriesNameMatcher extends TypeSafeMatcher<Series> {

		private String seriesName;

		public SeriesNameMatcher(String seriesName) {
			this.seriesName = seriesName;
		}

		@Override
		protected boolean matchesSafely(Series item) {
			return (item.name().equals(this.seriesName));
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a Series named as ");
			description.appendValue(this.seriesName);
		}
	}

	public static class SeriesOverviewMatcher extends TypeSafeMatcher<Series> {

		private String seriesOverview;

		public SeriesOverviewMatcher(String seriesOverview) {
			this.seriesOverview = seriesOverview;
		}

		@Override
		protected boolean matchesSafely(Series item) {
			return (item.overview().equals(this.seriesOverview));
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("a Series overviewed as ");
			description.appendValue(this.seriesOverview);
		}
	}
}
