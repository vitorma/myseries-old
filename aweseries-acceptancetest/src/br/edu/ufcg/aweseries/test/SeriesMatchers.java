package br.edu.ufcg.aweseries.test;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import br.edu.ufcg.aweseries.model.Series;

public class SeriesMatchers {

    public static SeriesNameMatcher namedAs(String seriesName) {
        return new SeriesNameMatcher(seriesName);
    }

    private static class SeriesNameMatcher extends BaseMatcher<Series> {

        private String seriesName;

        public SeriesNameMatcher(String seriesName) {
            this.seriesName = seriesName;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof Series)) {
                return false;
            }

            Series series = (Series) item;            
            return (series.getName().equals(this.seriesName));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("a Series named as ");
            description.appendValue(this.seriesName);
        }
    }
}
