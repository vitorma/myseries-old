/*
 *   SeriesListFragment.java
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

package mobi.myseries.gui.myseries;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class SeriesListFragment extends SherlockListFragment {
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpSelector();
        this.setUpEmptyTextView();
        this.setUpListAdapter();
    }

    private void setUpEmptyTextView() {
        this.setEmptyText(this.getString(R.string.no_followed_series));
    }

    private void setUpSelector() {
        this.getListView().setSelector(R.color.transparent);
    }

    private void setUpListAdapter() {
        ListAdapter adapter = new SeriesListAdapter(this.getActivity(), SERIES_PROVIDER.followedSeries());
        this.setListAdapter(adapter);
    }
}
