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
import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferences;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.PosterFetchingMethod;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Publisher;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.WeekDay;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements ScheduleListener, Publisher<ScheduleAdapter.Listener> {
    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED = 1;
    private static final int STATE_REGULAR = 2;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterThumbnailFrom(App.resources());

    private final int scheduleMode;
    private final MySchedulePreferences preferences;
    private ScheduleMode items;
    private int[] viewStates;

    public ScheduleAdapter(int scheduleMode, MySchedulePreferences preferences) {
        this.scheduleMode = scheduleMode;
        this.preferences = preferences;

        reload();
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        if (items == null) {return 0;}

        return items.numberOfEpisodes();
    }

    @Override
    public Object getItem(int position) {
        return items.episodeAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(App.context(), R.layout.myschedule_item, null);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Episode episode = items.episodeAt(position);
        Series series = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

        setUpViewSection(position, viewHolder, episode);
        setUpViewBody(viewHolder, series, episode);

        return view;
    }

    private void setUpViewSection(int position, ViewHolder viewHolder, Episode episode) {
        updateViewStates(position);

        if (isViewSectioned(position)) {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.context());
            String unavailable = LocalText.get(R.string.unavailable_date);
            String formattedDate = DatesAndTimes.toString(episode.airDate(), dateFormat, unavailable);
            viewHolder.date.setText(formattedDate);

            WeekDay weekDay = App.seriesFollowingService().getFollowedSeries(episode.seriesId()).airDay();
            String formattedWeekDay = DatesAndTimes.toString(weekDay, DateFormats.forShortWeekDay(Locale.getDefault()), "")
                    .toUpperCase();
            viewHolder.weekDay.setText(formattedWeekDay);

            RelativeDay relativeDay = DatesAndTimes.parse(episode.airDate(), null);
            viewHolder.relativeDay.setText(LocalText.of(relativeDay, ""));

            viewHolder.section.setVisibility(View.VISIBLE);
        } else {
            viewHolder.section.setVisibility(View.GONE);
        }
    }

    private void setUpViewBody(ViewHolder viewHolder, Series series, Episode episode) {
        AsyncImageLoader.loadBitmapOn(
                new PosterFetchingMethod(series, App.imageService()),
                GENERIC_POSTER,
                viewHolder.poster, viewHolder.progressBar);

        viewHolder.seriesName.setText(series.name());

        String numberFormat = App.context().getString(R.string.episode_number_format);
        String episodeNumber = String.format(numberFormat, episode.seasonNumber(), episode.number());
        viewHolder.episodeName.setText(episodeNumber + " " + episode.title());

        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(App.context());
        String airtime = DatesAndTimes.toString(episode.airTime(), airtimeFormat, "");
        String network = series.network();
        viewHolder.airInfo.setText(Strings.concat(airtime, network, " - "));

        viewHolder.seenMark.setChecked(episode.watched());
        viewHolder.seenMark.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));
    }

    private void updateViewStates(int position) {
        if (viewStates[position] == STATE_UNKNOWN) {
            viewStates[position] = calculateViewState(position);
        }
    }

    private int calculateViewState(int position) {
        return shouldViewBeSectioned(position) ? STATE_SECTIONED : STATE_REGULAR;
    }

    private boolean shouldViewBeSectioned(int position) {
        if (position == 0) {
            return true;
        }

        Date current = items.episodeAt(position).airDate();
        Date previous = items.episodeAt(position - 1).airDate();

        return Objects.areDifferent(current, previous);
    }

    private boolean isViewSectioned(int position) {
        return viewStates[position] == STATE_SECTIONED;
    }

    public void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                isLoading = true;
                ScheduleAdapter.this.notifyStartLoading();
            }

            @Override
            protected Void doInBackground(Void... params) {
                ScheduleAdapter.this.setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                isLoading = false;
                ScheduleAdapter.this.notifyFinishLoading();
                ScheduleAdapter.this.notifyDataSetChanged();
            }
        }.execute();
    }

    private void setUpData() {
        switch(scheduleMode) {
        case ScheduleMode.AIRED:
            items = App.schedule().aired(preferences.fullSpecification());
            break;
        case ScheduleMode.TO_WATCH:
            items = App.schedule().toWatch(preferences.fullSpecification());
            break;
        case ScheduleMode.UNAIRED:
            items = App.schedule().unaired(preferences.fullSpecification());
            break;
        }

        viewStates = new int[items.numberOfEpisodes()];
        items.register(this);
    }

    /* ViewHolder */

    private static class ViewHolder {
        private final View section;
        private final TextView date;
        private final TextView weekDay;
        private final TextView relativeDay;
        private final ImageView poster;
        private final SeenMark seenMark;
        private final TextView seriesName;
        private final TextView episodeName;
        private final TextView airInfo;
        private final ProgressBar progressBar;

        private ViewHolder(View view) {
            section = view.findViewById(R.id.section);
            date = (TextView) view.findViewById(R.id.date);
            weekDay = (TextView) view.findViewById(R.id.weekDay);
            relativeDay = (TextView) view.findViewById(R.id.relativeDay);
            poster = (ImageView) view.findViewById(R.id.poster);
            seenMark = (SeenMark) view.findViewById(R.id.seenMark);
            seriesName = (TextView) view.findViewById(R.id.seriesName);
            episodeName = (TextView) view.findViewById(R.id.episodeName);
            airInfo = (TextView) view.findViewById(R.id.airInfo);
            progressBar = (ProgressBar) view.findViewById(R.id.loadProgress);

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (seenMark.isChecked()) {
                        App.markingService().markAsWatched(episode);
                    } else {
                        App.markingService().markAsUnwatched(episode);
                    }
                }
            };
        }
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        viewStates = new int[items.numberOfEpisodes()];
        notifyDataSetChanged();
    }

    @Override
    public void onScheduleStructureChanged() {
        reload();
    }

    /* ScheduleAdapter.Listener */

    public static interface Listener {
        public void onStartLoading();
        public void onFinishLoading();
    }

    private boolean isLoading;
    private final ListenerSet<ScheduleAdapter.Listener> listeners = new ListenerSet<ScheduleAdapter.Listener>();

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean register(ScheduleAdapter.Listener listener) {
        return listeners.register(listener);
    }

    @Override
    public boolean deregister(ScheduleAdapter.Listener listener) {
        return listeners.deregister(listener);
    }

    private void notifyStartLoading() {
        for (ScheduleAdapter.Listener listener : listeners) {
            listener.onStartLoading();
        }
    }

    private void notifyFinishLoading() {
        for (ScheduleAdapter.Listener listener : listeners) {
            listener.onFinishLoading();
        }
    }
}