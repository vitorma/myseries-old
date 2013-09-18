package mobi.myseries.domain.source.trakttv;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ConnectionFailedException;
import mobi.myseries.domain.source.InvalidSearchCriteriaException;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Validate;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class Trakt implements TraktApi {
    private static final int SOCKET_TIMEOUT = 60000;
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final String TRAKT_URL = "http://api.trakt.tv/";
    private static final String TRENDING_URL = TRAKT_URL + "shows/trending.json/";
    private static final String SEARCH_URL = TRAKT_URL + "search/shows.json/";
    private static final String SHOW_SUMMARY_URL = TRAKT_URL + "show/summary.json/";
    private static final String UPDATE_URL = TRAKT_URL + "shows/updated.json/";

    private final String apiKey;

    /* Interface */

    public Trakt(String apiKey) {
        Validate.isNonNull(apiKey, "apiKey");

        this.apiKey = apiKey;
    }

    @Override
    public List<SearchResult> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException {
        Validate.isNonBlank(query, new InvalidSearchCriteriaException());

        String url = SEARCH_URL + this.apiKey + "/" + this.encode(query);

        return TraktParser.parseSearchResults(this.get(url));
    }

    @Override
    public List<SearchResult> listTrending() throws ConnectionFailedException, ParsingFailedException {
        String url = TRENDING_URL + this.apiKey;

        return TraktParser.parseSearchResults(this.get(url));
    }

    @Override
    public Series fetchSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException {
        String url = SHOW_SUMMARY_URL + this.apiKey + "/" + seriesId + "/extended";

        return TraktParser.parseSeries(this.get(url));
    }

    @Override
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException {
        long pstTimestamp = DatesAndTimes.toPstTime(utcTimestamp);

        String url = UPDATE_URL + this.apiKey + "/" + pstTimestamp;
        Log.d("DELETE THIS LOG", url);

        return TraktParser.parseUpdateMetadata(this.get(url));
    }

    /* Auxiliary */

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

    private String encode(String string) {
        try {
            return URLEncoder.encode(string, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException should never be thrown by " + this.getClass().getName(), e);
        }
    }
}
