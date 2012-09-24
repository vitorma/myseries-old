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
import java.util.Map;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.Schedule;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.Dates;
import mobi.myseries.shared.Objects;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements ScheduleListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final Schedule SCHEDULE = App.schedule();

    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED_CELL = 1;
    private static final int STATE_REGULAR_CELL = 2;

    private Context context;
    private int scheduleMode;
    private ScheduleSpecification specification;
    private ScheduleMode items;
    private int[] cellStates;

    public ScheduleAdapter(Context context, int scheduleMode, ScheduleSpecification specification) {
        this.context = context;
        this.scheduleMode = scheduleMode;
        this.specification = specification;

        this.reload();
    }

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
        View view = convertView;
        EpisodeViewHolder viewHolder = null;

        //TODO (Cleber) Extract method needSeparator(position)

        boolean needSeparator = false;

        switch (this.cellStates[position]) {
            case STATE_SECTIONED_CELL:
                needSeparator = true;
                break;
            case STATE_REGULAR_CELL:
                needSeparator = false;
                break;
            case STATE_UNKNOWN:
            default:
                if (position == 0) {
                    needSeparator = true;
                } else {
                    Episode current = this.items.episodeAt(position);
                    Episode previous = this.items.episodeAt(position - 1);

                    if (Objects.areDifferent(current.airDate(), previous.airDate())) {
                        needSeparator = true;
                    }
                }

                this.cellStates[position] = needSeparator ? STATE_SECTIONED_CELL : STATE_REGULAR_CELL;
                break;
        }

        //TODO (Cleber) Extract method or override newView() ?

        if (view == null) {
            view = View.inflate(this.context, R.layout.myschedule_item, null);
            viewHolder = new EpisodeViewHolder();
            viewHolder.section = view.findViewById(R.id.section);
            viewHolder.dateTextView = (TextView) view.findViewById(R.id.date);
            viewHolder.relativeTimeTextView = (TextView) view.findViewById(R.id.relativeDate);
            viewHolder.seriesNameTextView = (TextView) view.findViewById(R.id.episodeSeriesTextView);
            viewHolder.episodeNumberTextView = (TextView) view.findViewById(R.id.episodeSeasonEpisodeTextView);
            viewHolder.seenMarkCheckBox = (SeenMark) view.findViewById(R.id.episodeIsViewedCheckBox);
            viewHolder.seriesPosterImageView = (ImageView) view.findViewById(R.id.seriesPoster);
            view.setTag(viewHolder);
        } else {
            viewHolder = (EpisodeViewHolder) view.getTag();
        }

        Episode episode = this.items.episodeAt(position);
        Series series = App.getSeries(episode.seriesId());

        if (needSeparator) {
            DateFormat dateformat = new SimpleDateFormat(this.context.getString(R.string.date_format_with_weekday));
            String unavailable = this.context.getString(R.string.unavailable_date);
            String formattedDate = Dates.toString(episode.airDate(), dateformat, unavailable);

            viewHolder.section.setVisibility(View.VISIBLE);
            viewHolder.dateTextView.setText(formattedDate);
            viewHolder.relativeTimeTextView.setText(Dates.relativeTimeStringForNear(episode.airDate()));
        } else {
            viewHolder.section.setVisibility(View.GONE);
        }

        String numberFormat = this.context.getString(R.string.episode_number_format);

        viewHolder.seriesNameTextView.setText(series.name());
        viewHolder.episodeNumberTextView.setText(String.format(numberFormat, episode.seasonNumber(), episode.number()));

        Bitmap seriesPoster = App.imageProvider().getPosterOf(series);
        Bitmap genericPoster = Images.genericSeriesPosterFrom(App.resources());
        viewHolder.seriesPosterImageView.setImageBitmap(Objects.nullSafe(seriesPoster, genericPoster));

        viewHolder.seenMarkCheckBox.setChecked(episode.wasSeen());
        viewHolder.seenMarkCheckBox.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));

        return view;
    }

    private void reload() {
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

    private void setUpData() {
        this.specification = MyScheduleActivity.scheduleSpecification(this.context, this.scheduleMode);

        switch(this.scheduleMode) {
            case ScheduleMode.RECENT:
                this.items = SCHEDULE.recent(this.specification);
                break;
            case ScheduleMode.NEXT:
                this.items = SCHEDULE.next(this.specification);
                break;
            case ScheduleMode.UPCOMING:
                this.items = SCHEDULE.upcoming(this.specification);
                break;
        }

        this.cellStates = new int[this.items.numberOfEpisodes()];
        this.items.register(this);
    }

    public void sortBy(final int sortMode) {
        if (this.specification.sortMode() == sortMode) {return;}

        this.specification.specifySortMode(sortMode);
        this.reload();
    }

    public void hideOrShowSpecialEpisodes(boolean show) {
        if (this.specification.isSatisfiedBySpecialEpisodes() == show) {
            return;
        }

        this.specification.specifyInclusionOfSpecialEpisodes(show);
        this.reload();
    }

    public void hideOrShowSeenEpisodes(boolean show) {
        if (this.specification.isSatisfiedBySeenEpisodes() == show) {
            return;
        }

        this.specification.specifyInclusionOfSeenEpisodes(show);
        this.reload();
    }

    public void hideOrShowSeries(Map<Series, Boolean> filterOptions) {
        boolean needReload = false;

        for (Series s: filterOptions.keySet()) {
            if (this.specification.isSatisfiedByEpisodesOfSeries(s.id()) != filterOptions.get(s)) {
                this.specification.specifyInclusionOf(s, filterOptions.get(s));
                needReload = true;
            }
        }

        if (!needReload) {return;}

        this.reload();
    }

    //Listening---------------------------------------------------------------------------------------------------------

    @Override
    public void onScheduleStateChanged() {
        this.cellStates = new int[this.items.numberOfEpisodes()];
        this.notifyDataSetChanged();
    }

    @Override
    public void onScheduleStructureChanged() {
        this.reload();
    }

    //ViewHolder--------------------------------------------------------------------------------------------------------

    private static class EpisodeViewHolder {
        private View section;
        private TextView dateTextView;
        private TextView relativeTimeTextView;
        private TextView seriesNameTextView;
        private TextView episodeNumberTextView;
        private ImageView seriesPosterImageView;
        private SeenMark seenMarkCheckBox;

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