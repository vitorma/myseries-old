package mobi.myseries.gui.backup;

import mobi.myseries.R;
import mobi.myseries.application.backup.BackupMode;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.ConfirmationDialogBuilder;
import mobi.myseries.gui.shared.FailureDialogBuilder;
import mobi.myseries.gui.shared.MessageLauncher;
import android.app.Dialog;

public class BackupActivityV2 extends TabActivity {
    private static final int DEFAULT_SELECTED_TAB = 0;

    private State state;

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

    private static class State {
        private Dialog dialog;
        private boolean isShowingDialog;
        private MessageLauncher messageLauncher;

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
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected void init() {
        Object retainedState = this.getLastNonConfigurationInstance();

        if (retainedState != null) {
            this.state = (State) retainedState;
        } else {
            this.state = new State();
            this.state.messageLauncher = new MessageLauncher(this);
        }
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.backup_button, new BackupFragment()),
            new TabDefinition(R.string.restore_button, new RestoreFragment())
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return DEFAULT_SELECTED_TAB;
    }

    @Override
    protected CharSequence title() {
        return this.getText(R.string.backup_restore);
    }
}
