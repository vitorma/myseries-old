/*
 *   RecentEpisodesActivity.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */


package br.edu.ufcg.aweseries.gui;

import java.util.Comparator;
import java.util.List;

import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.EpisodeComparator;

public class RecentEpisodesActivity extends OutOfContextEpisodesActivity {

    @Override
    protected List<Episode> episodes() {
        return App.environment().seriesProvider().recentNotSeenEpisodes();
    }

    @Override
    protected Comparator<Episode> episodesComparator() {
        return EpisodeComparator.reversedByAirdateThenBySeasonThenByNumber();
    }
}
