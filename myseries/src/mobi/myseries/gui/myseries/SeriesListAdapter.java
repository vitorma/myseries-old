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

package mobi.myseries.gui.myseries;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.follow.FollowSeriesService;
import mobi.myseries.application.follow.SeriesFollowingListener;
import mobi.myseries.application.image.ImageService;
import mobi.myseries.application.update.UpdateListener;
import mobi.myseries.application.update.UpdateService;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.model.SeriesListener;
import mobi.myseries.gui.series.SeriesActivity;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenEpisodesBar;
import mobi.myseries.gui.shared.SeriesComparator;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Status;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

// TODO (Cleber) Refactoring:
//               Use ViewHolder pattern
//               Use notifyDatasetChanged instead of remove/add items
//               Clean code
public class SeriesListAdapter extends ArrayAdapter<Series> implements SeriesListener, SeriesFollowingListener {
    private static final ImageService IMAGE_SERVICE = App.imageService();
    private static final FollowSeriesService FOLLOW_SERIES_SERVICE = App.followSeriesService();

    /* TODO(Reul): Use MessageService instead of UpdateService*/
    private static final UpdateService UPDATE_SERIES_SERVICE = App.updateSeriesService();

    private static final SeriesComparator COMPARATOR = new SeriesComparator();
    private static final int ITEM_LAYOUT = R.layout.myseries_item_new;

    private static class SeriesListItemFactory {
        private Context context;
        private LayoutInflater layoutInflater;

        private SeriesListItemFactory(Context context) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(this.context);
        }

        public View draw(Series item, View oldView) {
            View itemView = this.prepareViewFrom(oldView);

            this.setPosterTo(item, itemView);
            this.setNameTo(item.name(), itemView);
            this.setStatusTo(item.status(), itemView);
            this.setAirInfoTo(item, itemView);
            this.setSeenEpisodesFor(item, itemView);
            this.setSeenEpisodesBarFor(item, itemView);
            this.setUpShowingSeriesDetailsViewOnClickFor(item, itemView);

            return itemView;
        }

        private View prepareViewFrom(View oldView) {
            View itemView = oldView;

            if (oldView == null) {
                itemView = this.layoutInflater.inflate(ITEM_LAYOUT, null);
            }

            return itemView;
        }

        private void setPosterTo(Series series, View itemView) {
            final ImageView image = (ImageView) itemView.findViewById(R.id.seriesImageView);

            Bitmap seriesPoster = IMAGE_SERVICE.getSmallPosterOf(series);
            Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());

            image.setImageBitmap(Objects.nullSafe(seriesPoster, genericPoster));
        }

        private void setNameTo(String name, View itemView) {
            TextView nameView = (TextView) itemView.findViewById(R.id.nameTextView);
            nameView.setText(name);
        }

        private void setStatusTo(Status status, View itemView) {
            TextView statusTextView = (TextView) itemView.findViewById(R.id.statusTextView);
            statusTextView.setText(LocalText.of(status, ""));
        }

        private void setAirInfoTo(Series series, View itemView) {
            TextView airInfoTextView = (TextView) itemView.findViewById(R.id.airInfoTextView);
            String airDay = series.airDay().toString(Locale.getDefault());
            DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(this.context);
            String airtime = DatesAndTimes.toString(series.airtime(), airtimeFormat, "");
            String network = series.network();
            String airInfo = airDay +
                    (Strings.isBlank(airtime) ? airtime : " " + airtime) +
                    (Strings.isBlank(network) ? network : " " + network);
            airInfoTextView.setText(airInfo);
        }

        private void setSeenEpisodesFor(Series series, View itemView) {
            TextView amountTextView = (TextView) itemView.findViewById(R.id.amountTextView);
            String am = series.numberOfSeenEpisodes() + "/" + series.numberOfEpisodes();
            amountTextView.setText(am);
        }

        private void setSeenEpisodesBarFor(Series series, View itemView) {
            SeenEpisodesBar seenEpisodesBar = (SeenEpisodesBar) itemView.findViewById(R.id.seenEpisodesBar);
            seenEpisodesBar.updateWithEpisodesOf(series);
        }

        private void setUpShowingSeriesDetailsViewOnClickFor(final Series series, View itemView) {
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = SeriesActivity.newIntent(SeriesListItemFactory.this.context, series.id());
                    SeriesListItemFactory.this.context.startActivity(intent);
                }
            });
        }
    }

    private SeriesListItemFactory listItemFactory;

    private UpdateListener updateListener = new UpdateListener() {

        @Override
        public void onUpdateSuccess() {
            SeriesListAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onUpdateFailure(Exception e) {
            // TODO(Gabriel) Should we really do something here?
            // May the series have been partially updated after a failure?
            SeriesListAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onUpdateStart() {}

        @Override
        public void onUpdateNotNecessary() {}
    };

    public SeriesListAdapter(Context context, Collection<Series> objects) {
        super(context, ITEM_LAYOUT, new ArrayList<Series>(objects));

        this.listItemFactory = new SeriesListItemFactory(context);

        FOLLOW_SERIES_SERVICE.registerSeriesFollowingListener(this);
        UPDATE_SERIES_SERVICE.register(this.updateListener);

        for (Series series : objects) {
            series.register(this);
        }

        this.sort(COMPARATOR);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Series item = this.getItem(position);
        return this.listItemFactory.draw(item, convertView);
    }

    @Override
    public void onFollowing(Series followedSeries) {
        followedSeries.register(this);
        this.add(followedSeries);
        this.sort(COMPARATOR);
    }

    @Override
    public void onStopFollowing(Series unfollowedSeries) {
        unfollowedSeries.deregister(this);
        this.remove(unfollowedSeries);
    }

    @Override
    public void onStopFollowingAll(Collection<Series> allUnfollowedSeries) {
        for (Series s : allUnfollowedSeries) {
            this.onStopFollowing(s);
        }
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Series series) {
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
    public void onFollowingStart(Series seriesToFollow) {}

    @Override
    public void onFollowingFailure(Series series, Exception e) {}
}
