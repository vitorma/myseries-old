/*
 *   SearchAdapter.java
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

package mobi.myseries.gui.addseries;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AddAdapter extends ArrayAdapter<Series> {
    public AddAdapter(Context context, List<Series> objects) {
        super(context, R.layout.addseries_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            final LayoutInflater vi = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = vi.inflate(R.layout.addseries_item, null);
        }

        Series item = this.getItem(position);

        TextView name = (TextView) itemView.findViewById(R.id.itemName);
        name.setText(item.name().toUpperCase());

        ImageView poster = (ImageView) itemView.findViewById(R.id.seriesPoster);
        if (!Strings.isNullOrBlank(item.posterFileName())) {
            Log.d("AddAdapter", item.name() + "=>" + item.posterFileName());

            Picasso.with(this.getContext())
                .load(item.posterFileName())
                .placeholder(R.drawable.generic_poster)
                .error(R.drawable.generic_poster)
                .resizeDimen(R.dimen.banner_width, R.dimen.banner_height)
                .into(poster);
        }

        return itemView;
    }
}
