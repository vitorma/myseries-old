package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.model.Episode;

public class UpcomingEpisodesActivity extends OutOfContextEpisodesActivity {

    private static final EpisodeComparator comparator = new EpisodeComparator();

    @Override
    protected List<Episode> episodes() {
        return App.environment().seriesProvider().nextEpisodesToAir();
    }

    @Override
    protected Comparator<Episode> episodesComparator() {
        return comparator;
    }

    //Episode comparator------------------------------------------------------------------------------------------------
    private static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode episodeA, Episode episodeB) {
            return episodeA.compareByDateTo(episodeB);
        }
    };
}
