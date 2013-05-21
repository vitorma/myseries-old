package mobi.myseries.gui.shared;

import com.actionbarsherlock.view.MenuItem;

public abstract class TopActivity extends BaseActivity {

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.toggleMenu();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
