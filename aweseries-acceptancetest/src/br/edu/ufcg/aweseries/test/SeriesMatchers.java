package br.edu.ufcg.aweseries.test;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesMatchers {

    public static SeriesNameMatcher namedAs(String seriesName) {
        return new SeriesNameMatcher(seriesName);
    }

    private static class SeriesNameMatcher extends TypeSafeMatcher<Series> {

        private String seriesName;

        public SeriesNameMatcher(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        protected boolean matchesSafely(Series item) {
            return (item.getName().equals(this.seriesName));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a Series named as ");
            description.appendValue(this.seriesName);
        }
    }
}
