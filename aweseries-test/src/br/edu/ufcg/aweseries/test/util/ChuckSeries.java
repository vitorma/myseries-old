package br.edu.ufcg.aweseries.test.util;

import java.io.InputStream;

import android.content.res.Resources;
import br.edu.ufcg.aweseries.test.R;

public class ChuckSeries {

    public static String id = "80348";
    public static String name = "Chuck";
    public static String posterResourcePath = "posters/80348-16.jpg";

    public static Resources resources;

    public static InputStream baseSeriesStream() {
        return resources.openRawResource(R.raw.chuck_base_series);
    }

    public static InputStream fullSeriesStream() {
        return resources.openRawResource(R.raw.chuck_full_series);
    }

    public static InputStream posterStream() {
        return resources.openRawResource(R.raw.chuck_poster_16);
    }
}

