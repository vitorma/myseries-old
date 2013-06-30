import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import android.util.Log;

public class FanartTv {
    private static final String API_KEY = "cfae8496b22bc0d4400c267f7ea243d8";
    private static final String FANART_URL = "http://api.fanart.tv/webservice/series/" + API_KEY + "/";
    private static final String FORMAT = "/json";
    private static final String TYPE = "/tvthumb";
    private static final String SORT = "/2";
    private static final String LIMIT = "/1/";

    public String getBannerUrl(int seriesId) {
        try {
            return this.responseUrl(this.unmarshall(this.get(this.requestUrl(seriesId))));
        } catch (Exception e) {
            Log.d(this.getClass().toString(), "Failed getting banner url for series " + seriesId);

            return "";
        }
    }

    private String responseUrl(JSONArray array) {
        return array.get(0).toString();
    }

    private String requestUrl(int seriesId) {
        return FANART_URL + seriesId + FORMAT + TYPE + SORT + LIMIT;
    }

    private JSONArray unmarshall(InputStream content) {
        try {
            return (JSONArray) new JSONParser().parse(new BufferedReader(new InputStreamReader(content)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream get(String url) {
        try {
            return new DefaultHttpClient()
                .execute(new HttpGet(url))
                .getEntity()
                .getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
