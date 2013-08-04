package mobi.myseries.application.backup.json;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.FilesUtil;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonHelper {
    private static final String INDENT = "    ";
    
    private static  Gson gson;
    
    public static void preferencesToJson(Map<String, ?> peferences, File file) throws IOException {
        String json = gson().toJson(peferences);
        FilesUtil.writeStringToFile(file.getAbsolutePath(), json);
    }
    
    public static Map<String, ?> preferencesFromJson(String json) {
        Type collectionType = new TypeToken<Map<String, ?>>(){}.getType();
        return gson().fromJson(json, collectionType);
    }
    

    private static Gson gson() {
        if(gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Series.class, new SeriesAdapter());
            gsonBuilder.registerTypeAdapter(Episode.class, new EpisodeAdapter());
            gson = gsonBuilder.create();
        }
        return gson;
    }
    
    public static void writeSeriesJsonStream(OutputStream out, Collection<Series> series) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));
        writer.beginArray();
        writer.setIndent(INDENT);
        for (Series s : series) {
            Log.v("json", "serializing " + s.name());
            gson().toJson(s, Series.class, writer);
        }
        writer.endArray();
        writer.close();
        out.close();
    }
    
    public static Collection<Series> readSeriesJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        Collection<Series> series = new ArrayList<Series>();
        reader.beginArray();
        while (reader.hasNext()) {
            Series s = gson().fromJson(reader, Series.class);
            series.add(s);
            Log.v("json", "deserialized " + s.name());
        }
        reader.endArray();
        reader.close();
        in.close();
        return series;
    }
}
