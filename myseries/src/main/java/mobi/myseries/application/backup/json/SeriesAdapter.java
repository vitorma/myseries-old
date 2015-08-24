package mobi.myseries.application.backup.json;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Status;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class SeriesAdapter implements JsonSerializer<Series>, JsonDeserializer<SeriesSnippet> {
    private static final String SERIES_ID = "id";

    @Override
    public JsonElement serialize(Series series, Type type,
            JsonSerializationContext context) {
        JsonObject seriesJson = new JsonObject();
        seriesJson.addProperty(SERIES_ID, series.id());
        JsonArray episodesArray = new JsonArray();
        seriesJson.add("episodes", episodesArray);
        for (Episode episode : series.episodes()) {
            if(episode.watched())
                episodesArray.add(context.serialize(episode));
        }
        return seriesJson;
    }

    @Override
    public SeriesSnippet deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject seriesJson = jsonElement.getAsJsonObject();
        Collection<EpisodeSnippet> episodes = new LinkedList<EpisodeSnippet>();
        for (JsonElement episodeJson : seriesJson.getAsJsonArray("episodes")) {
            EpisodeSnippet e = context.deserialize(episodeJson, Episode.class);
            Log.v("json", "deserialized " + e.id());
            episodes.add(e);
        }
        return SeriesSnippet.builder()
                .withTvdbId(seriesJson.get(SERIES_ID).getAsInt())
                .build()
                .includingAll(episodes);
    }

}
