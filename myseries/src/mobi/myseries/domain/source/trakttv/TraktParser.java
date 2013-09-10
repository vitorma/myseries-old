package mobi.myseries.domain.source.trakttv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.SearchResult;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.source.ParsingFailedException;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.WeekDay;

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

public class TraktParser {
    private static final String TVDB_ID = "tvdb_id";
    private static final String TITLE = "title";
    private static final String STATUS = "status";
    private static final String AIR_DAY = "air_day_utc";
    private static final String AIR_TIME = "air_time_utc";
    private static final String AIR_DATE = "first_aired_utc";
    private static final String RUNTIME = "runtime";
    private static final String NETWORK = "network";
    private static final String OVERVIEW = "overview";
    private static final String GENRES = "genres";
    private static final String PEOPLE = "people";
    private static final String ACTORS = "actors";
    private static final String NAME = "name";
    private static final String CHARACTER = "character";
    private static final String IMAGES = "images";
    private static final String HEADSHOT = "headshot";
    private static final String POSTER = "poster";
    private static final String SEASONS = "seasons";
    private static final String SEASON = "season";
    private static final String EPISODES = "episodes";
    private static final String EPISODE = "episode";
    private static final String NUMBER = "number";
    private static final String SCREEN = "screen";

    private static final int COMPRESSED_POSTER_300 = 300;

    private static final JsonDeserializer<Series> SERIES_ADAPTER = new JsonDeserializer<Series>() {
        @Override
        public Series deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject seriesObject = element.getAsJsonObject();

            int seriesId = readTvdbId(seriesObject);
            Time airTime = readAirTime(seriesObject);

            Series.Builder seriesBuilder = Series.builder()
                    .withTvdbId(readTvdbId(seriesObject))
                    .withTitle(readTitle(seriesObject))
                    .withStatus(readStatus(seriesObject))
                    .withAirDay(readAirDay(seriesObject))
                    .withAirTime(readAirTime(seriesObject))
                    .withAirDate(readAirDate(seriesObject))
                    .withRuntime(readRuntime(seriesObject))
                    .withNetwork(readNetwork(seriesObject))
                    .withOverview(readOverview(seriesObject))
                    .withGenres(readGenres(seriesObject))
                    .withActors(readActors(seriesObject))
                    .withPoster(readPoster(seriesObject));

            JsonArray seasonsArray = seriesObject.getAsJsonArray(SEASONS);

            for (JsonElement seasonElement : seasonsArray) {
                JsonArray episodesArray = seasonElement.getAsJsonObject().getAsJsonArray(EPISODES);

                for (JsonElement episodeElement : episodesArray) {
                    Episode.Builder episodeBuilder = context.deserialize(episodeElement, Episode.Builder.class);
                    Episode episode = episodeBuilder.withSeriesId(seriesId).withAirtime(airTime).build();

                    seriesBuilder.withEpisode(episode);
                }
            }

            return seriesBuilder.build();
        }
    };

    private static final JsonDeserializer<Episode.Builder> EPISODE_ADAPTER = new JsonDeserializer<Episode.Builder>() {
        @Override
        public Episode.Builder deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject episodeElement = element.getAsJsonObject();

            return Episode.builder()
                    .withId(readTvdbId(episodeElement))
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

            return new SearchResult()
                    .setTvdbId(readTvdbIdAsString(resultElement))
                    .setTitle(readTitle(resultElement))
                    .setOverview(readOverview(resultElement))
                    .setGenres(readGenres(resultElement))
                    .setPoster(readPoster(resultElement));
        }
    };

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
            throw new ParsingFailedException(e);
        }
    }

    public static List<SearchResult> parseSearchResults(InputStream in) throws ParsingFailedException {
        try {
            JsonReader reader = new JsonReader(new BufferedReader(new InputStreamReader(in, "UTF-8")));
            List<SearchResult> results = new ArrayList<SearchResult>();

            reader.beginArray();

            while (reader.hasNext()) {
                SearchResult result = gson().fromJson(reader, SearchResult.class);
                results.add(result);
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

    /* Auxiliary */

    private static Gson gson() {
        if (gson == null) {
            gson = new GsonBuilder()
                .registerTypeAdapter(Series.class, SERIES_ADAPTER)
                .registerTypeAdapter(Episode.Builder.class, EPISODE_ADAPTER)
                .registerTypeAdapter(SearchResult.class, SEARCH_RESULT_ADAPTER)
                .create();
        }

        return gson;
    }

    private static int readTvdbId(JsonObject object) {
        return object.get(TVDB_ID).getAsInt();
    }

    private static String readTvdbIdAsString(JsonObject object) {
        return object.get(TVDB_ID).getAsString();
    }

    private static String readTitle(JsonObject object) {
        return readStringSafely(object.get(TITLE));
    }

    private static Status readStatus(JsonObject object) {
        try {
            return Status.from(object.get(STATUS).getAsString());
        } catch (Exception e) {
            return null;
        }
    }

    private static WeekDay readAirDay(JsonObject object) {
        try {
            return WeekDay.valueOf(object.get(AIR_DAY).getAsString());
        } catch (Exception e) {
            return null;
        }
    }

    private static Time readAirTime(JsonObject object) {
        try {
            return Time.valueOf(object.get(AIR_TIME).getAsString());
        } catch (Exception e) {
            return null;
        }
    }

    private static Date readAirDate(JsonObject object) {
        try {
            return new Date(object.get(AIR_DATE).getAsLong());
        } catch (Exception e) {
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
        String posterUrl = readStringSafely(object.get(POSTER));

        return posterUrl.isEmpty() ? posterUrl : compressedPosterUrl(posterUrl, COMPRESSED_POSTER_300);
    }

    private static int readNumber(JsonObject object) {
        return object.get(NUMBER).getAsInt();
    }

    private static int readSeason(JsonObject object) {
        return object.get(SEASON).getAsInt();
    }

    private static String readScreen(JsonObject object) {
        return readStringSafely(object.get(SCREEN));
    }

    private static String readStringSafely(JsonElement object) {
        return Objects.nullSafe(object, new JsonPrimitive("")).getAsString();
    }

    private static String compressedPosterUrl(String poster, int size) {
        int extensionIndex = poster.lastIndexOf(".");

        if (extensionIndex == -1) { return poster; }

        return new StringBuilder()
        .append(poster.substring(0, extensionIndex))
        .append("-" + size)
        .append(poster.substring(extensionIndex))
        .toString();
    }
}
