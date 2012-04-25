/*
 *   SeriesListAdapter.java
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

package mobi.myseries.gui;

import java.util.ArrayList;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SeriesListAdapter extends ArrayAdapter<Series> {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.environment().imageProvider();
    private static final int ITEM_LAYOUT = R.layout.my_series_list_item;

    private LayoutInflater layoutInflater;

    //Construction------------------------------------------------------------------------------------------------------

    public SeriesListAdapter(Context context) {
       super(context, ITEM_LAYOUT, new ArrayList<Series>(SERIES_PROVIDER.followedSeries()));
       this.layoutInflater = LayoutInflater.from(context);
    }

    //Getting view------------------------------------------------------------------------------------------------------

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
        }

        ImageView image = (ImageView) itemView.findViewById(R.id.item_poster);
        TextView name = (TextView) itemView.findViewById(R.id.item_name);
        TextView nextToSee = (TextView) itemView.findViewById(R.id.item_next_to_see);
        CheckBox seenMark = (CheckBox) itemView.findViewById(R.id.item_seen_mark);

        Series item = this.getItem(position);

        image.setImageBitmap(IMAGE_PROVIDER.getPosterOf(item));
        name.setText(item.name());

        final Episode nextEpisodeToSee = item.nextEpisodeToSee(true);//TODO SharedPreference
        if (nextEpisodeToSee != null) {
            nextToSee.setText(Objects.nullSafe(nextEpisodeToSee.name(), this.getContext().getString(R.string.unnamed_episode)));
            seenMark.setEnabled(true);
            seenMark.setChecked(nextEpisodeToSee.wasSeen());
            seenMark.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    SERIES_PROVIDER.markEpisodeAsSeen(nextEpisodeToSee);
                }
            });
        } else {
            nextToSee.setText(R.string.up_to_date);
            seenMark.setChecked(false);
            seenMark.setEnabled(false);
        }

        return itemView;
    }
}
