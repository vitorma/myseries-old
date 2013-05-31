package mobi.myseries.gui.backup;

import mobi.myseries.R;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.gui.shared.BaseActivity;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import mobi.myseries.gui.shared.MessageLauncher;
import mobi.myseries.gui.shared.TabPagerAdapter;
import android.app.ActionBar;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;

public class BackupActivityV2 extends BaseActivity {
    private static final int DEFAULT_SELECTED_TAB = 0;

    private State state;

    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        super.onCreate(savedInstanceState);
        this.setTitle(R.string.backup_restore);

        this.setUpState();
        this.setUpActionBar();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
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
        Object retainedState = this.getLastNonConfigurationInstance();

        if (retainedState != null) {
            this.state = (State) retainedState;
        } else {
            this.state = new State();
            this.state.messageLauncher = new MessageLauncher(this);
            this.state.selectedTab = DEFAULT_SELECTED_TAB;
        }
    }

    private void setUpActionBar() {
        ActionBar ab = this.getActionBar();

        this.setProgressBarIndeterminateVisibility(false);

        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        new TabPagerAdapter(this)
            .addTab(R.string.backup_button, new BackupFragment())
            .addTab(R.string.restore_button, new RestoreFragment())
            .register(this.state);

        ab.setSelectedNavigationItem(this.state.selectedTab);
    }

    void onSearchFailure(int searchFailureTitleResourceId, int searchFailureMessageResourceId) {
        this.state.dialog = new FailureDialogBuilder(this)
            .setTitle(searchFailureTitleResourceId)
            .setMessage(searchFailureMessageResourceId)
            .build();

        this.state.dialog.show();
    }

    void onRequestOperation(BackupMode backupMode) {
        Dialog dialog;

            dialog = new ConfirmationDialogBuilder(this)
                .setTitle("")
                .setMessage("")
                .setPositiveButton(R.string.ok, new ConfirmationButtonOnClickListener())
                .setNegativeButton(R.string.cancel, null)
                .build();

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

    @Override
    protected int layoutResource() {
        return R.layout.addseries;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }
}
