package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;
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
    protected void init() { /* There's nothing to initialize */ }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.my_schedule);
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.recent, ScheduleFragment.newInstance(ScheduleMode.RECENT)),
            new TabDefinition(R.string.next, ScheduleFragment.newInstance(ScheduleMode.NEXT)),
            new TabDefinition(R.string.upcoming, ScheduleFragment.newInstance(ScheduleMode.UPCOMING))
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return this.getIntent().getExtras().getInt(Extra.SCHEDULE_MODE);
    }
}
