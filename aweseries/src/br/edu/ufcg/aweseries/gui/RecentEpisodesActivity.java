/*
 *   RecentEpisodesActivity.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

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
