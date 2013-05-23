package mobi.myseries.domain.source;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TraktTv implements TrendingSource {
    private static final String TRENDING_URL = "http://api.trakt.tv/shows/trending.json/";

    private String apiKey;

    public TraktTv(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    @Override
    public List<Series> listTrending() {
        try {
            return this.seriesListFrom(this.unmarshall(this.get()));
        } catch (Exception e) {
            return new ArrayList<Series>();
        }
    }

    private List<Series> seriesListFrom(JSONArray array) {
        List<Series> result = new ArrayList<Series>(array.size());

        for (Object o : array) {
            result.add(this.seriesFrom((JSONObject) o));
        }

        return result;
    }

    private Series seriesFrom(JSONObject object) {
        return Series.builder()
            .withId(Integer.valueOf(object.get("tvdb_id").toString()))
            .withName(object.get("title").toString())
            .withOverview(object.get("overview").toString())
            .withPosterFileName(((JSONObject) object.get("images")).get("banner").toString())
            .build();
    }

    private JSONArray unmarshall(InputStream content) {
        try {
            return (JSONArray) new JSONParser().parse(new BufferedReader(new InputStreamReader(content)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream get() {
        try {
            return new DefaultHttpClient()
                .execute(new HttpGet(TRENDING_URL + this.apiKey))
                .getEntity()
                .getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
