package mobi.myseries.domain.source;

import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.google.gson.internal.LinkedTreeMap;

public class Trakt implements TraktApi {
    private static final String TRAKT_PROTOCOL = "https";
    private static final String TRAKT = "api-v2launch.trakt.tv";
    private static final String TRENDING = "trending";
    private static final String SEARCH = "search";
    private static final String SHOWS = "shows";
    private static final String SHOW = "show";
    private static final String UPDATES = "updates";

    private static final String CONTENT_HEADER_KEY = "Content-type";
    private static final String CONTENT_HEADER_VALUE = "application/json";

    private static final String API_KEY_HEADER_KEY = "trakt-api-key";

    private static final String API_VERSION_HEADER_KEY = "trakt-api-version";
    private static final String API_VERSION_HEADER_VALUE = "2";

    private final String apiKey;
    private final Communications communications;

    /* Interface */

    public Trakt(Communications communications, String apiKey) {
        Validate.isNonNull(apiKey, "apiKey");
        Validate.isNonNull(communications, "communications");

        this.apiKey = apiKey;
        this.communications = communications;
    }

    @Override
    public List<SearchResult> search(String query)
            throws InvalidSearchCriteriaException, ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        Validate.isNonBlank(query, new InvalidSearchCriteriaException());

        //String normalizedQuery = normalizeQuery(query);
        try {
            Validate.isNonBlank(query, "this query is not supported by trakt");
        } catch (IllegalArgumentException e) {
            return new ArrayList<SearchResult>();
        }

        String url = searchUri(query).toString();
        Log.d("DELETE THIS LOG", url);
        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri searchUri(String query) {
        return traktUriBuilder()
        .appendPath(SEARCH)
        .appendQueryParameter("query", query)
        .appendQueryParameter("type", SHOW)
        .appendQueryParameter("extended", "full,images")
        .build();
    }

    @Override
    public List<SearchResult> listTrending() throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        String url = trendingUri().toString() ;
        Log.d("DELETE THIS LOG", this.get(url).toString());
        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri trendingUri() {
        return traktUriBuilder()
                     .appendPath(SHOWS)
                     .appendPath(TRENDING)
                     .appendQueryParameter("extended", "full,images")
                     .build();
    }

    @Override
    public Series fetchSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException, NetworkUnavailableException {
        String seriesUrl = showSummaryUri(seriesId).toString();
        Series series = TraktParser.parseSeries(this.get(seriesUrl));
//        List<Season> seasons = TraktParser.parseSeasons(this.get(seasonsUrl));
//        for(season : seasons) {
//            s.includingAll(TraktParser.parseEpisode(this.get(EpisodeUrl)))
//        }
//        List<Epis> seasons = TraktParser.parseSeasons(this.get(seasonsUrl));
        return series;
    }

    private Uri showSummaryUri(int seriesId) {
        return traktUriBuilder()
                     .appendPath(SHOWS)
                     .appendPath(String.valueOf(seriesId))
                     .appendQueryParameter("extended", "full,images")
                     .build();
    }

    @Override
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        String url = updateUri(utcTimestamp).toString();
        Log.d("DELETE THIS LOG", url);

        return TraktParser.parseUpdateMetadata(this.get(url));
    }

    private Uri updateUri(long pstTimestamp) {
        return traktUriBuilder()
                     .appendPath(SHOWS)
                     .appendPath(UPDATES)
                     .appendPath(millisecondsToISO8601(pstTimestamp))
                     .build();
    }

    /* Auxiliary */

    private InputStream get(String url) throws ConnectionFailedException, NetworkUnavailableException {
        return this.communications.streamFor(url, this.connectionHeaders());
    }

    private Builder traktUriBuilder() {
        return new Uri.Builder()
        .scheme(TRAKT_PROTOCOL)
        .authority(TRAKT);
    }

    public Map<String, String> connectionHeaders() {
        Map headers = new LinkedTreeMap<String, String>();
        headers.put(CONTENT_HEADER_KEY,CONTENT_HEADER_VALUE);
        headers.put(API_KEY_HEADER_KEY,this.apiKey);
        headers.put(API_VERSION_HEADER_KEY,API_VERSION_HEADER_VALUE);
        return headers;
    }

    //XXX Trakt.tv does not work with milliseconds
    private String dropMillisecondsFromTimeStamp(long timestamp) {
        return String.valueOf(timestamp).substring(0, 10);
    }

    private String millisecondsToISO8601(long timestamp) {
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
        String formattedDate = df.format(timestamp);
        return formattedDate;
    }

    //TODO remove this method
    @Deprecated
    private String normalizeQuery(String string) {
        string = string.trim();
        string = string.replaceAll("&", "and");
        string = string.replaceAll("\\-", " ");
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        string = string.replaceAll("[^a-zA-Z0-9' ]+", "");
        return string;
    }

}
