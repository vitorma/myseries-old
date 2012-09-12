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
import java.util.ArrayList;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.Day;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.schedule.ScheduleList;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.HasDate;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements ScheduleListener {
    private static final int VIEW_TYPE_DAY = 0;
    private static final int VIEW_TYPE_EPISODE = 1;
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final Schedule SCHEDULE = App.schedule();

    private Context context;
    private int scheduleMode;
    private ScheduleList items;

    public ScheduleAdapter(Context context, int scheduleMode) {
        this.context = context;
        this.scheduleMode = scheduleMode;

        this.reload();
    }

    public void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ScheduleAdapter.this.setUpData();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                ScheduleAdapter.this.notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public int getCount() {
        if (this.items == null) {
            return 0;
        }

        return this.items.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        HasDate element = this.items.get(position);

        return this.items.isEpisode(element) ?
               VIEW_TYPE_EPISODE :
               VIEW_TYPE_DAY;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return this.getItemViewType(position) == VIEW_TYPE_EPISODE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (this.getItemViewType(position)) {
            case VIEW_TYPE_DAY:
                return this.getDateView((Day) this.getItem(position), convertView, parent);
            case VIEW_TYPE_EPISODE:
                return this.getEpisodeView((Episode) this.getItem(position), convertView, parent);
            default:
                return null;
        }
    }

    private View getDateView(Day day, View convertView, ViewGroup parent) {
        View view = convertView;
        DateViewHolder viewHolder = null;

        if(view == null) {
            view = View.inflate(this.context, R.layout.myschedule_section, null);
            viewHolder = new DateViewHolder();
            viewHolder.dateTextView = (TextView) view.findViewById(R.id.title);
            view.setTag(viewHolder);
        } else {
            viewHolder = (DateViewHolder) view.getTag();
        }

        DateFormat format = App.dateFormat();
        String formattedDate = Dates.toString(day.getDate(), format, this.context.getString(R.string.unavailable_date));

        viewHolder.dateTextView.setText(formattedDate);

        return view;
    }

    private View getEpisodeView(Episode episode, View convertView, ViewGroup parent) {
        View view = convertView;
        EpisodeViewHolder viewHolder = null;

        if (view == null) {
            view = View.inflate(this.context, R.layout.myschedule_item, null);
            viewHolder = new EpisodeViewHolder();
            viewHolder.seriesNameTextView = (TextView) view.findViewById(R.id.episodeSeriesTextView);
            viewHolder.episodeNumberTextView = (TextView) view.findViewById(R.id.episodeSeasonEpisodeTextView);
            viewHolder.seenMarkCheckBox = (CheckBox) view.findViewById(R.id.episodeIsViewedCheckBox);
            viewHolder.seriesPosterImageView = (ImageView) view.findViewById(R.id.seriesPoster);
            view.setTag(viewHolder);
        } else {
            viewHolder = (EpisodeViewHolder) view.getTag();
        }

        Series series = App.getSeries(episode.seriesId());
        String format = this.context.getString(R.string.episode_number_format);

        viewHolder.seriesNameTextView.setText(series.name());
        viewHolder.episodeNumberTextView.setText(String.format(format, episode.seasonNumber(), episode.number()));
        viewHolder.seriesPosterImageView.setImageBitmap(App.seriesPoster(series.id()));
        viewHolder.seenMarkCheckBox.setChecked(episode.wasSeen());
        viewHolder.seenMarkCheckBox.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));

        return view;
    }

    private void setUpData() {
        int sortMode = MyScheduleActivity.sortMode(this.context, this.scheduleMode);
        boolean includingSpecialEpisodes = MyScheduleActivity.inclusionOfSpecialEpisodes(this.context, this.scheduleMode);
        boolean includingSeenEpisodes = MyScheduleActivity.inclusionOfSeenEpisodes(this.context, this.scheduleMode);
        List<Series> allSeriesToShow = new ArrayList<Series>();

        for (Series s : SERIES_PROVIDER.followedSeries()) {
            boolean includingEpisodesOfSeries = MyScheduleActivity.inclusionOfEpisodesOfSeries(this.context, this.scheduleMode, s.id());

            if (includingEpisodesOfSeries) {
                allSeriesToShow.add(s);
            }
        }

        switch(this.scheduleMode) {
            case ScheduleMode.RECENT:
                this.items = SCHEDULE.recentBuilder()
                    .includingSpecialEpisodes(includingSpecialEpisodes)
                    .includingSeenEpisodes(includingSeenEpisodes)
                    .includingEpisodesOfAllSeries(allSeriesToShow)
                    .sortingBy(sortMode)
                    .build();
                break;
            case ScheduleMode.NEXT:
                this.items = SCHEDULE.nextBuilder()
                    .includingSpecialEpisodes(includingSpecialEpisodes)
                    .includingEpisodesOfAllSeries(allSeriesToShow)
                    .sortingBy(sortMode)
                    .build();
                break;
            case ScheduleMode.UPCOMING:
                this.items = SCHEDULE.upcomingBuilder()
                    .includingSpecialEpisodes(includingSpecialEpisodes)
                    .includingSeenEpisodes(includingSeenEpisodes)
                    .includingEpisodesOfAllSeries(allSeriesToShow)
                    .sortingBy(sortMode)
                    .build();
                break;
        }

        this.items.register(this);
    }

    //Listening---------------------------------------------------------------------------------------------------------

    @Override
    public void onStateChanged() {
        this.notifyDataSetChanged();
    }

    //ViewHolder--------------------------------------------------------------------------------------------------------

    private static class DateViewHolder {
        private TextView dateTextView;
    }

    private static class EpisodeViewHolder {
        private TextView seriesNameTextView;
        private TextView episodeNumberTextView;
        private ImageView seriesPosterImageView;
        private CheckBox seenMarkCheckBox;

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    if (EpisodeViewHolder.this.seenMarkCheckBox.isChecked()) {
                        SERIES_PROVIDER.markEpisodeAsSeen(episode);
                    } else {
                        SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                    }
                }
            };
        }
    }
}