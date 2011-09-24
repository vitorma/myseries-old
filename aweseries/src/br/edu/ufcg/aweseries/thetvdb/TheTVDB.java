package br.edu.ufcg.aweseries.thetvdb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TheTVDB {
    private final UrlSupplier urlSupplier;

    public TheTVDB(String apiKey) {
        this.urlSupplier = new UrlSupplier(apiKey);
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
    	try {
			InputStream bmpStream = streamFor(url);
			BufferedInputStream bmpBuffer = new BufferedInputStream(bmpStream);
			
			Bitmap poster = BitmapFactory.decodeStream(bmpBuffer);
			
			// close buffers
			if (bmpStream != null) {
	         	bmpStream.close();
	        }
	        if (bmpBuffer != null) {
	         	bmpBuffer.close();
	        }
	        
	        return poster;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
