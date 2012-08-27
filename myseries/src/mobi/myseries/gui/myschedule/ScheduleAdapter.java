package mobi.myseries.gui.myschedule;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.application.schedule.ScheduleDays;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ScheduleAdapter extends BaseAdapter implements EpisodeListener {
    private static final int VIEW_TYPE_DATE = 0;
    private static final int VIEW_TYPE_EPISODE = 1;
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private Context context;
    private int scheduleMode;
    private List<Object> items;
    private SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {
        @Override
        public void onFollowing(Series followedSeries) {
            ScheduleAdapter.this.reload();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            ScheduleAdapter.this.reload();
        }
    };

    public ScheduleAdapter(Context context, int scheduleMode) {
        this.context = context;
        this.scheduleMode = scheduleMode;

        App.registerSeriesFollowingListener(this.seriesFollowingListener);

        this.reload();
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
        return this.items.get(position) instanceof Episode ?
               VIEW_TYPE_EPISODE :
               VIEW_TYPE_DATE;
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
            case VIEW_TYPE_DATE:
                return this.getDateView((Date) this.getItem(position), convertView, parent);
            case VIEW_TYPE_EPISODE:
                return this.getEpisodeView((Episode) this.getItem(position), convertView, parent);
            default:
                return null;
        }
    }

    private View getDateView(Date date, View convertView, ViewGroup parent) {
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
        String unavailable = this.context.getString(R.string.unavailable_date);
        String dateText = Dates.toString(date, format, unavailable);

        viewHolder.dateTextView.setText(dateText);

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
            view.setTag(viewHolder);
        } else {
            viewHolder = (EpisodeViewHolder) view.getTag();
        }

        Series series = App.getSeries(episode.seriesId());
        String format = this.context.getString(R.string.episode_number_format);

        viewHolder.seriesNameTextView.setText(series.name());
        viewHolder.episodeNumberTextView.setText(String.format(format, episode.seasonNumber(), episode.number()));
        viewHolder.seenMarkCheckBox.setChecked(episode.wasSeen());
        viewHolder.seenMarkCheckBox.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));

        return view;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void reload() {
        int sortMode = MyScheduleActivity.sortModeBy(this.context, this.scheduleMode);
        ScheduleDays scheduleDays = App.schedule().days(this.scheduleMode, sortMode);
        this.items = scheduleDays.toList();

        for (Episode e : scheduleDays.getEpisodes()) {
            e.register(this);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsSeen(Episode e) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeen(Episode e) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMerge(Episode e) {
        //It's not my problem
    }

    //------------------------------------------------------------------------------------------------------------------

    private static class DateViewHolder {
        private TextView dateTextView;
    }

    private static class EpisodeViewHolder {
        private TextView seriesNameTextView;
        private TextView episodeNumberTextView;
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
