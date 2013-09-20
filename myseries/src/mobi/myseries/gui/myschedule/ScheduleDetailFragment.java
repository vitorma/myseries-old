package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
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

public class ScheduleDetailFragment extends Fragment implements OnPageChangeListener {

    public static interface OnSelectPageListener {
        public void onSelectPage(int position);
    }

    public static ScheduleDetailFragment newInstance(int scheduleMode, int position) {
        Bundle arguments = new Bundle();

        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);
        arguments.putInt(Extra.POSITION, position);

        ScheduleDetailFragment instance = new ScheduleDetailFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private ScheduleMode mScheduleMode;
    private int mCurrentPosition;

    private ViewPager mPager;
    private ScheduleDetailAdapter mAdapter;
    private OnSelectPageListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnSelectPageListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EpisodePagerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        int scheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
        mCurrentPosition = getArguments().getInt(Extra.EPISODE_NUMBER);

        ScheduleSpecification specification = App.preferences().forMySchedule(scheduleMode).fullSpecification();

        switch (scheduleMode) {
            case ScheduleMode.AIRED:
                mScheduleMode = App.schedule().aired(specification);
                break;
            case ScheduleMode.UNAIRED:
                mScheduleMode = App.schedule().unaired(specification);
                break;
            case ScheduleMode.TO_WATCH:
            default:
                mScheduleMode = App.schedule().toWatch(specification);
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myschedule_detail_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ScheduleDetailAdapter(getFragmentManager(), mScheduleMode);
        mPager = (ViewPager) getView().findViewById(R.id.pager);

        PagerTabStrip titles = (PagerTabStrip) getView().findViewById(R.id.titles);
        titles.setTextColor(App.resources().getColor(R.color.dark_blue));
        titles.setTabIndicatorColorResource(R.color.dark_blue);

        mPager.setAdapter(mAdapter);
        mPager.setOnPageChangeListener(this);

        selectPage(mCurrentPosition);
    }

    public void update(ScheduleMode scheduleMode, int position) {
        mScheduleMode = scheduleMode;
        mAdapter = new ScheduleDetailAdapter(getFragmentManager(), mScheduleMode);

        mPager.setAdapter(mAdapter);
        selectPage(position);
    }

    public void update(ScheduleMode scheduleMode) {
        update(scheduleMode, mCurrentPosition);
    }

    public void selectPage(int position) {
        mCurrentPosition = position;

        mPager.setCurrentItem(mCurrentPosition, true);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) { /* Do nothing */ }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) { /* Do nothing */ }

    @Override
    public void onPageSelected(int position) {
        mListener.onSelectPage(position);
    }
}
