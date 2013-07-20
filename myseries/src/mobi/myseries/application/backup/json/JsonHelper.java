package mobi.myseries.application.backup.json;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.FilesUtil;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonHelper {
    
    public static String toJson(Collection<Series> series) {
        return gson().toJson(series);
    }

    public static void toJson(Collection<Series> series, File file) throws IOException {
        String json = gson().toJson(series);
        FilesUtil.writeStringToFile(file.getAbsolutePath(), json);
    }
    
    public static Collection<Series> fromJson(String json) {
        Type collectionType = new TypeToken<Collection<Series>>(){}.getType();
        return gson().fromJson(json, collectionType);
    }

    private static Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Series.class, new SeriesAdapter());
        gsonBuilder.registerTypeAdapter(Episode.class, new EpisodeAdapter());
        return gsonBuilder.create();
    }

}
