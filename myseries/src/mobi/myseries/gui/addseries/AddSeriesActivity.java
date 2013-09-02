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
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class AddSeriesActivity extends TabActivity {
    private static final int TRENDING_TAB = 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, AddSeriesActivity.class);
    }

    @Override
    protected void init(Bundle savedInstanceState) { /* There's nothing to initialize */ }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.add_series);
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected Intent navigateUpIntent() {
        return NavUtils.getParentActivityIntent(this);
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
}
