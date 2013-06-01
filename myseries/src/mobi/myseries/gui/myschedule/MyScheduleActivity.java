/*
 *   MyScheduleActivity.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

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
        return new Intent(context, MyScheduleActivity.class)
            .putExtra(Extra.SCHEDULE_MODE, scheduleMode);
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
