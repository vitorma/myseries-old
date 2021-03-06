package mobi.myseries.gui.schedule.dualpane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.BroadcastAction;
import mobi.myseries.application.schedule.ScheduleListener;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.schedule.ScheduleListAdapter;
import mobi.myseries.gui.schedule.SchedulePagerAdapter;
import mobi.myseries.gui.schedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.shared.Extra;
import android.app.Fragment;
import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class ScheduleFragment extends Fragment implements ScheduleListener, OnPageChangeListener, OnSharedPreferenceChangeListener {
    private int mScheduleMode;
    private int mSelectedItem;

    private ScheduleMode mItems;

    private ScheduleListAdapter mListAdapter;

    private SchedulePagerAdapter mPagerAdapter;
    private ListView mListView;
    private ViewPager mViewPager;
    private View mFullStateView;
    private View mEmptyStateView;

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

        App.preferences().forSchedule().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        App.preferences().forSchedule().deregister(this);
        mItems.deregister(this);
    }

    /* ScheduleListener */

    @Override
    public void onScheduleStateChanged() {
        if (mListAdapter != null && mPagerAdapter != null) {
            checkItem(mSelectedItem);
            mListAdapter.notifyDataSetChanged();
            mPagerAdapter.notifyDataSetChanged();
        }
        hideOrShowViews();
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
        mViewPager = (ViewPager) getView().findViewById(R.id.detailsPager);
        mFullStateView = getView().findViewById(R.id.fullStateView);
        mEmptyStateView = getView().findViewById(R.id.empty_state);

        PagerTitleStrip titles = (PagerTitleStrip) getView().findViewById(R.id.detailsTitles);
        titles.setTextColor(App.resources().getColor(R.color.dark_red));
    }

    private void setUpData() {
        if (mItems != null) { mItems.deregister(ScheduleFragment.this); }

        mItems = App.schedule().mode(mScheduleMode, App.preferences().forSchedule().fullSpecification());
        mListAdapter = new ScheduleListAdapter(mItems);
        mPagerAdapter = new SchedulePagerAdapter(mItems);

        mItems.register(this);
    }

    private void setUpViews() {
        setUpFullStateView();
        setUpEmptyStateView();
        hideOrShowViews();
    }

    private void hideOrShowViews() {
        if (mItems.numberOfEpisodes() > 0) {
            mFullStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setVisibility(View.GONE);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mFullStateView.setVisibility(View.GONE);
        }
    }

    private void setUpFullStateView() {
        mListView.setAdapter(mListAdapter);
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

    private void setUpEmptyStateView() {
        boolean thereAreFollowedSeries = !App.seriesFollowingService().getAllFollowedSeries().isEmpty();

        ScheduleSpecification specification = App.preferences().forSchedule().fullSpecification();
        boolean showHiddenEpisodesWarning = false;

        Button unhideSpecialEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideSpecialEpisodes);
        if (thereAreFollowedSeries && !specification.isSatisfiedBySpecialEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideSpecialEpisodes.setVisibility(View.VISIBLE);
            unhideSpecialEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forSchedule().putIfShowSpecialEpisodes(true);
                }
            });
        } else {
            unhideSpecialEpisodes.setVisibility(View.GONE);
        }

        Button unhideWatchedEpisodes = (Button) mEmptyStateView.findViewById(R.id.unhideWatchedEpisodes);
        if (thereAreFollowedSeries && mScheduleMode != ScheduleMode.TO_WATCH && !specification.isSatisfiedByWatchedEpisodes()) {
            showHiddenEpisodesWarning = true;
            unhideWatchedEpisodes.setVisibility(View.VISIBLE);
            unhideWatchedEpisodes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.preferences().forSchedule().putIfShowWatchedEpisodes(true);
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
        if (thereAreFollowedSeries && !isSatisfiedByEpisodesOfAllSeries) {
            showHiddenEpisodesWarning = true;
            unhideEpisodesOfSomeSeries.setVisibility(View.VISIBLE);
            unhideEpisodesOfSomeSeries.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SeriesFilterDialogFragment().show(getFragmentManager(), "seriesFilterDialog");
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        reload();

        App.context().sendBroadcast(new Intent(BroadcastAction.UPDATE));
    }
}