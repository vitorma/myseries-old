/*
 *   SeenEpisodesBar.java
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

package mobi.myseries.gui.shared;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.imageprocessing.LinearGradient;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SeenEpisodesBar extends LinearLayout {

    public SeenEpisodesBar(Context context) {
        this(context, null);
    }

    public SeenEpisodesBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setOrientation(LinearLayout.HORIZONTAL);
    }

    public void updateWithEpisodesOf(Season season) {
        this.updateWith(season.episodes());
    }

    public void updateWithEpisodesOf(Series series) {
        List<Episode> episodes = series.episodes();

        boolean[] parts = new boolean[episodes.size()];

        int[] stops = new int[series.seasons().numberOfSeasons()];

        int specialEpisodes = 0;
        if (series.season(0) != null) {
            specialEpisodes = series.season(0).numberOfEpisodes();
            stops[series.seasons().numberOfSeasons() - 1] = series.season(0).numberOfEpisodes();
        }

        for (int i = specialEpisodes; i < (episodes.size() + specialEpisodes); ++i) {
            parts[i - specialEpisodes] = episodes.get(i % episodes.size()).wasSeen();
        }

        for (int i = 1; i < series.seasons().numberOfSeasons(); ++i) {
            stops[i - 1] = series.seasonAt(i).numberOfEpisodes();
        }

        ChunkBar chunkBar = new ChunkBar(this.getContext());
        chunkBar.setParts(parts);
        chunkBar.setStops(stops);
        chunkBar.setBackgroundGradient(new LinearGradient()
                .from(this.getResources().getColor(R.color.chunk_bar_background_gradient_from))
                .to(this.getResources().getColor(R.color.chunk_bar_background_gradient_to))
                );

        chunkBar.setForegroundGradient(new LinearGradient()
                .from(this.getResources().getColor(R.color.chunk_bar_foreground_gradient_from))
                .to(this.getResources().getColor(R.color.chunk_bar_foreground_gradient_to))
                );

        this.removeAllViews();
        this.addView(chunkBar);
    }

    private void updateWith(List<Episode> episodes) {
        boolean[] parts = new boolean[episodes.size()];

        for (int i = 0; i < episodes.size(); ++i) {
            parts[i] = episodes.get(i).wasSeen();
        }

        ChunkBar chunkBar = new ChunkBar(this.getContext());
        chunkBar.setParts(parts);
        chunkBar.setBackgroundColor(this.getResources()
                .getColor(R.color.chunk_bar_background_plain));
        chunkBar.setForegroundColor(this.getResources()
                .getColor(R.color.chunk_bar_foreground_plain));

        this.removeAllViews();
        this.addView(chunkBar);
    }
}
