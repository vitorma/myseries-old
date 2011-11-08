package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.model.Episode;

public class RecentEpisodesActivity extends OutOfContextEpisodesActivity {

    private static final EpisodeComparator comparator = new EpisodeComparator();

    @Override
    protected List<Episode> episodes() {
        return App.environment().seriesProvider().recentNotSeenEpisodes();
    }

    @Override
    protected Comparator<Episode> episodesComparator() {
        return comparator;
    }

    //Episode comparator------------------------------------------------------------------------------------------------
    private static class EpisodeComparator implements Comparator<Episode> {
        @Override
        public int compare(Episode episodeA, Episode episodeB) {
            int byDate = episodeB.compareByDateTo(episodeA);
            return byDate == 0 ? episodeB.compareByNumberTo(episodeA) : byDate;
        }
    };
}
