/*
 *   SeriesListActivity.java
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import br.edu.ufcg.aweseries.App;
import br.edu.ufcg.aweseries.FollowingSeriesListener;
import br.edu.ufcg.aweseries.ImageProvider;
import br.edu.ufcg.aweseries.PosterDownloadListener;
import br.edu.ufcg.aweseries.R;
import br.edu.ufcg.aweseries.SeriesProvider;
import br.edu.ufcg.aweseries.UpdateListener;
import br.edu.ufcg.aweseries.model.Episode;
import br.edu.ufcg.aweseries.model.Series;
import br.edu.ufcg.aweseries.model.SeriesListener;
import br.edu.ufcg.aweseries.util.Objects;

public class SeriesListActivity extends ListActivity implements UpdateListener {
    private static final SeriesProvider seriesProvider = App.environment().seriesProvider();
    private static final SeriesComparator comparator = new SeriesComparator();
    private static final ImageProvider imageProvider = App.environment().imageProvider();

    private SeriesItemViewAdapter dataAdapter;
    private UpdateNotificationLauncher nLauncher;
    private MenuItem updateMenuItem;
    private boolean updateMenuItemStatus = true;

    public SeriesListActivity() {
        seriesProvider.addListener(this);
    }

    //Series comparator-------------------------------------------------------------------------------------------------

    private static class SeriesComparator implements Comparator<Series> {
        @Override
        public int compare(Series seriesA, Series seriesB) {
            return seriesA.name().compareTo(seriesB.name());
        }
    }

    //Series item view adapter------------------------------------------------------------------------------------------

    private class SeriesItemViewAdapter extends ArrayAdapter<Series> implements SeriesListener,
            FollowingSeriesListener, PosterDownloadListener {

        private List<Series> downloadingPosters = new LinkedList<Series>();

        public SeriesItemViewAdapter(Context context, int seriesItemResourceId, List<Series> objects) {
            super(context, seriesItemResourceId, objects);

            seriesProvider.addFollowingSeriesListener(this);
            imageProvider.register(this);

            for (final Series series : objects) {
                series.register(this);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            // if no view was passed, create one for the item
            if (itemView == null) {
                final LayoutInflater li = (LayoutInflater) SeriesListActivity.this
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                itemView = li.inflate(R.layout.series_list_item, null);
            }

            // get views for the series fields
            ImageView image = (ImageView) itemView.findViewById(R.id.seriesImageView);
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            TextView name = (TextView) itemView.findViewById(R.id.nameTextView);
            TextView nextToSee = (TextView) itemView.findViewById(R.id.nextToSeeTextView);
            final CheckBox seenMark = (CheckBox) itemView.findViewById(R.id.seenMarkCheckBox);

            // load series data
            final Series item = this.getItem(position);
            name.setText(item.name());

            if (this.downloadingPosters.contains(item)) {
                image.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                image.setImageBitmap(imageProvider.getPosterOf(item));
            }

            // next episode to see
            final Episode nextEpisodeToSee = item.nextEpisodeToSee(true);//TODO SharedPreference
            if (nextEpisodeToSee != null) {
                nextToSee.setText(Objects.nullSafe(nextEpisodeToSee.name(), this.getContext().getString(R.string.unnamed_episode)));
                seenMark.setEnabled(true);
                seenMark.setChecked(nextEpisodeToSee.wasSeen());
                seenMark.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        seriesProvider.markEpisodeAsSeen(nextEpisodeToSee);
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
            this.sort(comparator);
        }

        //SeriesListener------------------------------------------------------------------------------------------------

        @Override
        public void onUnfollowing(Series unfollowedSeries) {
            unfollowedSeries.deregister(this);
            this.remove(unfollowedSeries);
        }

        @Override
        public void onChangeNumberOfSeenEpisodes(Series series) {
            //TODO Update the 'progress' bar
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

        @Override
        public void onDownloadPosterOf(Series series) {
            this.hideProgressBarAndShowPoster(series);
            this.notifyDataSetChanged();
        }

        @Override
        public void onStartDownloadingPosterOf(Series series) {
            this.hidePosterAndShowProgressBar(series);
            this.notifyDataSetChanged();
        }

        @Override
        public void onConnectionFailureWhileDownloadingPosterOf(Series series) {
            // TODO Show toast            
            this.hideProgressBarAndShowPoster(series);
            this.notifyDataSetChanged();
        }

        @Override
        public void onFailureWhileSavingPosterOf(Series series) {
            // TODO Show toast
            this.hideProgressBarAndShowPoster(series);
            this.notifyDataSetChanged();
        }

        private void hidePosterAndShowProgressBar(Series series) {
            this.downloadingPosters.add(series);
        }

        private void hideProgressBarAndShowPoster(Series series) {
            this.downloadingPosters.remove(series);
        }
    }

    private static class FollowingSeriesToaster implements FollowingSeriesListener {

        @Override
        public void onFollowing(Series followedSeries) {
            final String message = String.format(
                    App.environment().context().getString(R.string.now_you_follow_series),
                    followedSeries.name());

            this.showToastWith(message);
        }

        @Override
        public void onUnfollowing(Series unfollowedSeries) {
            final String message = String.format(
                    App.environment().context().getString(R.string.you_no_longer_follow),
                    unfollowedSeries.name());

            this.showToastWith(message);
        }

        private void showToastWith(String message) {
            final Toast toast = Toast.makeText(App.environment().context(), message,
                    Toast.LENGTH_LONG);
            toast.show();
        }
    }

    //Interface---------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.nLauncher = new UpdateNotificationLauncher();

        seriesProvider.addFollowingSeriesListener(new FollowingSeriesToaster());

        this.setContentView(R.layout.list);
        this.adjustContentView();
        this.setAdapter();
        this.setUpRecentAndUpcomingButtonClickListener();
        this.setupItemClickListener();
        this.setupItemLongClickListener();
        this.dataAdapter.sort(comparator);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.series_list_options_menu, menu);
        this.updateMenuItem = menu.findItem(R.id.updateMenuItem);
        this.updateMenuItem.setEnabled(this.updateMenuItemStatus);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addSeriesMenuItem:
                this.showSearchActivity();
                return true;
            case R.id.updateMenuItem:
                seriesProvider.updateData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSearchRequested() {
        this.showSearchActivity();
        return true;
    }

    //Private-----------------------------------------------------------------------------------------------------------

    private void adjustContentView() {
        final TextView title = (TextView) this.findViewById(R.id.listTitleTextView);
        title.setText(R.string.my_series);

        final TextView empty = (TextView) this.findViewById(android.R.id.empty);
        empty.setText(R.string.no_series_followed);
    }

    private void setAdapter() {
        this.dataAdapter = new SeriesItemViewAdapter(this, R.layout.series_list_item,
                new ArrayList<Series>(seriesProvider.followedSeries()));
        this.setListAdapter(this.dataAdapter);
    }

    private void setupItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent = new Intent(view.getContext(), SeriesDetailsActivity.class);
                final Series series = (Series) parent.getItemAtPosition(position);
                intent.putExtra("series id", series.id());
                intent.putExtra("series name", series.name());
                SeriesListActivity.this.startActivity(intent);
            }
        });
    }

    private void setupItemLongClickListener() {
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                SeriesListActivity.this.showUnfollowingDialog((Series) parent
                        .getItemAtPosition(position));
                return true;
            }
        });
    }

    private void showUnfollowingDialog(final Series series) {
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        seriesProvider.unfollow(series);
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        new AlertDialog.Builder(this)
                .setMessage(
                        String.format(this.getString(R.string.do_you_want_to_stop_following),
                                series.name()))
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener).show();
    }

    private void showSearchActivity() {
        final Intent intent = new Intent(this, SeriesSearchActivity.class);
        this.startActivity(intent);
    }

    private void setUpRecentAndUpcomingButtonClickListener() {
        final ImageButton recentAndUpcomingEpisodesButton = (ImageButton) this
                .findViewById(R.id.recentAndUpcomingEpisodesButton);

        recentAndUpcomingEpisodesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesListActivity.this.showRecentAndUpcomingEpisodesActivity();
            }
        });
    }

    private void showRecentAndUpcomingEpisodesActivity() {
        final Intent intent = new Intent(this, RecentAndUpcomingEpisodesActivity.class);
        this.startActivity(intent);
    }

    private class UpdateNotificationLauncher {
        private final int id = 0;
        private final int updateNotificationText = R.string.updating_series_data;
        private final int updateNotificationTitle = R.string.updating_series_notification_title;
        private final int updateFailureText = R.string.update_failure_notification_message;
        private final int updateFailureTitle = R.string.updating_series_failure_notification_title;
        private final int icon = R.drawable.stat_sys_download;
        private final NotificationManager nm = (NotificationManager) SeriesListActivity.this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        private void launchNotification(String title, String text) {
            final long when = System.currentTimeMillis();

            final Notification notification = new Notification(this.icon,
                    SeriesListActivity.this.getString(this.updateNotificationText), when);
            final Context context = SeriesListActivity.this.getApplicationContext();

            final Intent notificationIntent = new Intent(SeriesListActivity.this,
                    SeriesListActivity.class);
            final PendingIntent contentIntent = PendingIntent.getActivity(SeriesListActivity.this,
                    0, notificationIntent, 0);

            notification.setLatestEventInfo(context, title, text, contentIntent);

            this.nm.notify(this.id, notification);
        }

        public void launchUpdatingNotification() {
            this.launchNotification(
                    SeriesListActivity.this.getString(this.updateNotificationTitle),
                    SeriesListActivity.this.getString(this.updateNotificationText));
        }

        public void launchUpdatingFailureNotification() {
            this.launchNotification(SeriesListActivity.this.getString(this.updateFailureTitle),
                    SeriesListActivity.this.getString(this.updateFailureText));
        }

        public void clearNotification() {
            this.nm.cancelAll();
        }
    }

    @Override
    public void onUpdateStart() {
        this.nLauncher.launchUpdatingNotification();
        this.disableUpdateMenuItem();
    }

    @Override
    public void onUpdateFailure() {
        this.nLauncher.clearNotification();
        this.nLauncher.launchUpdatingFailureNotification();
        this.enableUpdateMenuItem();
    }

    @Override
    public void onUpdateSuccess() {
        this.nLauncher.clearNotification();
        this.enableUpdateMenuItem();
    }

    private void disableUpdateMenuItem() {
        if (this.updateMenuItem != null) {
            this.updateMenuItem.setEnabled(false);
        }
        this.updateMenuItemStatus = false;
    }

    private void enableUpdateMenuItem() {
        if (this.updateMenuItem != null) {
            this.updateMenuItem.setEnabled(true);
        }
        this.updateMenuItemStatus = true;
    }
}
