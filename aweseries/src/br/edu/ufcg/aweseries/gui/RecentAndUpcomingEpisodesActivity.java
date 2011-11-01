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
