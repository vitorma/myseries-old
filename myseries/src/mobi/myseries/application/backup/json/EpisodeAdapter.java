package mobi.myseries.application.backup.json;

import java.lang.reflect.Type;
import mobi.myseries.domain.model.Episode;
import android.util.Log;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EpisodeAdapter implements JsonSerializer<Episode>,
        JsonDeserializer<EpisodeSnippet> {
    private static final String EPISODE_ID = "id";

    @Override
    public JsonElement serialize(Episode episode, Type type,
            JsonSerializationContext context) {
        JsonObject episodeJson = new JsonObject();
        episodeJson.addProperty(EPISODE_ID, episode.id());
        return episodeJson;
    }
    @Override
    public EpisodeSnippet deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject episodeJson = jsonElement.getAsJsonObject();
        Log.v("json", "deserialized " + episodeJson.get(EPISODE_ID).getAsLong());
        return EpisodeSnippet.builder()
                .withId(episodeJson.get(EPISODE_ID).getAsLong())
                .build();
    }
}
