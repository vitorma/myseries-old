package br.edu.ufcg.aweseries.thetvdb.parsing;

import java.io.InputStream;

import br.edu.ufcg.aweseries.model.Series;

public class FullSeriesParser extends TheTVDBParser<Series> {

    public FullSeriesParser(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public Series parse() {
        final SeriesParser seriesParser = new SeriesParser(this.getInputStream());
        final EpisodesParser episodesParser = new EpisodesParser(this.getInputStream());
        final Series series = seriesParser.parse();
        series.getSeasons().addAllEpisodes(episodesParser.parse());
        return series;
    }
}
