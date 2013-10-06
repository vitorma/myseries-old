package mobi.myseries.gui.myschedule.singlepane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.myschedule.EpisodeFilterDialogFragment;
import mobi.myseries.gui.myschedule.EpisodeSortingDialogFragment;
import mobi.myseries.gui.myschedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.myschedule.singlepane.ActionBarTabAdapter.OnTabSelectedListener;
import mobi.myseries.gui.myschedule.singlepane.ScheduleListFragment.OnItemClickListener;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabDefinition;
import mobi.myseries.gui.shared.ToastBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class MyScheduleSinglePaneActivity extends BaseActivity implements OnTabSelectedListener, OnItemClickListener {
    private static final String EXTRA_SHOW_LIST = "showEpisodeListFragment";
    private static final int INVALID_POSITION = -1;

    private int mSelectedMode;
    private int mSelectedItem;
    private boolean mIsShowingList;

    private ViewPager mViewPager;
    private ActionBarTabAdapter mTabAdapter;

    /* Intents */

    public static Intent newIntent(Context context, int scheduleMode) {
        return newIntent(context, scheduleMode, INVALID_POSITION);
    }

    public static Intent newIntent(Context context, int scheduleMode, int selectedItem) {
        return new Intent(context, MyScheduleSinglePaneActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode)
            .putExtra(Extra.POSITION, selectedItem);
    }

    /* Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.myschedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (isDrawerOpen()) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }

            return true;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter_series:
                showSeriesFilterDialog();
                return true;
            case R.id.filter_episodes:
                showEpisodeFilterDialog();
                return true;
            case R.id.sort:
                showSortDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* BaseActivity */

    @Override
    protected void init(Bundle savedInstanceState) {
        extractExtras(savedInstanceState);
        setUpFragments(savedInstanceState);
    }

    @Override
    protected CharSequence title() {
        return getText(R.string.ab_title_schedule);
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return getText(R.string.nav_schedule);
    }

    @Override
    protected int layoutResource() {
        return R.layout.myschedule_activity_singlepane;
    }

    /* Activity */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SCHEDULE_MODE, mSelectedMode);
        outState.putInt(Extra.POSITION, mSelectedItem);
        outState.putBoolean(EXTRA_SHOW_LIST, mIsShowingList);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (shouldGoBackToListFragment()) {
            mIsShowingList = true;

            setUpAdapter();
        } else {
            super.onBackPressed();
        }
    }

    /* ActionBarTabAdapter.OnTabSelectedListener */

    @Override
    public void onTabSelected(int position) {
        mSelectedMode = position;
    }

    /* ScheduleListFragment.OnItemClickListener */

    @Override
    public void onItemClick(int scheduleMode, int position) {
        mIsShowingList = false;
        mSelectedItem = position;

        setUpAdapter();
    }

    /* Auxiliary */

    private void extractExtras(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mSelectedMode = getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
            mSelectedItem = getIntent().getExtras().getInt(Extra.POSITION);
            mIsShowingList = wasIntentedToShowList();
        } else {
            mSelectedMode = savedInstanceState.getInt(Extra.SCHEDULE_MODE);
            mSelectedItem = savedInstanceState.getInt(Extra.POSITION);
            mIsShowingList = savedInstanceState.getBoolean(EXTRA_SHOW_LIST);
        }
    }

    private void setUpFragments(Bundle savedInstanceState) {
        mViewPager = (ViewPager) findViewById(R.id.tabPager);

        setUpAdapter();
    }

    private void setUpAdapter() {
        if (mTabAdapter != null) {
            mTabAdapter.deregister(this);
            mTabAdapter.reset();
        }

        if (mIsShowingList) {
            mTabAdapter = newActionBarTabAdapter(newTabDefinitionsForList());
        } else {
            mTabAdapter = newActionBarTabAdapter(newTabDefinitionsForDetail());
        }

        mTabAdapter.register(this);
        mViewPager.setAdapter(mTabAdapter);
        mTabAdapter.notifyDataSetChanged();

        getActionBar().setSelectedNavigationItem(mSelectedMode);
    }

    private ActionBarTabAdapter newActionBarTabAdapter(TabDefinition[] tabDefinitions) {
        return new ActionBarTabAdapter(
                getFragmentManager(),
                getActionBar(),
                mViewPager,
                tabDefinitions);
    }

    private TabDefinition[] newTabDefinitionsForList() {
        return new TabDefinition[] {
            new TabDefinition(
                R.string.tab_next_to_watch,
                ScheduleListFragment.newInstance(ScheduleMode.TO_WATCH)),
            new TabDefinition(
                R.string.tab_aired,
                ScheduleListFragment.newInstance(ScheduleMode.AIRED)),
            new TabDefinition(
                R.string.tab_unaired,
                ScheduleListFragment.newInstance(ScheduleMode.UNAIRED))
        };
    }

    private TabDefinition[] newTabDefinitionsForDetail() {
        return new TabDefinition[] {
            new TabDefinition(
                R.string.tab_next_to_watch,
                ScheduleDetailFragment.newInstance(ScheduleMode.TO_WATCH, selectedItem(ScheduleMode.TO_WATCH))),
            new TabDefinition(
                R.string.tab_aired,
                ScheduleDetailFragment.newInstance(ScheduleMode.AIRED, selectedItem(ScheduleMode.AIRED))),
            new TabDefinition(
                R.string.tab_unaired,
                ScheduleDetailFragment.newInstance(ScheduleMode.UNAIRED, selectedItem(ScheduleMode.UNAIRED)))
        };
    }

    private int selectedItem(int scheduleMode) {
        return scheduleMode == mSelectedMode ? validSelectedItem() : 0;
    }

    private int validSelectedItem() {
        return mSelectedItem != INVALID_POSITION ? mSelectedItem : 0;
    }

    private boolean shouldGoBackToListFragment() {
        return !mIsShowingList && wasIntentedToShowList();
    }

    private boolean wasIntentedToShowList() {
        return getIntent().getExtras().getInt(Extra.POSITION) == INVALID_POSITION;
    }

    private void showSeriesFilterDialog() {
        if (App.seriesFollowingService().getAllFollowedSeries().isEmpty()) {
            new ToastBuilder(this).setMessage(R.string.no_series_to_show).build().show();
        } else {
            SeriesFilterDialogFragment.newInstance(mSelectedMode).show(getFragmentManager(), "seriesFilterDialog");
        }
    }

    private void showEpisodeFilterDialog() {
        EpisodeFilterDialogFragment.newInstance(mSelectedMode).show(getFragmentManager(), "episodeFilterDialog");
    }

    private void showSortDialog() {
        EpisodeSortingDialogFragment.newInstance(mSelectedMode).show(getFragmentManager(), "seriesSortingDialog");
    }
}