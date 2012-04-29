/*
 *   RecentAndUpcomingEpisodesActivity.java
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

package mobi.myseries.gui;

import mobi.myseries.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class RecentAndUpcomingEpisodesActivity extends TabActivity {
    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.recent_and_upcoming_view);

        this.tabHost = (TabHost) this.findViewById(android.R.id.tabhost);

        final TabSpec recentEpisodesTabSpec = this.tabHost.newTabSpec("recent");
        final TabSpec upcomingEpisodesTabSpec = this.tabHost.newTabSpec("upcoming");

        recentEpisodesTabSpec.setIndicator(this.getString(R.string.recent_episodes));
        recentEpisodesTabSpec.setContent(new Intent(this, RecentEpisodesActivity.class));
        upcomingEpisodesTabSpec.setIndicator(this.getString(R.string.upcoming_episodes));
        upcomingEpisodesTabSpec.setContent(new Intent(this, UpcomingEpisodesActivity.class));

        this.tabHost.addTab(recentEpisodesTabSpec);
        this.tabHost.addTab(upcomingEpisodesTabSpec);
    }
}
