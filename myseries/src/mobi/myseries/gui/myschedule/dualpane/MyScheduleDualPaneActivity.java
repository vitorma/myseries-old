package mobi.myseries.gui.myschedule.dualpane;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.myschedule.dualpane.ActionBarTabAdapter.OnTabSelectedListener;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.TabDefinition;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class MyScheduleDualPaneActivity extends BaseActivity implements OnTabSelectedListener {
    private static final int NATURAL_FIRST_POSITION = 0;

    private int mSelectedMode;
    private ActionBarTabAdapter mTabAdapter;

    /* Intents */

    public static Intent newIntent(Context context, int scheduleMode) {
        return newIntent(context, scheduleMode, NATURAL_FIRST_POSITION);
    }

    public static Intent newIntent(Context context, int scheduleMode, int selectedItem) {
        return new Intent(context, MyScheduleDualPaneActivity.class)
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

    /* BaseActivity */

    @Override
    protected void init(Bundle savedInstanceState) {
        this.extractExtras(savedInstanceState);
        this.setUpView(savedInstanceState);
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.my_schedule);
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
                        R.string.schedule_to_watch,
                        ScheduleFragment.newInstance(ScheduleMode.TO_WATCH, selectedItemFor(ScheduleMode.TO_WATCH))),
                new TabDefinition(
                        R.string.schedule_aired,
                        ScheduleFragment.newInstance(ScheduleMode.AIRED, selectedItemFor(ScheduleMode.AIRED))),
                new TabDefinition(
                        R.string.schedule_unaired,
                        ScheduleFragment.newInstance(ScheduleMode.UNAIRED, selectedItemFor(ScheduleMode.UNAIRED)))
        };
    }

    private int selectedItemFor(int scheduleMode) {
        return scheduleMode == mSelectedMode ?
                getIntent().getExtras().getInt(Extra.POSITION) :
                NATURAL_FIRST_POSITION;
    }
}
