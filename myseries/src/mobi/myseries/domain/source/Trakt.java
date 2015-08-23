package mobi.myseries.domain.source;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import android.util.Pair;

import com.google.gson.internal.LinkedTreeMap;

import java.io.InputStream;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mobi.myseries.application.Communications;
import mobi.myseries.application.ConnectionFailedException;
import mobi.myseries.application.NetworkUnavailableException;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Validate;

public class Trakt implements TraktApi {
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
    public static final String TAG = Trakt.class.getName();

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
        String url = trendingUri().toString();
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
        Log.d(TAG, "FETCHING SERIES FROM:" + seriesUrl);

        String seasonsUrl = showSeasonsUri(seriesId).toString();
        Log.d(TAG, "FETCHING SEASONS FROM:" + seasonsUrl);

        Series series = TraktParser.parseSeries(this.get(seriesUrl));

        List<Pair<Integer, Integer>> seasons = TraktParser.parseSeasons(this.get(seasonsUrl));

        List<Episode> episodes = new LinkedList<Episode>();
        for (Pair<Integer, Integer> season : seasons) {
            int seasonNumber = season.first;

            for (int episodeNumber = 1; episodeNumber <= season.second; ++episodeNumber) {
                String episodeUrl = showEpisodesUri(seriesId, seasonNumber, episodeNumber).toString();
                Log.d(TAG, "FETCHING EPISODE: " + episodeUrl);

                Episode.Builder episode = TraktParser.parseEpisode(this.get(episodeUrl));
                episodes.add(episode.withSeriesId(seriesId).build());
            }

            series.includingAll(episodes);
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
