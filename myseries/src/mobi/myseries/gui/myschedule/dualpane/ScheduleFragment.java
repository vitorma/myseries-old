package mobi.myseries.gui.myschedule.dualpane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.preferences.MySchedulePreferencesListener;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.myschedule.ScheduleListAdapter;
import mobi.myseries.gui.myschedule.SchedulePagerAdapter;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.PauseOnScrollListener;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ScheduleFragment extends Fragment implements ScheduleListener, OnPageChangeListener, MySchedulePreferencesListener {
    private int mScheduleMode;
    private int mSelectedItem;

    private ScheduleMode mItems;

    private ScheduleListAdapter mListAdapter;
    private SchedulePagerAdapter mPagerAdapter;
    private ListView mListView;
    private ViewPager mViewPager;
    
    private AsyncTask<Void, Void, Void> loadTask;
    private boolean isLoading = false;

    public static ScheduleFragment newInstance(int scheduleMode, int selectedItem) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);
        arguments.putInt(Extra.POSITION, selectedItem);

        ScheduleFragment instance = new ScheduleFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        mScheduleMode = getArguments().getInt(Extra.SCHEDULE_MODE);
        mSelectedItem = getArguments().getInt(Extra.POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myschedule_fragment_dualpane, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUp();
        checkItem(mSelectedItem);
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
        mListAdapter.resetViewStates();
        mListAdapter.notifyDataSetChanged();

        mPagerAdapter = new SchedulePagerAdapter(mItems);
        mViewPager.setAdapter(mPagerAdapter);
        mPagerAdapter.notifyDataSetChanged();

        checkItem(mSelectedItem);
    }

    @Override
    public void onScheduleStructureChanged() {
        reload();
    }

    /* ViewPager.OnPageChangeListener */

    @Override
    public void onPageScrollStateChanged(int arg0) { }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) { }

    @Override
    public void onPageSelected(int position) {
        selectItem(position);
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

    /* Auxiliary */

    private void setUp() {
        findViews();
        setUpData();
        setUpViews();
    }

    private void reload() {
        if(isLoading)
            loadTask.cancel(true);
        loadTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                isLoading = true;
            }

            @Override
            protected Void doInBackground(Void... params) {
                setUpData();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if(!isCancelled()) {
                    setUpViews();
                    checkItem(mSelectedItem);
                }
                isLoading = false;
            }
        }.execute();
    }

    private void findViews() {
        mListView = (ListView) getView().findViewById(R.id.masterList);
        mViewPager = (ViewPager) this.getView().findViewById(R.id.detailsPager);
    }

    private void setUpData() {
        if (mItems != null) { mItems.deregister(ScheduleFragment.this); }

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forMySchedule(mScheduleMode).fullSpecification());
        mListAdapter = new ScheduleListAdapter(mItems);
        mPagerAdapter = new SchedulePagerAdapter(mItems);

        mItems.register(this);
    }

    private void setUpViews() {
        mListView.setAdapter(mListAdapter);
        mListView.setOnScrollListener(new PauseOnScrollListener(false, true));
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(this);
        mPagerAdapter.notifyDataSetChanged();
    }

    private void selectItem(int position) {
        if (mSelectedItem == position) { return; }

        mSelectedItem = position;

        checkItem(mSelectedItem);
    }

    private void checkItem(int position) {
        if (position != mListView.getCheckedItemPosition()) {
            mListView.setItemChecked(position, true);
            if (isNotVisible(position)) { mListView.smoothScrollToPosition(position); }
        }

        if (position != mViewPager.getCurrentItem()) {
            mViewPager.setCurrentItem(position, true);
        }
    }

    private boolean isNotVisible(int position) {
        return position <= mListView.getFirstVisiblePosition() ||
                position >= mListView.getLastVisiblePosition();
    }
}