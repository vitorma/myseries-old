package br.edu.ufcg.aweseries.thetvdb;

import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.thetvdb.season.Seasons;
import br.edu.ufcg.aweseries.thetvdb.season.SeasonsParser;
import br.edu.ufcg.aweseries.thetvdb.series.Series;
import br.edu.ufcg.aweseries.thetvdb.series.SeriesParser;
import br.edu.ufcg.aweseries.util.Strings;

public class TheTVDB {

    private final StreamFactory streamFactory;

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        if (streamFactory == null) {
            throw new IllegalArgumentException("streamFactory should not be null");
        }
        this.streamFactory = streamFactory;
    }

//These 2 methods will be the only ones called to retrieve series in future-------------------------

    public Series[] search(String seriesName) {
        return null;
    }

    public Series getFullSeries(String seriesId) {
        return new SeriesParser(this.streamFactory.streamForFullSeries(seriesId)).parse();
    }

//--------------------------------------------------------------------------------------------------

    public Series addSeasonsTo(Series series) {
        return null;
    }

    public Bitmap getPosterOf(Series series) {
        return null;
    }

    public Series getSeries(String seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }
        if (Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("seriesId should not be blank");
        }
        try {
            return new SeriesParser(streamFactory.streamForBaseSeries(seriesId)).parse();
        } catch (RuntimeException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                throw new NonExistentSeriesException(e);
            }

            throw e;
        }
    }

    public Seasons getSeasons(String seriesId) {
        return new SeasonsParser(streamFactory.streamForFullSeries(seriesId)).parse();
    }

    @Deprecated
    public Bitmap getSeriesPoster(Series series) {
        final String posterPath = series.getPoster();

        if (posterPath == null || posterPath.trim().isEmpty()) {
            return null;
        }

        return BitmapFactory.decodeStream(streamFactory.streamForSeriesPosterAt(posterPath));
    }
}
