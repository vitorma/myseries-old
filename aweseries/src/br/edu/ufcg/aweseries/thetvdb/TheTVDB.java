package br.edu.ufcg.aweseries.thetvdb;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.thetvdb.parsing.EpisodesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesParser;
import br.edu.ufcg.aweseries.thetvdb.parsing.SeriesSearchParser;
import br.edu.ufcg.aweseries.thetvdb.stream.StreamFactory;
import br.edu.ufcg.aweseries.thetvdb.stream.TheTVDBStreamFactory;

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

    public List<Series> search(String seriesName) {
        try {
            final SeriesSearchParser parser = new SeriesSearchParser(this.streamFactory);
            return parser.parse(seriesName);
        } catch (Exception e) {
            return null;
        }
    }

    public Series getFullSeries(String seriesId) {
        try {
            //TODO: Redesign FullSeriesParser and use it ASAP
            final SeriesParser seriesParser = new SeriesParser(this.streamFactory);
            final Series series = seriesParser.parse(seriesId);

            final EpisodesParser episodesParser = new EpisodesParser(
                    this.streamFactory.streamForFullSeries(seriesId));
            series.getSeasons().addAllEpisodes(episodesParser.parse());

            return series;
        } catch (Exception e) {
            return null;
        }
    }
}
