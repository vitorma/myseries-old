package mobi.myseries.gui.shared;

import android.view.MenuItem;

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
