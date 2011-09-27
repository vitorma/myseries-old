package br.edu.ufcg.aweseries.thetvdb;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.thetvdb.season.Seasons;
import br.edu.ufcg.aweseries.thetvdb.season.SeasonsParser;
import br.edu.ufcg.aweseries.thetvdb.series.Series;
import br.edu.ufcg.aweseries.thetvdb.series.SeriesParser;

public class TheTVDB {
    @Deprecated
    private final UrlSupplier urlSupplier;

    private final StreamFactory streamFactory;

    public TheTVDB(String apiKey) {
        // TODO: Create UrlSupplier as a local variable and pass it to the
        // TheTVDBStreamFactory while this class does not receive a
        // StreamFactory as a parameter in its constructor method.
        this.urlSupplier = new UrlSupplier(apiKey);
        this.streamFactory = new TheTVDBStreamFactory(urlSupplier);
    }

    public Series getSeries(String seriesId) {
        return new SeriesParser(streamFactory.streamForBaseSeries(seriesId)).parse();
    }

    public Seasons getSeasons(String seriesId) {
        return new SeasonsParser(streamFactory.streamForFullSeries(seriesId)).parse();
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
