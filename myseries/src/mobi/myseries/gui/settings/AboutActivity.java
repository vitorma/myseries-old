package mobi.myseries.gui.settings;

import mobi.myseries.R;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);

        getActionBar().setTitle(R.string.settings_about);
        getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        try {
            TextView appVersion = (TextView) findViewById(R.id.appVersion);
            appVersion.setText(getString(
                    R.string.settings_about_version_format,
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
        } catch (NameNotFoundException e) {
            /* Do nothing */
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
