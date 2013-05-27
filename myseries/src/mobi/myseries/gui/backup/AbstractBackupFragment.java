package mobi.myseries.gui.backup;

import java.util.List;

import mobi.myseries.R;
import mobi.myseries.application.backup.BackupListener;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class AbstractBackupFragment extends Fragment {
    private View buttonPanel;
    private ProgressBar progressIndicator;

    protected boolean isRuning;
    protected BackupListener backupListener;

    /* Fragment life cycle */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.backup_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.prepareViews();
        this.backupListener = this.backupListener();
    }

    protected void prepareViews() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStart() {
        super.onStart();

        this.registerListenerForBackup();
        if (this.isRuning) {
            this.backupListener.onStart();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        this.deregisterListenerForForBackup();
    }

    /* Abstract methods */

    protected abstract void registerListenerForBackup();
    protected abstract void deregisterListenerForForBackup();
    protected abstract BackupListener backupListener();

    /* Prepare views */

    private void prepareButtonPanel() {
        this.buttonPanel = this.findView(R.id.buttonPanel);

    }



    private void prepareProgressIndicator() {
        this.progressIndicator = (ProgressBar) this.findView(R.id.progressIndicator);
    }

    private View findView(int resourceId) {
        return this.getView().findViewById(resourceId);
    }

    /* Update views */

    protected void hideButtons() {
        this.buttonPanel.setVisibility(View.INVISIBLE);
    }

    protected void showButtons() {
        this.buttonPanel.setVisibility(View.VISIBLE);
    }


    protected void showProgress() {
        this.progressIndicator.setVisibility(View.VISIBLE);
    }

    protected void hideProgress() {
        this.progressIndicator.setVisibility(View.INVISIBLE);
    }

    /* Activity */

    protected BackupActivityV2 activity() {
        return (BackupActivityV2) this.getActivity();
    }
}
