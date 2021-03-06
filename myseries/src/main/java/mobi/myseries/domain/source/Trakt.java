package mobi.myseries.domain.source;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.util.Pair;
import com.google.gson.internal.LinkedTreeMap;
import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;

import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Trakt implements TraktApi {
    public static final String TAG = Trakt.class.getName();
    public static final int N_THREADS = 6;
    public static final int MAX_TRIES = 5;
    private static final String TRAKT_PROTOCOL = "https";
    private static final String TRAKT = "api-v2launch.trakt.tv";
    private static final String TRENDING = "trending";
    private static final String SEARCH = "search";
    private static final String SHOWS = "shows";
    private static final String SHOW = "show";
    private static final String UPDATES = "updates";
    private static final String SEASONS = "seasons";
    private static final String EPISODES = "episodes";
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
            e.printStackTrace();
            return new ArrayList<>();
        }

        String url = searchUri(query).toString();
        Log.d(Trakt.class.getName(), "SEARCH URL: " + url);
        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri searchUri(String query) {
        return traktUriBuilder()
                .appendPath(SEARCH)
                .appendQueryParameter("query", query)
                .appendQueryParameter("type", SHOW)
                .appendQueryParameter("extended", "full,images")
                .appendQueryParameter("limit", "100")
                .build();
    }

    @Override
    public List<SearchResult> listTrending() throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        String url = trendingUri().toString();
        Log.d("DELETE THIS LOG", this.get(url).toString());
        return TraktParser.parseSearchResults(this.get(url));
    }

    private Uri trendingUri() {
        return traktUriBuilder()
                .appendPath(SHOWS)
                .appendPath(TRENDING)
                .appendQueryParameter("extended", "full,images")
                .appendQueryParameter("limit", "100")
                .build();
    }

    @Override
    public Series fetchSeries(int seriesId) throws ParsingFailedException, ConnectionFailedException, NetworkUnavailableException {
        String seriesUrl = showSummaryUri(seriesId).toString();
        Log.d(TAG, "FETCHING SERIES FROM:" + seriesUrl);

        String seasonsUrl = showSeasonsUri(seriesId).toString();
        Log.d(TAG, "FETCHING SEASONS FROM:" + seasonsUrl);

        final Series series = TraktParser.parseSeries(this.get(seriesUrl));

        List<Pair<Integer, Integer>> seasons = TraktParser.parseSeasons(this.get(seasonsUrl));

        ExecutorService taskExecutor = Executors.newFixedThreadPool(N_THREADS);
        for (final Pair<Integer, Integer> season : seasons) {

            for (int i = 1; i <= season.second; ++i) {
                final int episodeNumber = i;

                taskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String episodeUrl = showEpisodesUri(series.id(), season.first, episodeNumber).toString();

                            for (int tries = 1; tries <= MAX_TRIES; ++tries) {
                                try {
                                    Episode.Builder episodeBuilder = TraktParser.parseEpisode(Trakt.this.get(episodeUrl));
                                    series.seasons().include(episodeBuilder.withSeriesId(series.id()).build());
                                    break;

                                } catch (ConnectionFailedException | NetworkUnavailableException e) {
                                    if (tries >= MAX_TRIES) {
                                        throw e;

                                    } else {
                                        e.printStackTrace();

                                        Log.d(TAG, "FETCHING EPISODE: " + episodeUrl + " FAILED. Retrying..." + 2);

                                    }
                                }
                            }
                        } catch (ParsingFailedException | ConnectionFailedException | NetworkUnavailableException e) {
                            e.printStackTrace();

                        }
                    }
                });
            }
        }

        taskExecutor.shutdown();

        try {
            taskExecutor.awaitTermination(2, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return series;
    }

    private Uri showEpisodesUri(int seriesId, Integer seasonNumber, Integer episodeNumber) {
        return traktUriBuilder()
                .appendPath(SHOWS)
                .appendPath(String.valueOf(seriesId))
                .appendPath(SEASONS)
                .appendPath(String.valueOf(seasonNumber))
                .appendPath(EPISODES)
                .appendPath(String.valueOf(episodeNumber))
                .appendQueryParameter("extended", "full,images")
                .build();
    }

    private Uri showSummaryUri(int seriesId) {
        return traktUriBuilder()
                .appendPath(SHOWS)
                .appendPath(String.valueOf(seriesId))
                .appendQueryParameter("extended", "full,images")
                .build();
    }

    private Uri showSeasonsUri(int seriesId) {
        return traktUriBuilder()
                .appendPath(SHOWS)
                .appendPath(String.valueOf(seriesId))
                .appendPath(SEASONS)
                .appendQueryParameter("extended", "full")
                .build();
    }

    @Override
    public List<Integer> updatedSeriesSince(long utcTimestamp)
            throws ConnectionFailedException, ParsingFailedException, NetworkUnavailableException {
        String url = updateUri(utcTimestamp).toString();
        Log.d(Trakt.class.getName(), "UPDATE URL: " + url);

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
        Map<String, String> headers = new LinkedTreeMap<String, String>();
        headers.put(CONTENT_HEADER_KEY, CONTENT_HEADER_VALUE);
        headers.put(API_KEY_HEADER_KEY, this.apiKey);
        headers.put(API_VERSION_HEADER_KEY, API_VERSION_HEADER_VALUE);
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
