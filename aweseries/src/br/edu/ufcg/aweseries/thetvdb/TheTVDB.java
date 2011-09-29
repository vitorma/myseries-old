package br.edu.ufcg.aweseries.thetvdb;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import br.edu.ufcg.aweseries.thetvdb.season.Seasons;
import br.edu.ufcg.aweseries.thetvdb.season.SeasonsParser;
import br.edu.ufcg.aweseries.thetvdb.series.Series;
import br.edu.ufcg.aweseries.thetvdb.series.SeriesParser;

public class TheTVDB {

    private final StreamFactory streamFactory;

    public TheTVDB(String apiKey) {
        this(new TheTVDBStreamFactory(apiKey));
    }

    public TheTVDB(StreamFactory streamFactory) {
        this.streamFactory = streamFactory;
    }
    
    public Series[] search(String seriesName) {
        return null;
    }
    
    public Series addSeasonsTo(Series series) {
        return null;
    }
    
    public Bitmap getPosterOf(Series series) {
        return null;
    }
    
    public Series getSeries(String seriesId) {
        return new SeriesParser(streamFactory.streamForBaseSeries(seriesId)).parse();
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
