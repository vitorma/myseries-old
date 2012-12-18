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
import mobi.myseries.application.backup.BackupListener;
import mobi.myseries.application.backup.BackupService;
import android.os.Bundle;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class SeriesListFragment extends SherlockListFragment implements BackupListener {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();
    private static final BackupService BACKUP_SERVICE = App.backupService();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpPadding();
        this.setUpSelector();
        this.setUpEmptyText();
        this.setUpListAdapter();
        BACKUP_SERVICE.register(this);
    }

    private void setUpPadding() {
        int padding = this.getActivity().getResources().getDimensionPixelSize(R.dimen.gap_large);
        this.getListView().setPadding(padding, 0, padding, 0);
    }

    private void setUpSelector() {
        this.getListView().setSelector(R.color.transparent);
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_followed_series));
    }

    private void setUpListAdapter() {
        ListAdapter adapter = new SeriesListAdapter(this.getActivity(), SERIES_PROVIDER.followedSeries());
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
