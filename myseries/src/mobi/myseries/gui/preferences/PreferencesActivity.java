package mobi.myseries.gui.preferences;

import mobi.myseries.R;
import android.app.Activity;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

public class PreferencesActivity extends SherlockActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.preferences);
        this.setResult(Activity.RESULT_CANCELED);
    }
}
