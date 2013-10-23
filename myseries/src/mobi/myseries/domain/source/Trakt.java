package mobi.myseries.domain.source;

import java.io.InputStream;
import java.util.List;

import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Validate;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

public class Trakt implements TraktApi {
    private static final String TRAKT_PROTOCOL = "http";
    private static final String TRAKT = "api.trakt.tv";
    private static final String TRENDING_JSON = "trending.json";
    private static final String SEARCH = "search";
    private static final String SHOW_JSON = "shows.json";
    private static final String SHOWS = "shows";
    private static final String SHOW = "show";
    private static final String SUMMARY_JSON = "summary.json";
    private static final String UPDATE_JSON = "updated.json";

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

        String url = searchUri(query).toString();
        Log.d("DELETE THIS LOG", url);
        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri searchUri(String query) {
        return traktUriBuilder()
        .appendPath(SEARCH)
        .appendPath(SHOW_JSON)
        .appendPath(this.apiKey)
        .appendPath(query)
        .build();
    }

    @Override
    public List<SearchResult> listTrending() throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        String url = trendingUri().toString() ;

        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri trendingUri() {
        return traktUriBuilder()
                     .appendPath(SHOWS)
                     .appendPath(TRENDING_JSON)
                     .appendPath(this.apiKey)
                     .build();
    }

    @Override
    public Series fetchSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException, NetworkUnavailableException {
        String url = showSummaryUri(seriesId).toString();

        return TraktParser.parseSeries(this.get(url));
    }

    private Uri showSummaryUri(int seriesId) {
        return traktUriBuilder()
                     .appendPath(SHOW)
                     .appendPath(SUMMARY_JSON)
                     .appendPath(apiKey)
                     .appendPath(String.valueOf(seriesId))
                     .appendPath("extended")
                     .build();
    }

    @Override
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        long pstTimestamp = DatesAndTimes.toPstTime(utcTimestamp);

        String url = updateUri(pstTimestamp).toString();
        Log.d("DELETE THIS LOG", url);

        return TraktParser.parseUpdateMetadata(this.get(url));
    }

    private Uri updateUri(long pstTimestamp) {
        return traktUriBuilder()
                     .appendPath(SHOWS)
                     .appendPath(UPDATE_JSON)
                     .appendPath(this.apiKey)
                     .appendPath(String.valueOf(pstTimestamp))
                     .build();
    }

    /* Auxiliary */

    private InputStream get(String url) throws ConnectionFailedException, NetworkUnavailableException {
        return this.communications.streamFor(url);
    }

    private Builder traktUriBuilder() {
        return new Uri.Builder()
        .scheme(TRAKT_PROTOCOL)
        .authority(TRAKT);
    }
}
