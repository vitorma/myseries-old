/*
 *   TextOnlyViewAdapter.java
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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.model.Series;

public final class TextOnlyViewAdapter extends ArrayAdapter<Series> {
    private final SeriesSearchActivity seriesSearchView;

    public TextOnlyViewAdapter(SeriesSearchActivity seriesSearchView, Context context,
            int seriesItemResourceId, Series[] objects) {
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
            itemView = vi.inflate(R.layout.text_only_list_item, null);
        }

        // get views for the series fields
        final TextView name = (TextView) itemView.findViewById(R.id.itemName);

        // load series data
        final Series item = this.getItem(position);

        name.setText(item.getName());

        return itemView;
    }
}
