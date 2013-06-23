package mobi.myseries.gui.episodes;

import java.util.Collections;
import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.shared.EpisodeComparator;
import mobi.myseries.gui.shared.SortMode;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class EpisodePagerAdapter extends FragmentStatePagerAdapter {
    private List<Episode> episodes;

    public EpisodePagerAdapter(FragmentManager fm, List<Episode> episodes) {
        super(fm);

        this.episodes = episodes;
        this.sortItems();
    }

    public int positionOf(Episode episode) {
        return this.episodes.indexOf(episode);
    }

    public Episode episodeAt(int position) {
        return this.episodes.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Episode episode = this.episodes.get(position);
        return EpisodeDetailsFragment.newInstance(episode.seriesId(), episode.seasonNumber(), episode.number());
    }

    @Override
    public int getCount() {
        return this.episodes.size();
    }

    @Override
    public String getPageTitle(int position) {
        Episode e = this.episodes.get(position);
        String format = App.resources().getString(R.string.episode_number_format);
        return String.format(format, e.seasonNumber(), e.number());
    }

    private void sortItems() {
        int sortMode = App.preferences().forEpisodes().sortMode();

        switch (sortMode) {
            case SortMode.NEWEST_FIRST:
                Collections.sort(this.episodes, EpisodeComparator.byNumberReversed());
                break;
            case SortMode.OLDEST_FIRST:
            default:
                Collections.sort(this.episodes, EpisodeComparator.byNumber());
                break;
        }

        this.notifyDataSetChanged();
    }
}
