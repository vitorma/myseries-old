package mobi.myseries.gui.myschedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.gui.myschedule.SchedulePagerAdapter;
import mobi.myseries.gui.shared.Extra;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleDetailFragment extends Fragment
        implements ScheduleListener, OnPageChangeListener, OnSharedPreferenceChangeListener {

    private int mScheduleMode;
    private int mSelectedPage;

    private ScheduleMode mItems;

    private ViewPager mViewPager;
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

        App.preferences().forActivities().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forActivities().register(this);
        mItems.deregister(this);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        mPagerAdapter = new SchedulePagerAdapter(mItems);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        mViewPager.setCurrentItem(mSelectedPage, true);
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

    /* SharedPreferences.OnSharedPreferenceChangeListener */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        reload();
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

        PagerTabStrip titles = (PagerTabStrip) getView().findViewById(R.id.titles);
        titles.setTextColor(App.resources().getColor(R.color.dark_blue));
        titles.setTabIndicatorColorResource(R.color.dark_blue);
    }

    private void setUpViews() {
        mPagerAdapter = new SchedulePagerAdapter(mItems);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
    }
}
