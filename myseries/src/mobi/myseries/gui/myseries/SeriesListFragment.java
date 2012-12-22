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
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.series.SeriesActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.actionbarsherlock.app.SherlockListFragment;

public class SeriesListFragment extends SherlockListFragment {
    private SeriesListAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();
        this.setUpItemClickListener();
        this.setUpListAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();

        this.adapter.register(this.adapterListener);

        if (this.adapter.isLoading()) {
            this.adapterListener.onStartLoading();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.adapter.deregister(this.adapterListener);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_followed_series));
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Series series = (Series) parent.getItemAtPosition(position);

                Intent intent = SeriesActivity.newIntent(App.context(), series.id());

                SeriesListFragment.this.startActivity(intent);
            }
        });
    }

    private void setUpListAdapter() {
        this.adapter = new SeriesListAdapter();

        this.setListAdapter(this.adapter);
    }

    /* SeriesListAdapter.Listener */

    SeriesListAdapter.Listener adapterListener = new SeriesListAdapter.Listener() {
        @Override
        public void onStartLoading() {
            SeriesListFragment.this.setListShown(false);
        }

        @Override
        public void onFinishLoading() {
            SeriesListFragment.this.setListShown(true);
        }
    };
}
