package mobi.myseries.domain.source.trakttv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import mobi.myseries.domain.model.ParcelableSeries;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.shared.Validate;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TraktTv implements TrendingSource, SearchSource {
    private static final int SOCKET_TIMEOUT = 5000;
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final String TRAKT_URL = "http://api.trakt.tv/";
    private static final String TRENDING_URL = TRAKT_URL + "shows/trending.json/";
    private static final String SEARCH_URL = TRAKT_URL + "search/shows.json/";
    private static final int COMPRESSED_300 = 300;

    private final String apiKey;

    /* Interface */

    public TraktTv(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    @Override
    public List<ParcelableSeries> listTrending()
            throws ConnectionFailedException, ParsingFailedException {
        String url = TRENDING_URL + this.apiKey;

        return this.parcelableSeriesListFrom(this.unmarshall(this.get(url)));
    }

    @Override
    public List<ParcelableSeries> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException {
        Validate.isNonBlank(query, new InvalidSearchCriteriaException());

        String url = SEARCH_URL + this.apiKey + "/" + this.encode(query);

        return this.parcelableSeriesListFrom(this.unmarshall(this.get(url)));
    }

    /* Auxiliary */

    private List<ParcelableSeries> parcelableSeriesListFrom(JSONArray array) {
        List<ParcelableSeries> parcelableSeriesList = new ArrayList<ParcelableSeries>(array.size());

        for (Object o : array) {
            try {
                parcelableSeriesList.add(this.parcelableSeriesFrom((JSONObject) o));
            } catch (Exception e) {
                continue;
            }
        }

        return parcelableSeriesList;
    }

    private ParcelableSeries parcelableSeriesFrom(JSONObject object) {
        String poster = ((JSONObject) object.get("images")).get("poster").toString();

        return new ParcelableSeries()
            .setTvdbId(object.get("tvdb_id").toString())
            .setTitle(object.get("title").toString())
            .setOverview(object.get("overview").toString())
            .setPoster(this.compressedPosterUrl(poster, COMPRESSED_300));
    }

    private JSONArray unmarshall(InputStream content) throws ParsingFailedException {
        try {
            return (JSONArray) new JSONParser().parse(new BufferedReader(new InputStreamReader(content)));
        } catch (IOException e) {
            throw new ParsingFailedException(e);
        } catch (ParseException e) {
            throw new ParsingFailedException(e);
        }
    }

    private InputStream get(String url) throws ConnectionFailedException {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpParams params = client.getParams();

            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        try {
            return client
                .execute(new HttpGet(url))
                .getEntity()
                .getContent();
        } catch (Exception e) {
            throw new ConnectionFailedException(e);
        }
    }

    private String compressedPosterUrl(String poster, int size) {
        int extensionIndex = poster.lastIndexOf(".");

        if (extensionIndex == -1) { return poster; }

        return new StringBuilder()
            .append(poster.substring(0, extensionIndex))
            .append("-" + size)
            .append(poster.substring(extensionIndex))
            .toString();
    }

    private String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException should never be thrown by " + this.getClass().getName(), e);
        }
    }
}
