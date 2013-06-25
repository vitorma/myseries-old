package mobi.myseries.application.backup.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Time;
import mobi.myseries.shared.WeekDay;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SeriesAdapter implements JsonSerializer<Series>, JsonDeserializer<Series> {
    private static final String SERIES_ID = "id";
    private static final String SERIES_NAME = "name";
    private static final String SERIES_STATUS = "status";
    private static final String SERIES_AIRDAY = "airday";
    private static final String SERIES_AIRTIME = "airtime";
    private static final String SERIES_AIRDATE = "airdate";
    private static final String SERIES_RUNTIME = "runtime";
    private static final String SERIES_NETWORK = "network";
    private static final String SERIES_OVERVIEW = "overview";
    private static final String SERIES_GENRES = "genres";
    private static final String SERIES_ACTORS = "actors";
    private static final String SERIES_POSTER = "poster";
    private static final String SERIES_LASTUPDATE = "lastUpdate";
    private static final Date DEFAULT_AIRDATE = null;
    private static final Time DEFAULT_AIRTIME = null;
    private static final WeekDay DEFAULT_AIRDAY = null;

    @Override
    public JsonElement serialize(Series series, Type type,
            JsonSerializationContext context) {
        JsonObject seriesJson = new JsonObject();
        seriesJson.addProperty(SERIES_ID, series.id());
        seriesJson.addProperty(SERIES_NAME, series.name());
        seriesJson.addProperty(SERIES_STATUS, series.status().name());
        seriesJson.addProperty(SERIES_AIRDAY, Numbers.parseLong(series.airDay(), null));
        seriesJson.addProperty(SERIES_AIRTIME, Numbers.parseLong(series.airtime(), null));
        seriesJson.addProperty(SERIES_AIRDATE, Numbers.parseLong(series.airDate(), null));
        seriesJson.addProperty(SERIES_RUNTIME, series.runtime());
        seriesJson.addProperty(SERIES_NETWORK, series.network());
        seriesJson.addProperty(SERIES_OVERVIEW, series.overview());
        seriesJson.addProperty(SERIES_GENRES, series.genres());
        seriesJson.addProperty(SERIES_ACTORS, series.actors());
        seriesJson.addProperty(SERIES_POSTER, series.posterFileName());
        seriesJson.addProperty(SERIES_LASTUPDATE, series.lastUpdate());
        JsonArray episodesArray = new JsonArray();
        seriesJson.add("episodes", episodesArray);
        for (Episode episodes : series.episodes()) {
            episodesArray.add(context.serialize(episodes));
        }
        return seriesJson;
    }

    @Override
    public Series deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject seriesJson = jsonElement.getAsJsonObject();
        Collection<Episode> episodes = new LinkedList<Episode>();
        for (JsonElement episodeJson : seriesJson.getAsJsonArray("episodes")) {
            Episode e = context.deserialize(episodeJson, Episode.class);
            episodes.add(e);
        }
        return Series.builder()
                .withId(seriesJson.get(SERIES_ID).getAsInt())
                .withName(seriesJson.get(SERIES_NAME).getAsString())
                .withStatus(Status.from(seriesJson.get(SERIES_STATUS).getAsString()))
                .withAirDay(DatesAndTimes.parse(seriesJson.get(SERIES_AIRDAY).getAsLong(), DEFAULT_AIRDAY))
                .withAirtime(DatesAndTimes.parse(seriesJson.get(SERIES_AIRTIME).getAsLong(), DEFAULT_AIRTIME))
                .withAirDate(DatesAndTimes.parse(seriesJson.get(SERIES_AIRDATE).getAsLong(), DEFAULT_AIRDATE))
                .withRuntime(seriesJson.get(SERIES_RUNTIME).getAsString())
                .withNetwork(seriesJson.get(SERIES_NETWORK).getAsString())
                .withOverview(seriesJson.get(SERIES_OVERVIEW).getAsString())
                .withGenres(seriesJson.get(SERIES_GENRES).getAsString())
                .withActors(seriesJson.get(SERIES_ACTORS).getAsString())
                .withPosterFileName(seriesJson.get(SERIES_POSTER).getAsString())
                .withLastUpdate(seriesJson.get(SERIES_LASTUPDATE).getAsLong())
                .build()
                .includingAll(episodes);
    }

}