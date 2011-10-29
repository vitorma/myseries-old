package br.edu.ufcg.aweseries.thetvdb;

import java.util.List;

import br.edu.ufcg.aweseries.model.Series;
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
            return new SeriesSearchParser(this.streamFactory).parse(seriesName);
        } catch (Exception e) {
            //TODO A better exception handling
            return null;
        }
    }

    public Series getSeries(String seriesId) {
        try {
            return new SeriesParser(this.streamFactory).parse(seriesId);
        } catch (Exception e) {
            //TODO A better exception handling
            return null;
        }
    }
}
