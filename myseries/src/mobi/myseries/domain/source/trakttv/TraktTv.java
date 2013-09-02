package mobi.myseries.domain.source.trakttv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.shared.Validate;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//XXX Throw exceptions
public class TraktTv implements TrendingSource, SearchSource {
    private static final String TRAKT_URL = "http://api.trakt.tv/";
    private static final String TRENDING_URL = TRAKT_URL + "shows/trending.json/";
    private static final String SEARCH_URL = TRAKT_URL + "search/shows.json/";
    private static final int COMPRESSED_300 = 300;

    private final String apiKey;

    public TraktTv(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    @Override
    public List<SearchResult> listTrending() {
        try {
            return this.resultListFrom(this.unmarshall(this.get(TRENDING_URL + this.apiKey)));
        } catch (Exception e) {
            return new ArrayList<SearchResult>();
        }
    }

    @Override
    public List<SearchResult> search(String query) {
        try {
            return this.resultListFrom(this.unmarshall(this.get(SEARCH_URL + this.apiKey + "/" + this.encode(query))));
        } catch (Exception e) {
            return new ArrayList<SearchResult>();
        }
    }

    private List<SearchResult> resultListFrom(JSONArray array) {
        List<SearchResult> result = new ArrayList<SearchResult>(array.size());

        for (Object o : array) {
            try {
                result.add(this.resultFrom((JSONObject) o));
            } catch (Exception e) {
                continue;
            }
        }

        return result;
    }

    private SearchResult resultFrom(JSONObject object) {
        String poster = ((JSONObject) object.get("images")).get("poster").toString();

        return new SearchResult()
            .setTvdbId(object.get("tvdb_id").toString())
            .setTitle(object.get("title").toString())
            .setOverview(object.get("overview").toString())
            .setPoster(this.compressed(poster, COMPRESSED_300));
    }

    private JSONArray unmarshall(InputStream content) {
        try {
            return (JSONArray) new JSONParser().parse(new BufferedReader(new InputStreamReader(content)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream get(String url) {
        try {
            return new DefaultHttpClient()
                .execute(new HttpGet(url))
                .getEntity()
                .getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String compressed(String poster, int size) {
        int extensionIndex = poster.lastIndexOf(".");
        if (extensionIndex == -1) { return poster; }

        StringBuilder builder = new StringBuilder();

        builder.append(poster.substring(0, extensionIndex));
        builder.append("-" + size);
        builder.append(poster.substring(extensionIndex));

        return builder.toString();
    }

    private String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException should never be thrown by " + this.getClass().getName(), e);
        }
    }
}
