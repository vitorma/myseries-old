package mobi.myseries.gui.season;

import java.util.Collections;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.EpisodeListener;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.SeasonListener;
import mobi.myseries.gui.shared.CheckableFrameLayout;
import mobi.myseries.gui.shared.CheckableFrameLayout.OnCheckedListener;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.Objects;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class SeasonAdapter extends BaseAdapter implements SeasonListener, EpisodeListener, OnSharedPreferenceChangeListener {
    private int seriesId;
    private int seasonNumber;

    private List<Episode> items;

    public SeasonAdapter(int seriesId, int seasonNumber) {
        this.seriesId = seriesId;
        this.seasonNumber = seasonNumber;

        this.loadEpisodes();
    }

    private void loadEpisodes() {
        Season season = App.seriesProvider().getSeries(this.seriesId).season(this.seasonNumber);
        this.items = season.episodes();

        season.register(this);
        for (Episode e : this.items) {
            e.register(this);
        }

        int sortMode = App.preferences().forSeason().sortMode();

        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                Collections.sort(this.items, EpisodeComparator.byNumber());
                break;
            case SortMode.NEWEST_FIRST:
            default:
                Collections.sort(this.items, EpisodeComparator.byNumberReversed());
                break;
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
        return this.items.get(position).id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = Objects.nullSafe(convertView, View.inflate(App.context(), R.layout.season_item_episode, null));
        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Episode episode = this.items.get(position);

        String format = App.resources().getString(R.string.episode_number_format_ext);
        viewHolder.episodeNumber.setText(String.format(format, episode.number()));
        viewHolder.episodeName.setText(episode.name());
        viewHolder.episodeSeenMark.setChecked(episode.wasSeen());
        viewHolder.episodeSeenMark.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));

        return view;
    }

    /* ViewHolder */

    private static class ViewHolder {
        private CheckedTextView episodeNumber;
        private CheckedTextView episodeName;
        private SeenMark episodeSeenMark;

        private ViewHolder(View view) {
            this.episodeNumber = (CheckedTextView) view.findViewById(R.id.episodeNumber);
            this.episodeName = (CheckedTextView) view.findViewById(R.id.episodeName);
            this.episodeSeenMark = (SeenMark) view.findViewById(R.id.seenMark);

            ((CheckableFrameLayout) view).setOnCheckedListener(this.checkableFrameLayoutListener());

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

        private OnCheckedListener checkableFrameLayoutListener() {
            return new OnCheckedListener() {
                @Override
                public void onChecked(boolean checked) {
                    ViewHolder.this.episodeNumber.setChecked(checked);
                    ViewHolder.this.episodeName.setChecked(checked);

                    ViewHolder.this.episodeSeenMark.setImageDrawable(
                        checked ?
                        App.resources().getDrawable(R.drawable.watchmark_dark) :
                        App.resources().getDrawable(R.drawable.watchmark_light));
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        this.loadEpisodes();
        this.notifyDataSetChanged();
    }
}
