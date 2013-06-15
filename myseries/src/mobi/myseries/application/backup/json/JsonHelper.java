package mobi.myseries.application.backup.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;

public class JsonHelper {
    
    public static String toJson(Collection<Series> series) {
        return gson().toJson(series);
    }
    
    public static void toJson(Collection<Series> series, File file) throws FileNotFoundException {
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        gson().toJson(series, writer);
    }
    
    public static Collection<Series> fromJson(String json) {
        Type collectionType = new TypeToken<Collection<Series>>(){}.getType();
        return gson().fromJson(json, collectionType);
    }

    private static Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Series.class, new SeriesAdapter());
        gsonBuilder.registerTypeAdapter(Episode.class, new EpisodeAdapter());
        return gsonBuilder.create();
    }

}
