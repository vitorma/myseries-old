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

    public void updateWithEpisodesOf(Series series) {
        this.updateWith(series.episodes());
    }

    public void updateWithEpisodesOf(Season season) {
        this.updateWith(season.episodes());
    }

    private void updateWith(List<Episode> episodes) {
        boolean[] parts = new boolean[episodes.size()];

        for (int i = 0; i < episodes.size(); ++i) {
            parts[i] = episodes.get(i).wasSeen();
        }

        ChunkBar chunkBar = new ChunkBar(this.getContext());
        chunkBar.setParts(parts);
        chunkBar.setBackgroundColor(this.getResources().getColor(R.color.myseries_style_chunkbar_background));
        chunkBar.setForegroundColor(this.getResources().getColor(R.color.myseries_style_chunkbar_foreground));
        chunkBar.setTextColor(this.getResources().getColor(R.color.myseries_style_chunkbar_textColor));
        chunkBar.setTextBackgroundColor(this.getResources().getColor(R.color.myseries_style_chunkbar_textBackground));

        this.removeAllViews();
        this.addView(chunkBar);
    }
}
