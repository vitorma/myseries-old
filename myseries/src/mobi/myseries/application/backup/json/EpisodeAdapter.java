package mobi.myseries.application.backup.json;

import java.lang.reflect.Type;
import java.util.Date;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Numbers;
import mobi.myseries.shared.Time;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class EpisodeAdapter implements JsonSerializer<Episode>,
        JsonDeserializer<Episode> {
    private static final String EPISODE_ID = "id";
    private static final String EPISODE_SERIES = "series";
    private static final String EPISODE_NUMBER = "number";
    private static final String EPISODE_SEASON = "season";
    private static final String EPISODE_NAME = "name";
    private static final String EPISODE_AIRDATE = "airdate";
    private static final String EPISODE_AIRTIME = "airtime";
    private static final String EPISODE_OVERVIEW = "overview";
    private static final String EPISODE_DIRECTORS = "directors";
    private static final String EPISODE_WRITERS = "writers";
    private static final String EPISODE_GUESTSTARS = "gueststars";
    private static final String EPISODE_IMAGE = "image";
    private static final String EPISODE_SEENMARK = "seenmark";
    private static final Date DEFAULT_AIRDATE = null;
    private static final Time DEFAULT_AIRTIME = null;

    @Override
    public JsonElement serialize(Episode episode, Type type,
            JsonSerializationContext context) {
        JsonObject episodeJson = new JsonObject();
        episodeJson.addProperty(EPISODE_ID, episode.id());
        episodeJson.addProperty(EPISODE_SERIES, episode.seriesId());
        episodeJson.addProperty(EPISODE_NUMBER, episode.number());
        episodeJson.addProperty(EPISODE_SEASON, episode.seasonNumber());
        episodeJson.addProperty(EPISODE_NAME, episode.name());
        episodeJson.addProperty(EPISODE_AIRDATE, Numbers.parseLong(episode.airDate(), null));
        episodeJson.addProperty(EPISODE_AIRTIME, Numbers.parseLong(episode.airtime(), null));
        episodeJson.addProperty(EPISODE_OVERVIEW, episode.overview());
        episodeJson.addProperty(EPISODE_DIRECTORS, episode.directors());
        episodeJson.addProperty(EPISODE_WRITERS, episode.writers());
        episodeJson.addProperty(EPISODE_GUESTSTARS, episode.guestStars());
        episodeJson.addProperty(EPISODE_IMAGE, episode.imageFileName());
        episodeJson.addProperty(EPISODE_SEENMARK, String.valueOf(episode.wasSeen()));
        return episodeJson;
    }
    @Override
    public Episode deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject episodeJson = jsonElement.getAsJsonObject();
        return Episode.builder()
                .withId(episodeJson.get(EPISODE_ID).getAsInt())
                .withSeriesId(episodeJson.get(EPISODE_SERIES).getAsInt())
                .withNumber(episodeJson.get(EPISODE_NUMBER).getAsInt())
                .withSeasonNumber(episodeJson.get(EPISODE_SEASON).getAsInt())
                .withName(episodeJson.get(EPISODE_NAME).getAsString())
                .withAirDate(DatesAndTimes.parse(episodeJson.get(EPISODE_AIRDATE).isJsonNull()? null : episodeJson.get(EPISODE_AIRDATE).getAsLong(), DEFAULT_AIRDATE))
                .withAirtime(DatesAndTimes.parse(episodeJson.get(EPISODE_AIRTIME).isJsonNull()? null : episodeJson.get(EPISODE_AIRTIME).getAsLong(), DEFAULT_AIRTIME))
                .withOverview(episodeJson.get(EPISODE_OVERVIEW).getAsString())
                .withDirectors(episodeJson.get(EPISODE_DIRECTORS).getAsString())
                .withWriters(episodeJson.get(EPISODE_WRITERS).getAsString())
                .withGuestStars(episodeJson.get(EPISODE_GUESTSTARS).getAsString())
                .withImageFileName(episodeJson.get(EPISODE_IMAGE).getAsString())
                .withSeenMark(Boolean.valueOf(episodeJson.get(EPISODE_SEENMARK).getAsBoolean()))
                .build();
    }
}
