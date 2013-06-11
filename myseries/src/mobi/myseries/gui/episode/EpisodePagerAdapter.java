package mobi.myseries.gui.episode;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentStatePagerAdapter;

public class EpisodePagerAdapter extends FragmentStatePagerAdapter {
    private Context context;
    private List<Episode> episodes;

    public EpisodePagerAdapter(Context context, FragmentManager fm, List<Episode> episodes) {
        super(fm);

        this.context = context;
        this.episodes = episodes;
    }

    public int positionOf(Episode episode) {
        return this.episodes.indexOf(episode);
    }

    @Override
    public Fragment getItem(int position) {
        Episode episode = this.episodes.get(position);
        return EpisodeFragment.newInstance(episode.seriesId(), episode.seasonNumber(), episode.number());
    }

    @Override
    public int getCount() {
        return this.episodes.size();
    }

    @Override
    public String getPageTitle(int position) {
        Episode e = this.episodes.get(position);
        String format = this.context.getString(R.string.episode_number_format);
        return String.format(format, e.seasonNumber(), e.number());
    }
}
