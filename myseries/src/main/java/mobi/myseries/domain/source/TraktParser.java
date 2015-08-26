package mobi.myseries.domain.source;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.WeekDay;

public class TraktParser {
    private static final String IDS = "ids";
    private static final String TVDB_ID = "tvdb";
    private static final String TRAKT_ID = "trakt";
    private static final String IMDB_ID = "imdb";
    private static final String TMDB_ID = "tmdb";
    private static final String TVRAGE_ID = "tvrage";
    private static final String TITLE = "title";
    private static final String STATUS = "status";
    private static final String AIRS = "airs";
    private static final String AIR_DAY = "day";
    private static final String AIR_TIME = "time";
    private static final String TIMEZONE = "timezone";
    private static final String FIRST_AIRED = "first_aired";
    private static final String RUNTIME = "runtime";
    private static final String NETWORK = "network";
    private static final String OVERVIEW = "overview";
    private static final String GENRES = "genres";
    private static final String PEOPLE = "people";
    private static final String ACTORS = "actors";
    private static final String NAME = "name";
    private static final String IMAGES = "images";
    private static final String POSTERS = "poster";
    private static final String POSTER_MEDIUM = "medium";

    private static final String SEASONS = "seasons";
    private static final String SEASON = "season";
    private static final String EPISODES = "episodes";
    private static final String NUMBER = "number";
    private static final String SCREEN = "screen";
    private static final String FULL = "full";
    private static final String SHOWS = "shows";
    private static final String SHOW = "show";
    private static final String TRAKT_DEFAULT_POSTER_FILENAME = "poster-dark.jpg";
    private static final String TRAKT_DEFAULT_SCREEN_FILENAME = "episode-dark.jpg";

    private static final java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    private static final int COMPRESSED_POSTER_300 = 300;

    private static final JsonDeserializer<Series> SERIES_ADAPTER = new JsonDeserializer<Series>() {
        @Override
        public Series deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject seriesObject = element.getAsJsonObject();

            //TODO(Reul): use a single date to store airday and airtime
            Time time = readAirTime(seriesObject);

            WeekDay airDay = readAirDay(seriesObject);

            Date airtime = null;
            if (time != null && airDay != null) {
                airtime = new Date(airDay.toDate().getTime() + time.toLong());
            }
            Date seriesAirTime = DatesAndTimes.toUtcTime(airtime, readTimeZone(seriesObject));

            Series.Builder seriesBuilder = Series.builder()
                    .withTraktId(readTraktId(seriesObject))
                    .withTitle(readTitle(seriesObject))
                    .withStatus(readStatus(seriesObject))
                    .withAirTime(seriesAirTime)
                    .withAirDate(readAirDate(seriesObject))
                    .withRuntime(readRuntime(seriesObject))
                    .withNetwork(readNetwork(seriesObject))
                    .withOverview(readOverview(seriesObject))
                    .withGenres(readGenres(seriesObject))
//                    .withActors(readActors(seriesObject))
                    .withPoster(readPoster(seriesObject));

            return seriesBuilder.build();
        }
    };

    private static final JsonDeserializer<Season> SEASON_ADAPTER = new JsonDeserializer<Season>() {
        @Override
        public Season deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject seasonElement = element.getAsJsonObject();

            int seriesId = 0; //TODO

            return new Season(seriesId, readNumber(seasonElement));
        }
    };

    private static final JsonDeserializer<Episode.Builder> EPISODE_ADAPTER = new JsonDeserializer<Episode.Builder>() {
        @Override
        public Episode.Builder deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject episodeElement = element.getAsJsonObject();

            return Episode.builder()
                    .withId(readTraktId(episodeElement))
                    .withNumber(readNumber(episodeElement))
                    .withSeasonNumber(readSeason(episodeElement))
                    .withTitle(readTitle(episodeElement))
                    .withAirDate(readAirDate(episodeElement))
                    .withOverview(readOverview(episodeElement))
                    .withScreenUrl(readScreen(episodeElement));
        }
    };

    private static final JsonDeserializer<SearchResult> SEARCH_RESULT_ADAPTER = new JsonDeserializer<SearchResult>() {
        @Override
        public SearchResult deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject resultElement = element.getAsJsonObject();
            JsonObject showElement = resultElement.get(SHOW).getAsJsonObject();

            return new SearchResult()
                    .setTraktId(readTraktIdAsString(showElement))
                    .setTitle(readTitle(showElement))
                    .setOverview(readOverview(showElement))
//            .setGenres(readGenres(showElement))
                    .setPoster(readPoster(showElement));
        }
    };

    private static final JsonDeserializer<List<Integer>> UPDATE_METADATA_ADAPTER = new JsonDeserializer<List<Integer>>() {
        @Override
        public List<Integer> deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            List<Integer> updateMetadata = new ArrayList<Integer>();

            JsonArray updatedSeriesArray = element.getAsJsonArray();

            for (JsonElement updatedSeries : updatedSeriesArray) {
                int seriesId = updatedSeries.getAsJsonObject()
                        .get(SHOW).getAsJsonObject()
                        .get(IDS).getAsJsonObject()
                        .get(TRAKT_ID).getAsInt();

                String title = updatedSeries.getAsJsonObject()
                        .get(SHOW).getAsJsonObject()
                        .get(TITLE).getAsString();
                updateMetadata.add(seriesId);
            }

            return updateMetadata;
        }
    };

    private static final String EPISODE_COUNT = "episode_count";
    public static final String SCREENSHOT = "screenshot";

    private static Gson gson;

    /* Interface */

    public static Series parseSeries(InputStream in) throws ParsingFailedException {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));

            Series series = gson().fromJson(reader, Series.class);

            reader.close();
            in.close();

            return series;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingFailedException(e);
        }
    }

    public static List<Pair<Integer, Integer>> parseSeasons(InputStream in) throws ParsingFailedException {
        List<Pair<Integer, Integer>> seasons = new ArrayList<Pair<Integer, Integer>>();

        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));

            reader.beginArray();

            while (reader.hasNext()) {
                reader.beginObject();
                int seasonNumber = -1;
                int episodeCount = 0;
                while (reader.hasNext()) {
                    String name = reader.nextName();

                    if (name.equals(NUMBER)) {
                        seasonNumber = reader.nextInt();

                    } else if (name.equals(EPISODE_COUNT)) {
                        episodeCount = reader.nextInt();

                    } else {
                        reader.skipValue();
                    }

                }

                seasons.add(new Pair<Integer, Integer>(seasonNumber, episodeCount));
                reader.endObject();
            }

            reader.close();
            in.close();

            //return seasons;

        } catch (Exception e) {
            e.printStackTrace();
            //TODO: throw new ParsingFailedException(e);
        }

        return seasons;
    }


    public static List<SearchResult> parseSearchResults(InputStream in) throws ParsingFailedException {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
            List<SearchResult> results = new ArrayList<SearchResult>();
            reader.beginArray();

            while (reader.hasNext()) {
                SearchResult result = gson().fromJson(reader, SearchResult.class);
                try {
                    result.toSeries();
                    results.add(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    //Ignore the result if it cannot be converted to a Series object.
                    //TODO(Tiago) Is there a better way to do this?
                }
            }

            reader.endArray();
            reader.close();
            in.close();

            return results;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingFailedException(e);
        }
    }

    public static List<Integer> parseUpdateMetadata(InputStream in) throws ParsingFailedException {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));

            List<Integer> series = gson().fromJson(reader, List.class);

            reader.close();
            in.close();

            return series;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingFailedException(e);
        }
    }

    /* Auxiliary */

    private static Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .registerTypeAdapter(Series.class, SERIES_ADAPTER)
                    .registerTypeAdapter(Episode.Builder.class, EPISODE_ADAPTER)
                    .registerTypeAdapter(Season.class, SEASON_ADAPTER)
                    .registerTypeAdapter(SearchResult.class, SEARCH_RESULT_ADAPTER)
                    .registerTypeAdapter(List.class, UPDATE_METADATA_ADAPTER) //XXX (Cleber) Create a class to encapsulate UpdateMetadata
                    .create();
        }

        return gson;
    }

    private static int readTraktId(JsonObject object) {
        JsonObject idsObject = object.get(IDS).getAsJsonObject();
        return idsObject.get(TRAKT_ID).getAsInt();
    }

    private static String readTraktIdAsString(JsonObject object) {
        JsonObject idsObject = object.get(IDS).getAsJsonObject();
        return idsObject.get(TRAKT_ID).getAsString();
    }

    private static String readTitle(JsonObject object) {
        return readStringSafely(object.get(TITLE));
    }

    private static Status readStatus(JsonObject object) {
        try {
            return Status.from(object.get(STATUS).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static WeekDay readAirDay(JsonObject object) {
        try {
            JsonObject airsObject = object.get(AIRS).getAsJsonObject();
            return WeekDay.valueOf(airsObject.get(AIR_DAY).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Time readAirTime(JsonObject object) {
        try {
            JsonObject airsObject = object.get(AIRS).getAsJsonObject();
            return Time.valueOf(airsObject.get(AIR_TIME).getAsString());
        } catch (Exception e) {
            return null;
        }
    }

    private static TimeZone readTimeZone(JsonObject object) {
        try {
            JsonObject airsObject = object.get(AIRS).getAsJsonObject();
            return TimeZone.getTimeZone(airsObject.get(TIMEZONE).getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Date readAirDate(JsonObject object) {
        try {
            String airDateObject = object.get(FIRST_AIRED).getAsString();
            return DatesAndTimes.parse(airDateObject, df, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String readRuntime(JsonObject object) {
        return readStringSafely(object.get(RUNTIME));
    }

    private static String readNetwork(JsonObject object) {
        return readStringSafely(object.get(NETWORK));
    }

    private static String readOverview(JsonObject object) {
        return readStringSafely(object.get(OVERVIEW));
    }

    //TODO (Cleber) Return List<String>
    private static String readGenres(JsonObject object) {
        StringBuilder builder = new StringBuilder();

        for (JsonElement genre : object.getAsJsonArray(GENRES)) {
            builder.append(genre.getAsString()).append(", ");
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }

    //TODO (Cleber) Return List<Actor>
    private static String readActors(JsonObject object) {
        StringBuilder builder = new StringBuilder();

        JsonArray actorsArray = object.getAsJsonObject(PEOPLE).getAsJsonArray(ACTORS);

        for (JsonElement actorElement : actorsArray) {
            JsonElement nameElement = actorElement.getAsJsonObject().get(NAME);

            if (!nameElement.isJsonNull()) {
                builder.append(nameElement.getAsString()).append(", ");
            }
        }

        if (builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }

        return builder.toString();
    }

    private static String readPoster(JsonObject object) {
        JsonObject imagesObject = object.getAsJsonObject(IMAGES);
        JsonObject postersObject = imagesObject.getAsJsonObject(POSTERS);

        if (postersObject.get(POSTER_MEDIUM).isJsonNull()) {
            return "";
        }

        String posterUrl = readStringSafely(postersObject.get(POSTER_MEDIUM));
        if (isDefaultPoster(posterUrl))
            posterUrl = "";

        return posterUrl;
    }

    private static boolean isDefaultPoster(String posterUrl) {
        return posterUrl.contains(TRAKT_DEFAULT_POSTER_FILENAME);
    }

    private static int readNumber(JsonObject object) {
        return object.get(NUMBER).getAsInt();
    }

    private static int readSeason(JsonObject object) {
        return object.get(SEASON).getAsInt();
    }

    private static String readScreen(JsonObject object) {
        String screenUrl = readStringSafely(object.get(IMAGES).getAsJsonObject().get(SCREENSHOT).getAsJsonObject().get(FULL));
        if (isDefaultScreen(screenUrl))
            screenUrl = "";

        return screenUrl;
    }

    private static boolean isDefaultScreen(String screenUrl) {
        return screenUrl.contains(TRAKT_DEFAULT_SCREEN_FILENAME);
    }


    private static String readStringSafely(JsonElement object) {
        if (object.isJsonNull()) {
            return "";
        }

        return Objects.nullSafe(object, new JsonPrimitive("")).getAsString();
    }

    private static Long toMiliseconds(long seconds) {
        return 1000L * seconds;
    }

    private static String compressedPosterUrl(String poster, int size) {
        int extensionIndex = poster.lastIndexOf(".");

        if (extensionIndex == -1) {
            return poster;
        }

        return new StringBuilder()
                .append(poster.substring(0, extensionIndex))
                .append("-" + size)
                .append(poster.substring(extensionIndex))
                .toString();
    }

    public static Episode.Builder parseEpisode(InputStream in) throws ParsingFailedException {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
            Episode.Builder episode = gson().fromJson(reader, Episode.Builder.class);
            reader.close();
            in.close();

            return episode;

        } catch (Exception e) {
            e.printStackTrace();
            throw new ParsingFailedException(e);
        }
    }
}
