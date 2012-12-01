/*
 *   SetupService.java
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

package mobi.myseries.gui.appwidget;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.preferences.Preferences;
import mobi.myseries.gui.preferences.SchedulePreferences.AppWidgetPreferences;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class SetupService extends IntentService {
    private static final int ITEMS_PER_PAGE = 4;

    private int appWidgetId;
    private String action;
    private AppWidgetPreferences preferences;
    private RemoteViews appWidgetView;
    private List<Episode> episodes;
    private ItemPageBrowser itemPageBrowser;

    public SetupService() {
        super("mobi.myseries.gui.appwidget.SetupService");
    }

    public static Intent newIntent(Context context, int appWidgetId, String action) {
        Intent intent = new Intent(context, SetupService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setAction(action);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        return intent;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(this.getApplicationContext());

        this.appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
        this.action = intent.getAction();
        this.preferences = Preferences.forAppWidget(this.appWidgetId);
        this.loadEpisodes();
        this.setUpItemPageBrowser();
        this.setUpAppWidgetView();

        mgr.updateAppWidget(this.appWidgetId, this.appWidgetView);
    }

    private void loadEpisodes() {
        int scheduleMode = this.preferences.scheduleMode();
        ScheduleSpecification specification = this.preferences.fullSpecification();

        switch(scheduleMode) {
            case ScheduleMode.RECENT:
                this.episodes = App.schedule().modeRecent(specification).episodes();
                break;
            case ScheduleMode.NEXT:
                this.episodes = App.schedule().modeNext(specification).episodes();
                break;
            case ScheduleMode.UPCOMING:
                this.episodes = App.schedule().modeUpcoming(specification).episodes();
                break;
        }
    }

    private void setUpItemPageBrowser() {
        this.itemPageBrowser = ItemPageBrowser.from(this.episodes.size(), ITEMS_PER_PAGE)
            .goToPage(this.currentPage())
            .navigateAccordingToAction(this.action);

        this.saveCurrentPage();
    }

    private void setUpAppWidgetView() {
        this.appWidgetView = new RemoteViews(this.getPackageName(), R.layout.appwidget_myschedule);

        this.setUpActionBar();
        this.setUpItemPageBar();
        this.setUpItems();
    }

    private void setUpActionBar() {
        ActionBar.from(this.getApplicationContext(), this.appWidgetView).setUpFor(this.appWidgetId);
    }

    private void setUpItemPageBar() {
        ItemPageBar.from(this.getApplicationContext(), this.appWidgetView)
            .setupFor(this.appWidgetId)
            .showNavigationAccordingTo(this.itemPageBrowser);
    }

    private void setUpItems() {
        this.appWidgetView.removeAllViews(R.id.episodes);

        if (this.episodes.isEmpty()) {
            RemoteViews item = Item.from(this.getApplicationContext()).empty();
            this.appWidgetView.addView(R.id.episodes, item);
            return;
        }

        for (int i = this.firstItemPosition(); i <= this.lastItemPosition(); i++) {
            RemoteViews item = Item.from(this.getApplicationContext()).createFor(this.episodes.get(i));
            this.appWidgetView.addView(R.id.episodes, item);
        }
    }

    private int firstItemPosition() {
        return this.itemPageBrowser.firstItemOfCurrentPage();
    }

    private int lastItemPosition() {
        return this.itemPageBrowser.lastItemOfCurrentPage();
    }

    private int currentPage() {
        return this.preferences.currentPage();
    }

    private void saveCurrentPage() {
        this.preferences.setCurrentPage(this.itemPageBrowser.currentPage());
    }
}