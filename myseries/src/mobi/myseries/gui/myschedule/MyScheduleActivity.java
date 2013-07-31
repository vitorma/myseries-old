package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;

public class MyScheduleActivity extends TabActivity {

    public static Intent newIntent(Context context, int scheduleMode) {
        return new Intent(context, MyScheduleActivity.class).putExtra(Extra.SCHEDULE_MODE, scheduleMode);
    }

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
    protected void init(Bundle savedInstanceState) { /* There's nothing to initialize */ }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.my_schedule);
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected Intent navigateUpIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getText(R.string.nav_schedule);
    }

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
}
