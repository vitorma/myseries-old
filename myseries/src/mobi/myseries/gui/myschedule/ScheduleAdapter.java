package mobi.myseries.gui.myschedule;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesFollowingListener;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.shared.Dates;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ScheduleAdapter extends ArrayAdapter<Episode> implements EpisodeListener {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();
    private static final int EPISODE_ITEM_RESOURCE_ID = R.layout.myschedule_item;

    private int scheduleMode;
    private LayoutInflater layoutInflater;
    private SeriesFollowingListener seriesFollowingListener = new SeriesFollowingListener() {
        @Override
        public void onFollowing(Series followedSeries) {
            ScheduleAdapter.this.setUpData();
        }

        @Override
        public void onStopFollowing(Series unfollowedSeries) {
            for (Episode e : unfollowedSeries.episodes()) {
                ScheduleAdapter.this.remove(e);
            }
        }
    };

    public ScheduleAdapter(Context context, int scheduleMode) {
        super(context, R.layout.myschedule_item);

        this.scheduleMode = scheduleMode;
        this.layoutInflater = LayoutInflater.from(context);

        App.registerSeriesFollowingListener(this.seriesFollowingListener);

        this.setUpData();
    }

    private List<Episode> episodes() {
        int sortMode = MyScheduleActivity.sortModeBy(this.getContext(), this.scheduleMode);

        return App.scheduledEpisodes(this.scheduleMode, sortMode);
    }

    private void setUpData() {
        this.clear();

        for (Episode e : this.episodes()) {
            e.register(this);
            this.add(e);
        }

        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = this.itemViewFrom(convertView);

        Episode episode = this.getItem(position);
        Series series = App.getSeries(episode.seriesId());
        Season season = series.season(episode.seasonNumber());

        this.showData(episode, season, series, itemView);
        this.setUpSeenEpisodeCheckBoxListener(episode, itemView);

        return itemView;
    }

    private View itemViewFrom(View convertView) {
        View itemView = convertView;

        if (itemView == null) {
            itemView = this.layoutInflater.inflate(EPISODE_ITEM_RESOURCE_ID, null);
        }

        return itemView;
    }

    private void showData(Episode episode, Season season, Series series, View itemView) {
        TextView seriesTextView = (TextView) itemView.findViewById(R.id.episodeSeriesTextView);
        TextView seasonTextView = (TextView) itemView.findViewById(R.id.episodeSeasonEpisodeTextView);
        TextView dateTextView = (TextView) itemView.findViewById(R.id.episodeDateTextView);
        CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        seriesTextView.setText(series.name());

        String format = this.getContext().getString(R.string.episode_number_format);
        seasonTextView.setText(String.format(format, season.number(), episode.number()));

        java.text.DateFormat dateFormat = App.environment().localization().dateFormat();
        dateTextView.setText(Dates.toString(episode.airDate(), dateFormat, ""));

        isViewedCheckBox.setChecked(episode.wasSeen());
    }

    private void setUpSeenEpisodeCheckBoxListener(final Episode episode, View itemView) {
        final CheckBox isViewedCheckBox = (CheckBox) itemView.findViewById(R.id.episodeIsViewedCheckBox);

        isViewedCheckBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (isViewedCheckBox.isChecked()) {
                    SERIES_PROVIDER.markEpisodeAsSeen(episode);
                } else {
                    SERIES_PROVIDER.markEpisodeAsNotSeen(episode);
                }
            }
        });
    }

    @Override
    public void onMarkAsSeen(Episode e) {
        this.remove(e);
    }

    @Override
    public void onMarkAsNotSeen(Episode e) {
        this.setUpData();
    }

    @Override
    public void onMerge(Episode e) {
        this.notifyDataSetChanged();
    }
}
