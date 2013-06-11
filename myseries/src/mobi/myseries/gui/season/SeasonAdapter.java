package mobi.myseries.gui.season;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.Objects;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeasonAdapter extends BaseAdapter implements SeasonListener, EpisodeListener {
    List<Episode> items;

    public SeasonAdapter(int seriesId, int seasonNumber) {
        Season season = App.seriesProvider().getSeries(seriesId).season(seasonNumber);

        this.items = season.episodes();

        season.register(this);
        for (Episode e : this.items) {
            e.register(this);
        }
    }

    @Override
    public int getCount() {
        return this.items.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = Objects.nullSafe(convertView, View.inflate(App.context(), R.layout.series_seasons_item_episode, null));
        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Episode episode = this.items.get(position);

        viewHolder.episodeNumber.setText(String.format("%02d", episode.number()));
        viewHolder.episodeName.setText(episode.name());
        viewHolder.episodeSeenMark.setChecked(episode.wasSeen());
        viewHolder.episodeSeenMark.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));

        return view;
    }

    /* ViewHolder */

    private static class ViewHolder {
        private TextView episodeNumber;
        private TextView episodeName;
        private SeenMark episodeSeenMark;

        private ViewHolder(View view) {
            this.episodeNumber = (TextView) view.findViewById(R.id.episodeNumber);
            this.episodeName = (TextView) view.findViewById(R.id.episodeName);
            this.episodeSeenMark = (SeenMark) view.findViewById(R.id.seenMark);

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.episodeSeenMark.isChecked()) {
                        App.seriesProvider().markEpisodeAsSeen(episode);
                    } else {
                        App.seriesProvider().markEpisodeAsNotSeen(episode);
                    }
                }
            };
        }
    }

    @Override
    public void onMarkAsSeen(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeen(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNumberOfSeenEpisodes(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onChangeNextEpisodeToSee(Season season) {
        /* Is not my problem */
    }

    @Override
    public void onMarkAsSeenBySeries(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeenBySeries(Season season) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsSeen(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeen(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsSeenBySeason(Episode episode) {
        this.notifyDataSetChanged();
    }

    @Override
    public void onMarkAsNotSeenBySeason(Episode episode) {
        this.notifyDataSetChanged();
    }
}
