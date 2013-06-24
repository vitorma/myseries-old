package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.gui.shared.Extra;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class EpisodePagerFragment extends Fragment implements OnPageChangeListener {

    public static interface EpisodePagerFragmentListener {
        public void onSelectPage(int position);
    }

    public static EpisodePagerFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);
        arguments.putInt(Extra.EPISODE_NUMBER, episodeNumber);

        EpisodePagerFragment instance = new EpisodePagerFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private Season season;
    private int episodeNumber;
    private EpisodePagerFragmentListener listener;

    private EpisodePagerAdapter adapter;
    private ViewPager pager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (EpisodePagerFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EpisodePagerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);

        int seriesId = this.getArguments().getInt(Extra.SERIES_ID);
        int seasonNumber = this.getArguments().getInt(Extra.SEASON_NUMBER);

        this.season = App.seriesProvider().getSeries(seriesId).season(seasonNumber);
        this.episodeNumber = this.getArguments().getInt(Extra.EPISODE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.episode_pager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.adapter = new EpisodePagerAdapter(this.getFragmentManager(), this.season.episodes());

        this.pager = (ViewPager) this.getView().findViewById(R.id.pager);

        PagerTabStrip titles = (PagerTabStrip) this.getView().findViewById(R.id.titles);
        titles.setTextColor(App.resources().getColor(R.color.dark_blue));
        titles.setTabIndicatorColorResource(R.color.dark_blue);

        this.pager.setAdapter(this.adapter);
        this.pager.setOnPageChangeListener(this);

        Episode current = this.season.episode(this.episodeNumber);
        this.selectPage(this.adapter.positionOf(current));
    }

    public void update(Season season, int episodeNumber) {
        this.season = season;
        this.episodeNumber = episodeNumber;

        this.adapter = new EpisodePagerAdapter(this.getFragmentManager(), this.season.episodes());
        this.pager.setAdapter(this.adapter);
        this.selectPage(this.adapter.positionOf(this.season.episode(this.episodeNumber)));
    }

    public void update(Season season) {
        this.update(season, this.episodeNumber);
    }

    public void selectPage(int position) {
        this.episodeNumber = this.adapter.episodeAt(position).number();

        this.pager.setCurrentItem(position, true);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) { /* Do nothing */ }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) { /* Do nothing */ }

    @Override
    public void onPageSelected(int position) {
        this.listener.onSelectPage(position);
    }
}
