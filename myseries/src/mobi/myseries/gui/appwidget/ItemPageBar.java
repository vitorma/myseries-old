/*
 *   ItemPageBar.java
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

import mobi.myseries.R;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

public class ItemPageBar {
    private static final int FIRST = R.id.firstButton;
    private static final int PREVIOUS = R.id.previousButton;
    private static final int NEXT = R.id.nextButton;
    private static final int LAST = R.id.lastButton;
    private static final int PAGE = R.id.pageTextView;

    private Context context;
    private RemoteViews appWidgetView;

    private ItemPageBar(Context context, RemoteViews appWidgetView) {
        this.context = context;
        this.appWidgetView = appWidgetView;
    }

    public static ItemPageBar from(Context context, RemoteViews appWidgetView) {
        return new ItemPageBar(context, appWidgetView);
    }

    public ItemPageBar setupFor(int appWidgetId) {
        this.appWidgetView.setOnClickPendingIntent(FIRST, intentForScrollToFirst(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(PREVIOUS, intentForScrollToPrevious(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(NEXT, intentForScrollToNext(appWidgetId));
        this.appWidgetView.setOnClickPendingIntent(LAST, intentForScrollToLast(appWidgetId));

        return this;
    }

    public ItemPageBar showNavigationAccordingTo(ItemPageBrowser itemPageBrowser) {
        if (itemPageBrowser.isCurrentlyAtFirstPage()) {
            this.hideNavigationBackward();
        } else {
            this.showNavigationBackward();
        }

        if (itemPageBrowser.isCurrentlyAtLastPage()) {
            this.hideNavigationForward();
        } else {
            this.showNavigationForward();
        }

        this.setPageInfo(itemPageBrowser.currentPage(), itemPageBrowser.numberOfPages());

        return this;
    }

    public ItemPageBar hideNavigationBackward() {
        this.hide(FIRST);
        this.hide(PREVIOUS);

        return this;
    }

    public ItemPageBar hideNavigationForward() {
        this.hide(NEXT);
        this.hide(LAST);

        return this;
    }

    public ItemPageBar showNavigationBackward() {
        this.show(FIRST);
        this.show(PREVIOUS);

        return this;
    }

    public ItemPageBar showNavigationForward() {
        this.show(NEXT);
        this.show(LAST);

        return this;
    }

    public void setPageInfo(int page, int numberOfPages) {
        String format = this.context.getString(R.string.page_info_format);
        this.appWidgetView.setTextViewText(PAGE, String.format(format, page, numberOfPages));
    }

    private PendingIntent intentForScrollToFirst(int appWidgetId) {
        return pendingIntentServiceFor(appWidgetId, Action.GO_TO_FIRST);
    }

    private PendingIntent intentForScrollToPrevious(int appWidgetId) {
        return pendingIntentServiceFor(appWidgetId, Action.GO_TO_PREVIOUS);
    }

    private PendingIntent intentForScrollToNext(int appWidgetId) {
        return pendingIntentServiceFor(appWidgetId, Action.GO_TO_NEXT);
    }

    private PendingIntent intentForScrollToLast(int appWidgetId) {
        return pendingIntentServiceFor(appWidgetId, Action.GO_TO_LAST);
    }

    private PendingIntent pendingIntentServiceFor(int appWidgetId, String action) {
        Intent intent = intentServiceFor(appWidgetId, action);

        return PendingIntent.getService(this.context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent intentServiceFor(int appWidgetId, String action) {
        return SetupService.newIntent(this.context, appWidgetId, action);
    }

    private void hide(int view) {
        this.appWidgetView.setViewVisibility(view, View.INVISIBLE);
    }

    private void show(int view) {
        this.appWidgetView.setViewVisibility(view, View.VISIBLE);
    }
}
