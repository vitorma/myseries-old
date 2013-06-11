/*
 *   ScheduleAdapter.java
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

package mobi.myseries.gui.myschedule;

import java.text.DateFormat;
import java.util.Date;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferences;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Strings;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements ScheduleListener, Publisher<ScheduleAdapter.Listener> {
    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED = 1;
    private static final int STATE_REGULAR = 2;

    private int scheduleMode;
    private MySchedulePreferences preferences;
    private ScheduleMode items;
    private int[] viewStates;

    public ScheduleAdapter(int scheduleMode, MySchedulePreferences preferences) {
        this.scheduleMode = scheduleMode;
        this.preferences = preferences;

        this.reload();
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        if (this.items == null) {return 0;}

        return this.items.numberOfEpisodes();
    }

    @Override
    public Object getItem(int position) {
        return this.items.episodeAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = Objects.nullSafe(convertView, View.inflate(App.context(), R.layout.myschedule_item, null));
        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Episode episode = this.items.episodeAt(position);
        Series series = App.seriesProvider().getSeries(episode.seriesId());

        this.setUpViewSection(position, viewHolder, episode);
        this.setUpViewBody(viewHolder, series, episode);

        return view;
    }

    private void setUpViewSection(int position, ViewHolder viewHolder, Episode episode) {
        this.updateViewStates(position);

        if (this.isViewSectioned(position)) {
            DateFormat dateformat = android.text.format.DateFormat.getDateFormat(App.context());
            String unavailable = LocalText.get(R.string.unavailable_date);
            String formattedDate = DatesAndTimes.toString(episode.airDate(), dateformat, unavailable);
            viewHolder.date.setText(formattedDate);

            RelativeDay relativeDay = DatesAndTimes.parse(episode.airDate(), null);
            viewHolder.relativeDay.setText(LocalText.of(relativeDay, ""));

            viewHolder.section.setVisibility(View.VISIBLE);
        } else {
            viewHolder.section.setVisibility(View.GONE);
        }
    }

    private void setUpViewBody(ViewHolder viewHolder, Series series, Episode episode) {
        Bitmap seriesPoster = App.imageService().getPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        viewHolder.poster.setImageBitmap(Objects.nullSafe(seriesPoster, genericPoster));

        viewHolder.seriesName.setText(series.name());

        String numberFormat = App.context().getString(R.string.episode_number_format);
        String episodeNumber = String.format(numberFormat, episode.seasonNumber(), episode.number());
        viewHolder.episodeName.setText(episodeNumber + " " + episode.name());

        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(App.context());
        String airtime = DatesAndTimes.toString(episode.airtime(), airtimeFormat, "");
        String network = series.network();
        viewHolder.airInfo.setText(Strings.concat(airtime, network, " - "));

        viewHolder.seenMark.setChecked(episode.wasSeen());
        viewHolder.seenMark.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));
    }

    private void updateViewStates(int position) {
        if (this.viewStates[position] == STATE_UNKNOWN) {
            this.viewStates[position] = this.calculateViewState(position);
        }
    }

    private int calculateViewState(int position) {
        return this.shouldViewBeSectioned(position) ? STATE_SECTIONED : STATE_REGULAR;
    }

    private boolean shouldViewBeSectioned(int position) {
        if (position == 0) {
            return true;
        }

        Date current = this.items.episodeAt(position).airDate();
        Date previous = this.items.episodeAt(position - 1).airDate();

        return Objects.areDifferent(current, previous);
    }

    private boolean isViewSectioned(int position) {
        return this.viewStates[position] == STATE_SECTIONED;
    }

    public void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                ScheduleAdapter.this.isLoading = true;
                ScheduleAdapter.this.notifyStartLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ScheduleAdapter.this.setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ScheduleAdapter.this.isLoading = false;
                ScheduleAdapter.this.notifyFinishLoading();
                ScheduleAdapter.this.notifyDataSetChanged();
            }
        }.execute();
    }

    private void setUpData() {
        switch(this.scheduleMode) {
            case ScheduleMode.RECENT:
                this.items = App.schedule().modeRecent(this.preferences.fullSpecification());
                break;
            case ScheduleMode.NEXT:
                this.items = App.schedule().modeNext(this.preferences.fullSpecification());
                break;
            case ScheduleMode.UPCOMING:
                this.items = App.schedule().modeUpcoming(this.preferences.fullSpecification());
                break;
        }

        this.viewStates = new int[this.items.numberOfEpisodes()];
        this.items.register(this);
    }

    /* ViewHolder */

    private static class ViewHolder {
        private View section;
        private TextView date;
        private TextView relativeDay;
        private ImageView poster;
        private SeenMark seenMark;
        private TextView seriesName;
        private TextView episodeName;
        private TextView airInfo;

        private ViewHolder(View view) {
            this.section = view.findViewById(R.id.section);
            this.date = (TextView) view.findViewById(R.id.date);
            this.relativeDay = (TextView) view.findViewById(R.id.relativeDay);
            this.poster = (ImageView) view.findViewById(R.id.poster);
            this.seenMark = (SeenMark) view.findViewById(R.id.seenMark);
            this.seriesName = (TextView) view.findViewById(R.id.seriesName);
            this.episodeName = (TextView) view.findViewById(R.id.episodeName);
            this.airInfo = (TextView) view.findViewById(R.id.airInfo);

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.seenMark.isChecked()) {
                        App.seriesProvider().markEpisodeAsSeen(episode);
                    } else {
                        App.seriesProvider().markEpisodeAsNotSeen(episode);
                    }
                }
            };
        }
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        this.viewStates = new int[this.items.numberOfEpisodes()];
        this.notifyDataSetChanged();
    }

    @Override
    public void onScheduleStructureChanged() {
        this.reload();
    }

    /* ScheduleAdapter.Listener */

    public static interface Listener {
        public void onStartLoading();
        public void onFinishLoading();
    }

    private boolean isLoading;
    private ListenerSet<ScheduleAdapter.Listener> listeners = new ListenerSet<ScheduleAdapter.Listener>();

    public boolean isLoading() {
        return this.isLoading;
    }

    @Override
    public boolean register(ScheduleAdapter.Listener listener) {
        return this.listeners.register(listener);
    }

    @Override
    public boolean deregister(ScheduleAdapter.Listener listener) {
        return this.listeners.deregister(listener);
    }

    private void notifyStartLoading() {
        for (ScheduleAdapter.Listener listener : this.listeners) {
            listener.onStartLoading();
        }
    }

    private void notifyFinishLoading() {
        for (ScheduleAdapter.Listener listener : this.listeners) {
            listener.onFinishLoading();
        }
    }
}