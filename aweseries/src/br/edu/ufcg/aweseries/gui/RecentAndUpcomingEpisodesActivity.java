/*
 *   RecentAndUpcomingEpisodesActivity.java
 *
 *   Copyright 2011 Cleber Gonçalves de Sousa, Gabriel Assis Bezerra
 *                  and Tiago Almeida Reul
 *
 *   All rights reserved.
 *
 *   This file is part of aweseries.
 *
 *   aweseries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   aweseries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with aweseries.  If not, see <http://www.gnu.org/licenses/>.
 *
 *   Contributors:
 *      Cleber Gonçalves de Sousa
 *      Gabriel Assis Bezerra
 *      Tiago Almeida Reul
 */

package br.edu.ufcg.aweseries.gui;

import br.edu.ufcg.aweseries.R;
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
        setContentView(R.layout.recent_and_upcoming_view);

        this.tabHost = (TabHost) findViewById(android.R.id.tabhost);

        TabSpec recentEpisodesTabSpec = tabHost.newTabSpec("recent");
        TabSpec upcomingEpisodesTabSpec = tabHost.newTabSpec("upcoming");

        recentEpisodesTabSpec.setIndicator("Recent Episodes");
        recentEpisodesTabSpec.setContent(new Intent(this, RecentEpisodesActivity.class));
        upcomingEpisodesTabSpec.setIndicator("Upcoming Episodes");
        upcomingEpisodesTabSpec.setContent(new Intent(this, UpcomingEpisodesActivity.class));

        tabHost.addTab(recentEpisodesTabSpec);
        tabHost.addTab(upcomingEpisodesTabSpec);
    }
}
