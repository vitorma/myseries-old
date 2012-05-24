/*
 *   SeriesSearchItemAdapter.java
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

package mobi.myseries.gui.seriessearch;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Series;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class SeriesSearchItemAdapter extends ArrayAdapter<Series> {
    private final SeriesSearchActivity seriesSearchView;

    public SeriesSearchItemAdapter(SeriesSearchActivity seriesSearchView, Context context,
            int seriesItemResourceId, List<Series> objects) {
        super(context, seriesItemResourceId, objects);
        this.seriesSearchView = seriesSearchView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        // if no view was passed, create one for the item
        if (itemView == null) {
            final LayoutInflater vi = (LayoutInflater) this.seriesSearchView
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = vi.inflate(R.layout.seriessearch_item_deletemeasap, null);
        }

        // get views for the series fields
        final TextView name = (TextView) itemView.findViewById(R.id.itemName);

        // load series data
        final Series item = this.getItem(position);

        name.setText(item.name());

        return itemView;
    }
}
