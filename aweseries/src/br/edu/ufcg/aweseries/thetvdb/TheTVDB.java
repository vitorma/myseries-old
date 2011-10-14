package br.edu.ufcg.aweseries.thetvdb;

import android.graphics.Bitmap;
import android.util.Log;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.parsing.EpisodesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.stream.TheTVDBStreamFactory;
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

    public Series[] search(String seriesName) {
        //TODO: Implement it
        return null;
    }

    public Series getFullSeries(String seriesId) {
        if (seriesId == null) {
            throw new IllegalArgumentException("seriesId should not be null");
        }
        if (Strings.isBlank(seriesId)) {
            throw new IllegalArgumentException("seriesId should not be blank");
        }

        try {
            //TODO: Redesign FullSeriesParser and use it ASAP
            final SeriesParser seriesParser = new SeriesParser(this.streamFactory);
            final Series series = seriesParser.parse(seriesId);
            final EpisodesParser episodesParser = new EpisodesParser(
                    this.streamFactory.streamForFullSeries(seriesId));
            series.getSeasons().addAllEpisodes(episodesParser.parse());
            return series;
        } catch (Exception e) {
            Log.e("TheTVDB", "series coudn't be retrieved from The TVDB server: " + e.getMessage());
            return null;
        }
    }

    @Deprecated
    public Bitmap getPosterOf(Series series) {
        if (series == null) {
            throw new IllegalArgumentException("series should not be null");
        }

        if (!series.hasPoster()) {
            return null;
        }

        return null;
    }

    public Bitmap getPosterOf(Episode episode) {
        //TODO: Implement it
        return null;
    }
}
