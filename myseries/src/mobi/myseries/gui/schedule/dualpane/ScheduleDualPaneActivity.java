package mobi.myseries.gui.schedule.dualpane;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.schedule.EpisodeFilterDialogFragment;
import mobi.myseries.gui.schedule.EpisodeSortingDialogFragment;
import mobi.myseries.gui.schedule.SeriesFilterDialogFragment;
import mobi.myseries.gui.schedule.dualpane.ActionBarTabAdapter.OnTabSelectedListener;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabDefinition;
import mobi.myseries.gui.shared.ToastBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

public class ScheduleDualPaneActivity extends BaseActivity implements OnTabSelectedListener {
    private static final int NATURAL_FIRST_POSITION = 0;

    private int mSelectedMode;
    private ActionBarTabAdapter mTabAdapter;

    /* Intents */

    public static Intent newIntent(Context context, int scheduleMode) {
        return newIntent(context, scheduleMode, NATURAL_FIRST_POSITION);
    }

    public static Intent newIntent(Context context, int scheduleMode, int selectedItem) {
        return new Intent(context, ScheduleDualPaneActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode)
            .putExtra(Extra.POSITION, selectedItem);
    }

    /* Menu */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.myschedule, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (this.isDrawerOpen()) {
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
        this.extractExtras(savedInstanceState);
        this.setUpView(savedInstanceState);
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.ab_title_schedule);
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getText(R.string.nav_schedule);
    }

    @Override
    protected int layoutResource() {
        return R.layout.myschedule_activity_dualpane;
    }

    /* Activity */

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SCHEDULE_MODE, mSelectedMode);

        super.onSaveInstanceState(outState);
    }

    /* OnSelectedTabListener */

    @Override
    public void onTabSelected(int position) {
        mSelectedMode = position;
    }

    /* Auxiliary */

    private void extractExtras(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mSelectedMode = getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
        } else {
            mSelectedMode = savedInstanceState.getInt(Extra.SCHEDULE_MODE);
        }
    }

    private void setUpView(Bundle savedInstanceState) {
        ViewPager tabPager = (ViewPager) findViewById(R.id.tabPager);
        mTabAdapter = new ActionBarTabAdapter(getFragmentManager(), getActionBar(), tabPager, tabDefinitions(), mSelectedMode);
        mTabAdapter.register(this);
    }

    private TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
                new TabDefinition(
                        R.string.tab_next_to_watch,
                        ScheduleFragment.newInstance(ScheduleMode.TO_WATCH, selectedItemFor(ScheduleMode.TO_WATCH))),
                new TabDefinition(
                        R.string.tab_aired,
                        ScheduleFragment.newInstance(ScheduleMode.AIRED, selectedItemFor(ScheduleMode.AIRED))),
                new TabDefinition(
                        R.string.tab_unaired,
                        ScheduleFragment.newInstance(ScheduleMode.UNAIRED, selectedItemFor(ScheduleMode.UNAIRED)))
        };
    }

    private int selectedItemFor(int scheduleMode) {
        return scheduleMode == mSelectedMode ?
                getIntent().getExtras().getInt(Extra.POSITION) :
                NATURAL_FIRST_POSITION;
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
