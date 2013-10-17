package mobi.myseries.gui.myschedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferencesListener;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.myschedule.SchedulePagerAdapter;
import mobi.myseries.gui.myschedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.shared.Extra;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class ScheduleDetailFragment extends Fragment
        implements ScheduleListener, OnPageChangeListener, MySchedulePreferencesListener {

    private int mScheduleMode;
    private int mSelectedPage;

    private ScheduleMode mItems;

    private ViewPager mViewPager;
    private View mEmptyStateView;
    private SchedulePagerAdapter mPagerAdapter;

    /* New instance */

    public static ScheduleDetailFragment newInstance(int scheduleMode, int position) {
        Bundle arguments = new Bundle();

        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);
        arguments.putInt(Extra.POSITION, position);

        ScheduleDetailFragment instance = new ScheduleDetailFragment();
        instance.setArguments(arguments);

        return instance;
    }

    /* Fragment */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
        mSelectedPage = getArguments().getInt(Extra.POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myschedule_fragment_singlepane_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUp();
        mViewPager.setCurrentItem(mSelectedPage, true);
    }

    @Override
    public void onStart() {
        super.onStart();

        App.preferences().forMySchedule(mScheduleMode).register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forMySchedule(mScheduleMode).deregister(this);
        mItems.deregister(this);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        if (mPagerAdapter != null) {
            mPagerAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mSelectedPage, true);
        }
        hideOrshowViews();
    }

    @Override
    public void onScheduleStructureChanged() {
        reload();
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageScrollStateChanged(int arg0) { /* Do nothing */ }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) { /* Do nothing */ }

    @Override
    public void onPageSelected(int position) {
        mSelectedPage = position;
    }

    /* Auxiliary */

    private void setUp() {
        setUpData();
        findViews();
        setUpViews();
    }

    private void reload() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                setUpData();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                setUpViews();
                mViewPager.setCurrentItem(mSelectedPage, true);
            }
        }.execute();
    }

    private void setUpData() {
        ScheduleSpecification specification = App.preferences().forMySchedule(mScheduleMode).fullSpecification();
        mItems = App.schedule().mode(mScheduleMode, specification);
        mItems.register(this);
    }

    private void findViews() {
        mViewPager = (ViewPager) getView().findViewById(R.id.pager);
        mEmptyStateView = getView().findViewById(R.id.empty_state);

        PagerTitleStrip titles = (PagerTitleStrip) getView().findViewById(R.id.titles);
        titles.setTextColor(App.resources().getColor(R.color.dark_red));
    }

    private void setUpViews() {
         setUpViewPager();
         setUpEmptyStateView();
         hideOrshowViews();
    }
    
    private void hideOrshowViews() {
        if (mItems.numberOfEpisodes() > 0) {
            mEmptyStateView.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
        }
    }

    private void setUpViewPager() {
        mPagerAdapter = new SchedulePagerAdapter(mItems);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void setUpEmptyStateView() {
        ScheduleSpecification specification = App.preferences().forMySchedule(mScheduleMode).fullSpecification();
        boolean showHiddenEpisodesWarning = false;

        Button unhideSpecialEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideSpecialEpisodes);
        if (!specification.isSatisfiedBySpecialEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideSpecialEpisodes.setVisibility(View.VISIBLE);
            unhideSpecialEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forMySchedule(mScheduleMode).putIfShowSpecialEpisodes(true);
                }
            });
        } else {
            unhideSpecialEpisodes.setVisibility(View.GONE);
        }

        Button unhideWatchedEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideWatchedEpisodes);
        if (mScheduleMode != ScheduleMode.TO_WATCH && !specification.isSatisfiedByWatchedEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideWatchedEpisodes.setVisibility(View.VISIBLE);
            unhideWatchedEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forMySchedule(mScheduleMode).putIfShowWatchedEpisodes(true);
                }
            });
        } else {
            unhideWatchedEpisodes.setVisibility(View.GONE);
        }

        Button unhideEpisodesOfSomeSeries = (Button) mEmptyStateView.findViewById(R.id.unhideEpisodesOfSomeSeries);
        boolean isSatisfiedByEpisodesOfAllSeries = true;
        for (Series s : App.seriesFollowingService().getAllFollowedSeries()) {
            if (!specification.isSatisfiedByEpisodesOfSeries(s.id())) {
                isSatisfiedByEpisodesOfAllSeries = false;
                break;
            }
        }
        if (!isSatisfiedByEpisodesOfAllSeries) {
            showHiddenEpisodesWarning = true;
            unhideEpisodesOfSomeSeries.setVisibility(View.VISIBLE);
            unhideEpisodesOfSomeSeries.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    SeriesFilterDialogFragment.newInstance(mScheduleMode).show(getFragmentManager(), "seriesFilterDialog");
                }
            });
        } else {
            unhideEpisodesOfSomeSeries.setVisibility(View.GONE);
        }

        View hiddenEpisodesWarning = mEmptyStateView.findViewById(R.id.hiddenEpisodes);
        if (showHiddenEpisodesWarning) {
            hiddenEpisodesWarning.setVisibility(View.VISIBLE);
        } else {
            hiddenEpisodesWarning.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSeriesToShowChange() {
        reload();
    }

    @Override
    public void onEpisodesToShowChange() {
        reload();
    }

    @Override
    public void onSortingChange() {
        reload();
    }
}
