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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.preferences.SchedulePreferences;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.ListenerSet;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.Publisher;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements ScheduleListener, Publisher<ScheduleAdapter.Listener> {
    private static final Context CONTEXT = App.context();
    private static final Schedule SCHEDULE = App.schedule();
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED_CELL = 1;
    private static final int STATE_REGULAR_CELL = 2;

    private int scheduleMode;
    private SchedulePreferences preferences;
    private ScheduleMode items;
    private int[] cellStates;

    public ScheduleAdapter(int scheduleMode, SchedulePreferences preferences) {
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
    public int getViewTypeCount() {
        return 1;
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
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = Objects.nullSafe(convertView, View.inflate(CONTEXT, R.layout.myschedule_item, null));
        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Episode episode = this.items.episodeAt(position);
        Series series = SERIES_PROVIDER.getSeries(episode.seriesId());

        this.setUpCellSection(position, viewHolder, episode);
        this.setUpCellBody(viewHolder, series, episode);

        return view;
    }

    private void setUpCellSection(int position, ViewHolder viewHolder, Episode episode) {
        this.updateCellStates(position);

        if (this.isCellSectioned(position)) {
            DateFormat dateformat = new SimpleDateFormat(CONTEXT.getString(R.string.date_format_with_weekday));
            String unavailable = CONTEXT.getString(R.string.unavailable_date);
            String formattedDate = Dates.toString(episode.airDate(), dateformat, unavailable);

            viewHolder.dateTextView.setText(formattedDate);
            viewHolder.relativeTimeTextView.setText(Dates.relativeTimeStringForNear(episode.airDate()));
            viewHolder.section.setVisibility(View.VISIBLE);
        } else {
            viewHolder.section.setVisibility(View.GONE);
        }
    }

    public void setUpCellBody(ViewHolder viewHolder, Series series, Episode episode) {
        Bitmap seriesPoster = App.imageProvider().getPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        viewHolder.seriesPosterImageView.setImageBitmap(Objects.nullSafe(seriesPoster, genericPoster));

        viewHolder.seriesNameTextView.setText(series.name());

        String numberFormat = CONTEXT.getString(R.string.episode_number_format);
        viewHolder.episodeNumberTextView.setText(String.format(numberFormat, episode.seasonNumber(), episode.number()));

        viewHolder.seenMarkCheckBox.setChecked(episode.wasSeen());
        viewHolder.seenMarkCheckBox.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));
    }

    private void updateCellStates(int position) {
        if (this.cellStates[position] == STATE_UNKNOWN) {
            this.cellStates[position] = this.calculateCellState(position);
        }
    }

    private int calculateCellState(int position) {
        return this.shouldBeSectioned(position) ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
    }

    private boolean shouldBeSectioned(int position) {
        if (position == 0) {
            return true;
        }

        Date current = this.items.episodeAt(position).airDate();
        Date previous = this.items.episodeAt(position - 1).airDate();

        return Objects.areDifferent(current, previous);
    }

    private boolean isCellSectioned(int position) {
        return this.cellStates[position] == STATE_SECTIONED_CELL;
    }

    /* Preferences change */

    public void sortBy(int sortMode) {
        if (this.preferences.sortMode() != sortMode) {
            this.preferences.setSortMode(sortMode);
            this.reload();
        }
    }

    public void hideOrShowSpecialEpisodes(boolean showSpecialEpisodes) {
        if (this.preferences.showSpecialEpisodes() != showSpecialEpisodes) {
            this.preferences.setIfShowSpecialEpisodes(showSpecialEpisodes);
            this.reload();
        }
    }

    public void hideOrShowSeenEpisodes(boolean showSeenEpisodes) {
        if (this.preferences.showSeenEpisodes() != showSeenEpisodes) {
            this.preferences.setIfShowSeenEpisodes(showSeenEpisodes);
            this.reload();
        }
    }

    public void hideOrShowSeries(Map<Series, Boolean> seriesFilterOptions) {
        boolean needReload = false;

        for (Series s: seriesFilterOptions.keySet()) {
            if (this.preferences.showSeries(s.id()) != seriesFilterOptions.get(s)) {
                this.preferences.setIfShowSeries(s.id(), seriesFilterOptions.get(s));
                needReload = true;
            }
        }

        if (needReload) {
            this.reload();
        }
    }

    private void reload() {
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
                this.items = SCHEDULE.modeRecent(this.preferences.fullSpecification());
                break;
            case ScheduleMode.NEXT:
                this.items = SCHEDULE.modeNext(this.preferences.fullSpecification());
                break;
            case ScheduleMode.UPCOMING:
                this.items = SCHEDULE.modeUpcoming(this.preferences.fullSpecification());
                break;
        }

        this.cellStates = new int[this.items.numberOfEpisodes()];
        this.items.register(this);
    }

    /* ViewHolder */

    private static class ViewHolder {
        private View section;
        private ImageView seriesPosterImageView;
        private TextView dateTextView;
        private TextView relativeTimeTextView;
        private TextView seriesNameTextView;
        private TextView episodeNumberTextView;
        private SeenMark seenMarkCheckBox;

        private ViewHolder(View view) {
            this.section = view.findViewById(R.id.section);
            this.dateTextView = (TextView) view.findViewById(R.id.date);
            this.relativeTimeTextView = (TextView) view.findViewById(R.id.relativeDate);
            this.seriesNameTextView = (TextView) view.findViewById(R.id.episodeSeriesTextView);
            this.episodeNumberTextView = (TextView) view.findViewById(R.id.episodeSeasonEpisodeTextView);
            this.seenMarkCheckBox = (SeenMark) view.findViewById(R.id.episodeIsViewedCheckBox);
            this.seriesPosterImageView = (ImageView) view.findViewById(R.id.seriesPoster);

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.seenMarkCheckBox.isChecked()) {
                        SERIES_PROVIDER.markEpisodeAsSeen(episode);
                    } else {
                        SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                    }
                }
            };
        }
    }

    /* ScheduleAdapter.Holder */

    public static interface Holder {
        public ScheduleAdapter adapterForMode(int scheduleMode);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        this.cellStates = new int[this.items.numberOfEpisodes()];
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