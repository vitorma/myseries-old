package mobi.myseries.gui.episodes;

import java.util.Collections;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.marking.MarkingListener;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.CheckableFrameLayout;
import mobi.myseries.gui.shared.CheckableFrameLayout.OnCheckedListener;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.SortMode;
import mobi.myseries.shared.Objects;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

public class EpisodeListAdapter extends BaseAdapter {

    private Season season;
    private List<Episode> items;

    public EpisodeListAdapter(Season season) {
        this.season = season;

        App.markingService().register(mMarkingListener);

        this.setUpItems();
    }

    private void setUpItems() {
        this.items = this.season.episodes();

        int sortMode = App.preferences().forEpisodes().sortMode();

        switch (sortMode) {
            case SortMode.OLDEST_FIRST:
                Collections.sort(this.items, EpisodeComparator.byNumber());
                break;
            case SortMode.NEWEST_FIRST:
            default:
                Collections.sort(this.items, EpisodeComparator.byNumberReversed());
                break;
        }

        this.notifyDataSetChanged();
    }

    public int positionOf(Episode e) {
        return this.items.indexOf(e);
    }

    public Episode episodeAt(int position) {
        return this.items.get(position);
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
        View view = Objects.nullSafe(convertView, View.inflate(App.context(), R.layout.episode_list_item, null));
        ViewHolder viewHolder = (view == convertView) ? (ViewHolder) view.getTag() : new ViewHolder(view);

        Episode episode = this.items.get(position);

        String format = App.resources().getString(R.string.episode_number_format_ext);
        viewHolder.episodeNumber.setText(String.format(format, episode.number()));
        viewHolder.episodeName.setText(episode.title());
        viewHolder.episodeSeenMark.setChecked(episode.watched());
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
            this.episodeName = (CheckedTextView) view.findViewById(R.id.episodeTitle);
            this.episodeSeenMark = (SeenMark) view.findViewById(R.id.seenMark);

            boolean isDualPane = App.resources().getBoolean(R.bool.isTablet);

            if (isDualPane) {
                ((CheckableFrameLayout) view).changeBackgroundWhenChecked(true);
                ((CheckableFrameLayout) view).setOnCheckedListener(this.checkableFrameLayoutListener());
            }

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.episodeSeenMark.isChecked()) {
                        App.markingService().markAsWatched(episode);
                    } else {
                        App.markingService().markAsUnwatched(episode);
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

    private final MarkingListener mMarkingListener = new MarkingListener() {
        @Override
        public void onMarked(Episode e) {
            if ((e.seriesId() != season.seriesId()) || (e.seasonNumber() != season.number())) { return; }

            notifyDataSetChanged();
        }

        @Override
        public void onMarked(Season s) {
            if ((s.seriesId() != season.seriesId()) || (s.number() != season.number())) { return; }

            notifyDataSetChanged();
        }

        @Override
        public void onMarked(Series s) {
            if (s.id() != season.seriesId()) { return; }

            notifyDataSetChanged();
        }
    };
}
