package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

public class ScheduleDetailAdapter extends FragmentStatePagerAdapter {
    private ScheduleMode mScheduleMode;

    public ScheduleDetailAdapter(FragmentManager fm, ScheduleMode scheduleMode) {
        super(fm);

        mScheduleMode = scheduleMode;
    }

    @Override
    public Fragment getItem(int position) {
        Episode e = mScheduleMode.episodeAt(position);

        return ScheduleDetailItemFragment.newInstance(e.seriesId(), e.seasonNumber(), e.number());
    }

    @Override
    public int getCount() {
        return mScheduleMode.numberOfEpisodes();
    }

    @Override
    public String getPageTitle(int position) {
        //XXX (Cleber) Show episode airDate and airTime
        Episode e = mScheduleMode.episodeAt(position);
        String format = App.resources().getString(R.string.episode_number_format_ext);

        return String.format(format, e.number());
    }
}
