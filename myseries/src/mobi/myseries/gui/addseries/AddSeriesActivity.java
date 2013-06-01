/*
 *   AddSeriesActivity.java
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

package mobi.myseries.gui.addseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import android.app.Dialog;

public class AddSeriesActivity extends TabActivity {
    private static final int TRENDING_TAB = 0;

    @Override
    protected void init() { /* There's nothing to initialize */ }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.add_series);
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.trending, new TrendingFragment()),
            new TabDefinition(R.string.search, new SearchFragment())
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return TRENDING_TAB;
    }

    void onSearchFailure(int searchFailureTitleResourceId, int searchFailureMessageResourceId) {
        this.showDialog(
            new FailureDialogBuilder(this)
                .setTitle(searchFailureTitleResourceId)
                .setMessage(searchFailureMessageResourceId)
                .build()
        );
    }

    void onRequestAdd(Series seriesToAdd) {
        Dialog dialog;

        if (App.followSeriesService().follows(seriesToAdd)) {
            String messageFormat = this.getString(R.string.add_already_followed_series_message);

            dialog = new FailureDialogBuilder(this)
                .setMessage(String.format(messageFormat, seriesToAdd.name()))
                .build();
        } else {
            dialog = new ConfirmationDialogBuilder(this)
                .setTitle(seriesToAdd.name())
                .setMessage(seriesToAdd.overview())
                .setSurrogateMessage(R.string.overview_unavailable)
                .setPositiveButton(R.string.add, new AddButtonOnClickListener(seriesToAdd))
                .setNegativeButton(R.string.dont_add, null)
                .build();
        }

        this.showDialog(dialog);
    }
}
