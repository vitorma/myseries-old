package br.edu.ufcg.aweseries.thetvdb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import br.edu.ufcg.aweseries.thetvdb.season.Seasons;
import br.edu.ufcg.aweseries.thetvdb.season.SeasonsParser;
import br.edu.ufcg.aweseries.thetvdb.series.Series;
import br.edu.ufcg.aweseries.thetvdb.series.SeriesParser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TheTVDB {
    private final UrlSupplier urlSupplier;

    public TheTVDB(String apiKey) {
        this.urlSupplier = new UrlSupplier(apiKey);
    }

    public Series getSeries(int id) {
        String url = this.urlSupplier.getBaseSeriesUrl(id);
        return new SeriesParser(streamFor(url)).parse();
    }
    
    public Seasons getSeasons(int seriesId) {
    	String url = this.urlSupplier.getFullSeriesUrl(seriesId);
    	return new SeasonsParser(streamFor(url)).parse();
    }

    public Bitmap getSeriesPoster(Series series) {
    	String url = this.urlSupplier.getSeriesPosterUrl(series.getPoster());
    	if (url == null) {
    		return null;
    	}
        return bitmapFrom(streamFor(url));
    }

    private InputStream streamFor(String url) {
        try {
            return new URL(url).openConnection().getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private Bitmap bitmapFrom(InputStream stream) {
    	try {
            BufferedInputStream bmpBuffer = new BufferedInputStream(stream);
			
            Bitmap poster = BitmapFactory.decodeStream(bmpBuffer);

            // close buffers
            if (stream != null) {
                stream.close();
            }
            if (bmpBuffer != null) {
                bmpBuffer.close();
            }

	        return poster;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
