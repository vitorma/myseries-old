package br.edu.ufcg.aweseries.test.unit.thetvdb.season;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.mockito.Mockito;

import br.edu.ufcg.aweseries.thetvdb.episode.Episode;
import br.edu.ufcg.aweseries.thetvdb.season.Season;
import br.edu.ufcg.aweseries.thetvdb.season.SeasonBuilder;

public class SeasonBuilderTest {
    private SeasonBuilder builder;
    private int number;
    private List<Episode> episodes;
    private Episode episode1;
    private Episode episode2;
    private Episode episode3;

    @Before
    public void setUp() throws Exception {
        this.builder = new SeasonBuilder();
        this.number = 10;
        this.episodes = new ArrayList<Episode>();

        this.episode1 = Mockito.mock(Episode.class);
        this.episode2 = Mockito.mock(Episode.class);
        this.episode3 = Mockito.mock(Episode.class);

        this.episodes.add(this.episode1);
        this.episodes.add(this.episode2);
        this.episodes.add(this.episode3);

        this.builder.withSeasonNumber(this.number);
        this.builder.withEpisodes(this.episodes);
    }

    @Test
    public final void testBuild() {
        Season season = this.builder.build();

        Assert.assertEquals(this.number, season.getNumber());
        Assert.assertThat(season.getEpisodes(),
                JUnitMatchers.hasItems(this.episode1, this.episode2, this.episode3));
        Assert.assertNotSame(this.episodes, season.getEpisodes());
    }

}
