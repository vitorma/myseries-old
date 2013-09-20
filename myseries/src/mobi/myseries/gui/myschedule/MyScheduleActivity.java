package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

//TODO Remove this inheritance
public class MyScheduleActivity extends TabActivity {
    private static final String SCHEDULE_MASTER_FRAGMENT = "scheduleMasterFragment";
    private static final String SCHEDULE_DETAIL_FRAGMENT = "scheduleDetailFragment";
    private static final String EPISODE_SORTING_DIALOG_FRAGMENT = "episodeSortingDialogFragment";

    private static final String EXTRA_SHOW_LIST_FRAGMENT = "showEpisodeListFragment";
    private static final String EXTRA_SHOW_PAGER_FRAGMENT = "showEpisodePagerFragment";

    private static final int INVALID_SERIES_ID = -1;
    private static final int INVALID_SEASON_NUMBER = -1;
    private static final int INVALID_EPISODE_NUMBER = -1;
    private static final int NATURAL_FIRST_POSITION = 0;

    /* Intents */

    public static Intent newIntent(Context context, int scheduleMode) {
        return newIntent(context, scheduleMode, INVALID_SERIES_ID, INVALID_SEASON_NUMBER, INVALID_EPISODE_NUMBER);
    }

    public static Intent newIntent(Context context, int scheduleMode, int seriesId, int seasonNumber, int episodeNumber) {
        return new Intent(context, MyScheduleActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode)
            .putExtra(Extra.SERIES_ID, seriesId)
            .putExtra(Extra.SEASON_NUMBER, seasonNumber)
            .putExtra(Extra.EPISODE_NUMBER, episodeNumber);
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
        this.setUpDescendantNavigation();
        this.setUpFragments(savedInstanceState);
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

    /* TabActivity */

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.schedule_to_watch, ScheduleFragment.newInstance(ScheduleMode.TO_WATCH)),
            new TabDefinition(R.string.schedule_aired, ScheduleFragment.newInstance(ScheduleMode.AIRED)),
            new TabDefinition(R.string.schedule_unaired, ScheduleFragment.newInstance(ScheduleMode.UNAIRED))
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return this.getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
    }

    /* TODO */

    private void extractExtras(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    }

    private void setUpDescendantNavigation() {
        // TODO Auto-generated method stub
    }

    private void setUpFragments(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
    }

    /* Dual Pane */

    private boolean isDualPane() {
        return this.getResources().getBoolean(R.bool.isTablet);
    }
}
