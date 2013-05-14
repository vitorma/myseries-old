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
import mobi.myseries.gui.shared.BaseActivity;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.TabPagerAdapter;
import net.simonvt.menudrawer.MenuDrawer;
import android.app.Dialog;
import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Window;

public class AddSeriesActivity extends BaseActivity {
    private static final int DEFAULT_SELECTED_TAB = 0;

    private State state;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.addseries);

        this.setUpState();
        this.setUpActionBar();

        this.getMenu().setTouchMode(MenuDrawer.TOUCH_MODE_BEZEL);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return this.state;
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.state.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        this.state.onStop();
    }

    private void setUpState() {
        Object retainedState = this.getLastCustomNonConfigurationInstance();

        if (retainedState != null) {
            this.state = (State) retainedState;
        } else {
            this.state = new State();
            this.state.messageLauncher = new MessageLauncher(this);
            this.state.selectedTab = DEFAULT_SELECTED_TAB;
        }
    }

    private void setUpActionBar() {
        ActionBar ab = this.getSupportActionBar();

        this.setSupportProgressBarIndeterminateVisibility(false);

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle(R.string.add_series);
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        new TabPagerAdapter(this)
            .addTab(R.string.trending, new TrendingFragment())
            .addTab(R.string.search, new SearchSeriesFragment())
            .register(this.state);

        ab.setSelectedNavigationItem(this.state.selectedTab);
    }

    void onSearchStart() {
        this.setSupportProgressBarIndeterminateVisibility(true);
    }

    void onSearchFinish() {
        this.setSupportProgressBarIndeterminateVisibility(false);
    }

    void onSearchFailure(int searchFailureTitleResourceId, int searchFailureMessageResourceId) {
        this.state.dialog = new FailureDialogBuilder(this)
            .setTitle(searchFailureTitleResourceId)
            .setMessage(searchFailureMessageResourceId)
            .build();

        this.state.dialog.show();
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

        dialog.show();

        this.state.dialog = dialog;
    }

    private static class State implements TabPagerAdapter.Listener {
        private int selectedTab;
        private Dialog dialog;
        private boolean isShowingDialog;
        private MessageLauncher messageLauncher;

        @Override
        public void onSelected(int position) {
            this.selectedTab = position;
        }

        private void onStart() {
            if (this.isShowingDialog) {
                this.dialog.show();
            }

            this.messageLauncher.loadState();
        }

        private void onStop() {
            if (this.dialog != null && this.dialog.isShowing()) {
                this.isShowingDialog = true;
                this.dialog.dismiss();
            } else {
                this.isShowingDialog = false;
            }

            this.messageLauncher.onStop();
        }
    }
}
