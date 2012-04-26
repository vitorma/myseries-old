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
import java.util.Collection;
import java.util.Comparator;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.FollowingSeriesListener;
import mobi.myseries.application.ImageProvider;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
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

public class SeriesListAdapter extends ArrayAdapter<Series> implements SeriesListener, FollowingSeriesListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final ImageProvider IMAGE_PROVIDER = App.environment().imageProvider();
    private static final SeriesComparator COMPARATOR = new SeriesComparator();
    private static final int ITEM_LAYOUT = R.layout.series_list_item;

    private LayoutInflater layoutInflater;

    public SeriesListAdapter(Context context, Collection<Series> objects) {
       super(context, ITEM_LAYOUT, new ArrayList<Series>(objects));

       this.layoutInflater = LayoutInflater.from(context);

       SERIES_PROVIDER.addFollowingSeriesListener(this);

       for (Series series : objects) {
           series.register(this);
       }

       this.sort(COMPARATOR);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
        }

        ImageView image = (ImageView) itemView.findViewById(R.id.seriesImageView);
        TextView name = (TextView) itemView.findViewById(R.id.nameTextView);
        SeenEpisodesBar seenEpisodesBar = (SeenEpisodesBar) itemView.findViewById(R.id.SeenEpisodesBar);
        TextView nextToSee = (TextView) itemView.findViewById(R.id.nextToSeeTextView);
        CheckBox seenMark = (CheckBox) itemView.findViewById(R.id.seenMarkCheckBox);

        Series item = this.getItem(position);

        image.setImageBitmap(IMAGE_PROVIDER.getPosterOf(item));
        name.setText(item.name());
        seenEpisodesBar.setSeries(item);

        final Episode nextEpisodeToSee = item.nextEpisodeToSee(true);//TODO SharedPreference
        if (nextEpisodeToSee != null) {
            nextToSee.setText(
                Objects.nullSafe(nextEpisodeToSee.name(), this.getContext().getString(R.string.unnamed_episode)));
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

    @Override
    public void onFollowing(Series followedSeries) {
        followedSeries.register(this);
        this.add(followedSeries);
        this.sort(COMPARATOR);
    }

    @Override
    public void onUnfollowing(Series unfollowedSeries) {
        unfollowedSeries.deregister(this);
        this.remove(unfollowedSeries);
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
        //TODO Update the 'progress' bar
//        int index = this.getPosition(series);
//
//        ListView listView = this.getListView();
//        final SeenEpisodesBar seenEpisodesBar = (SeenEpisodesBar) listView.getChildAt(index)
//                .findViewById(R.id.SeenEpisodesBar);
//        seenEpisodesBar.setSeries(series);
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNextEpisodeToSee(Series series) {
        //TODO This behavior will depend on the user's settings (SharedPreference)
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNextNonSpecialEpisodeToSee(Series series) {
        //TODO This behavior will depend on the user's settings (SharedPreference)
    }

    @Override
    public void onMerge(Series series) {
        this.notifyDataSetChanged();
    }

    private static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series seriesA, Series seriesB) {
            return seriesA.name().compareTo(seriesB.name());
        }
    }
}
