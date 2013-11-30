package mobi.myseries.gui.settings;

import mobi.myseries.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutActivity extends Activity {
    private static final String COMMUNITY_URL = "https://plus.google.com/u/0/communities/117519773750876586624";
    private static final String USERVOICE_URL = "http://myseriesapp.uservoice.com/";

    private boolean mAreCreditDetailsVisible;

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
        } catch (NameNotFoundException e) { /* Do nothing */ }

        TextView appCommunity = (TextView) findViewById(R.id.appCommunity);
        appCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(COMMUNITY_URL)));
            }
        });

        TextView appFeatureSuggestions = (TextView) findViewById(R.id.appFeatureSuggestions);
        appFeatureSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(USERVOICE_URL)));
            }
        });

        final LinearLayout creditsView = (LinearLayout) findViewById(R.id.appCredits);

        creditsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAreCreditDetailsVisible = !mAreCreditDetailsVisible;
                showOrHideCreditDetails(creditsView);
            }
        });

        showOrHideCreditDetails(creditsView);
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

    private void showOrHideCreditDetails(LinearLayout creditDetailsView) {
        for (int i = 1; i < creditDetailsView.getChildCount(); i++) {
            creditDetailsView.getChildAt(i).setVisibility(mAreCreditDetailsVisible ? View.VISIBLE : View.GONE);
        }
    }
}
