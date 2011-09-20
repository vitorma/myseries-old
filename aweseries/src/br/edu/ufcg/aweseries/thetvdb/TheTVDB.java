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

    public Series getSeries(int id) {
        String url = this.urlSupplier.getBaseSeriesUrl(id);
        return new SeriesParser(url).parse();
    }

//    private Banner getSeriesBanner(int seriesId) {
//    	String url = this.urlSupplier.getSeriesBannersUrl(seriesId);
//    	return new BannersParser(url).parse().getSeriesBanner();
//    }

    public Bitmap getSeriesPoster(Series series) {
    	String url = this.urlSupplier.getSeriesPosterUrl(series.getPoster());
    	if (url == null) {
    		return null;
    	}
    	try {
			URL u = new URL(url);
			InputStream bmpStream = u.openConnection().getInputStream();
			BufferedInputStream bmpBuffer = new BufferedInputStream(bmpStream);
			
			Bitmap banner = BitmapFactory.decodeStream(bmpBuffer);
			
			// close buffers
			if (bmpStream != null) {
	         	bmpStream.close();
	        }
	        if (bmpBuffer != null) {
	         	bmpBuffer.close();
	        }
	        
	        return banner;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }

    public Seasons getSeasons(int seriesId) {
    	String url = this.urlSupplier.getFullSeriesUrl(seriesId);
		return new SeasonsParser(url).parse();
    }
}
