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
import java.util.Collection;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.Day;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.schedule.ScheduleElements;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import android.content.Context;
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
    private ScheduleElements items;

    public ScheduleAdapter(Context context, int scheduleMode) {
        this.context = context;
        this.scheduleMode = scheduleMode;

        this.setUpData();
    }

    @Override
    public int getCount() {
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
        return this.items.get(position).getClass() == Episode.class ?
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
        String formattedDate = Dates.toString(day.getDate(), format);

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

    //------------------------------------------------------------------------------------------------------------------

    private void setUpData() {
        int sortMode = MyScheduleActivity.sortModeBy(this.context, this.scheduleMode);

        switch(this.scheduleMode) {
            case ScheduleMode.RECENT:
                this.items = SCHEDULE.recent().sortBy(sortMode);
                SCHEDULE.registerAsRecentListener(this);
                break;
            case ScheduleMode.NEXT:
                this.items = SCHEDULE.next().sortBy(sortMode);
                SCHEDULE.registerAsNextListener(this);
                break;
            case ScheduleMode.UPCOMING:
                this.items = SCHEDULE.upcoming().sortBy(sortMode);
                SCHEDULE.registerAsUpcomingListener(this);
                break;
        };

        this.notifyDataSetChanged();
    }

    public void sortBy(int sortMode) {
        this.items.sortBy(sortMode);
        this.notifyDataSetChanged();
    }

    //------------------------------------------------------------------------------------------------------------------

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

    @Override
    public void onAdd(Episode e) {
        this.items.add(e);
        this.notifyDataSetChanged();
    }

    @Override
    public void onAdd(Collection<Episode> c) {
        this.items.addAll(c);
        this.notifyDataSetChanged();
    }

    @Override
    public void onRemove(Episode e) {
        this.items.remove(e);
        this.notifyDataSetChanged();
    }

    @Override
    public void onRemove(Collection<Episode> c) {
        this.items.removeAll(c);
        this.notifyDataSetChanged();
    }
}