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

    private Banner getSeriesBanner(int seriesId) {
    	String url = this.urlSupplier.getSeriesBannersUrl(seriesId);
    	return new BannersParser(url).parse().getSeriesBanner();
    }

    public Bitmap getBitMap(int seriesId) {
    	String url = this.urlSupplier.getSeriesBannersUrl(seriesId);
    	Banner b = new BannersParser(url).parse().getSeriesBanner();
    	try {
			URL u = new URL(url + "/" + b.getPath());
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
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
    }
}
