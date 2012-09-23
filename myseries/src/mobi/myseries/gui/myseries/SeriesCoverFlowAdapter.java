/*
 *   SeriesImageAdapter.java
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

package mobi.myseries.gui.myseries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.image.ImageProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.shared.CoverFlowAdapter;
import android.graphics.Bitmap;

public class SeriesCoverFlowAdapter extends CoverFlowAdapter {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.imageProvider();

    private List<Series> seriesList;

    public SeriesCoverFlowAdapter() {
        this.seriesList = new ArrayList<Series>(SERIES_PROVIDER.followedSeries());
    }

    @Override
    public int getCount() {
        return this.seriesList.size();
    }

    @Override
    protected Bitmap createBitmap(int position) {
        Bitmap bitmap = IMAGE_PROVIDER.getPosterOf(this.seriesList.get(position));
        return bitmap;
    }

    public Series itemOf(int position) {
        return this.seriesList.get(position);
    }

    public void registerSeriesListener(SeriesListener sl) {
        for (Series s : this.seriesList) {
            s.register(sl);
        }
    }

    public SeriesCoverFlowAdapter sort(Comparator<Series> seriesComparator) {
        Collections.sort(this.seriesList, seriesComparator);
        this.notifyDataSetChanged();
        return this;
    }

    @Override
    public boolean isEmpty() {
        return this.seriesList.isEmpty();
    }
}
