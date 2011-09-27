package br.edu.ufcg.aweseries.thetvdb;

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
        final String posterPath = series.getPoster();

        if (posterPath == null || posterPath.trim().isEmpty()) {
            return null;
        }

        return BitmapFactory.decodeStream(streamFactory.streamForSeriesPosterAt(posterPath));
    }
}
