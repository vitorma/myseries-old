package mobi.myseries.gui.activity.base;

import mobi.myseries.R;
import android.app.ActionBar;
import android.os.Bundle;

public abstract class TabActivity extends BaseActivity implements TabAdapter.Listener {
    private static final String SELECTED_TAB = "selectedTab";

    private int selectedTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            this.selectedTab = this.defaultSelectedTab();
        } else {
            this.selectedTab = savedInstanceState.getInt(SELECTED_TAB);
        }

        this.setUpActionBar();
    }

    protected int selectedTab() {
        return this.selectedTab;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SELECTED_TAB, this.selectedTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSelected(int position) {
        this.selectedTab = position;
    }

    @Override
    protected int layoutResource() {
        return R.layout.activity_base_tabbed;
    }

    protected abstract TabDefinition[] tabDefinitions();
    protected abstract int defaultSelectedTab();

    private void setUpActionBar() {
        this.getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        new TabAdapter(this, this.tabDefinitions(), this.selectedTab).register(this);
    }
}
