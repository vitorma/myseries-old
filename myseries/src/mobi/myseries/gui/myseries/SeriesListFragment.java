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
import mobi.myseries.application.backup.BackupListener;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class SeriesListFragment extends SherlockListFragment implements BackupListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();
        this.setUpListAdapter();

        App.backupService().register(this);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_followed_series));
    }

    private void setUpListAdapter() {
        ListAdapter adapter = new SeriesListAdapter(this.getActivity(), App.seriesProvider().followedSeries());
        this.setListAdapter(adapter);
    }

    @Override
    public void onBackupSucess() {}

    @Override
    public void onBackupFailure(Exception e) {}

    @Override
    public void onRestoreSucess() {
        this.setUpListAdapter();
    }

    @Override
    public void onRestoreFailure(Exception e) {}
}
