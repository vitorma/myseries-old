package br.edu.ufcg.aweseries.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.test.KeyValueParser.KeyValuePair;

public class DefaultSeriesFactory {

    private KeyValueParser keyValueParser = new KeyValueParser();

    public Series createSeries(String... attributes) {
        Map<String, String> values = this.defaultValues();

        for (String attribute : attributes) {
            // parse attribute
            KeyValuePair attributePair = this.keyValueParser.parse(attribute);

            // set attribute
            if (!values.containsKey(attributePair.key)) {
                throw new IllegalArgumentException("Nonexistent attribute key");
            }
            values.put(attributePair.key, attributePair.value);
        }

        return new Series.Builder().withId(values.get("id"))
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
        return String.valueOf(new Random().nextInt());
    }
}
