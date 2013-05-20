package mobi.myseries.application;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Publisher;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import android.os.AsyncTask;
import android.util.Log;

public class TrendingSeriesService implements Publisher<TrendingSeriesListener> {
    private static final String API_KEY = "2665c5546c888a02c4ceff0afccfa927";
    private static final String TRENDING_URL = "http://api.trakt.tv/shows/trending.json/";
    private static final String REQUEST_URL = TRENDING_URL + API_KEY;

    private ListenerSet<TrendingSeriesListener> listeners;

    public TrendingSeriesService() {
        this.listeners = new ListenerSet<TrendingSeriesListener>();
    }

    public void downloadTrendingList() {
        new AsyncTask<Void, Void, List<Series>>() {
            @Override
            protected void onPreExecute() {
                TrendingSeriesService.this.notifyStart();
            }

            @Override
            protected List<Series> doInBackground(Void... params) {
                return TrendingSeriesService.this.listTrendingSeries();
            }

            @Override
            protected void onPostExecute(List<Series> result) {
                TrendingSeriesService.this.notifyEnd(result);
            }
        }.execute();
    }

    private List<Series> listTrendingSeries() {
        try {
            return this.seriesListFrom(this.unmarshall(this.get()));
        } catch (Exception e) {
            Log.e(TrendingSeriesService.class.toString(), e.getMessage());

            return new ArrayList<Series>();
        }
    }

    private List<Series> seriesListFrom(JSONArray array) {
        List<Series> result = new ArrayList<Series>(array.size());

        for (Object o : array) {
            try {
                result.add(this.seriesFrom((JSONObject) o));
            } catch (Exception e) {
                Log.e(TrendingSeriesService.class.toString(), e.getMessage());
            }
        }

        return result;
    }

    private Series seriesFrom(JSONObject object) {
        return Series.builder()
            .withId(Integer.valueOf(object.get("tvdb_id").toString()))
            .withName(object.get("title").toString())
            .withOverview(object.get("overview").toString())
            .build();
    }

    private JSONArray unmarshall(InputStream content) {
        try {
            return (JSONArray) new JSONParser().parse(new BufferedReader(new InputStreamReader(content)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream get() {
        try {
            return new DefaultHttpClient()
                .execute(new HttpGet(REQUEST_URL))
                .getEntity()
                .getContent();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Publisher */

    @Override
    public boolean register(TrendingSeriesListener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(TrendingSeriesListener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyStart() {
        for (TrendingSeriesListener tsl: this.listeners) {
            tsl.onStartLoading();
        }
    }

    private void notifyEnd(List<Series> result) {
        for (TrendingSeriesListener tsl: this.listeners) {
            tsl.onFinishLoading(result);
        }
    }
}