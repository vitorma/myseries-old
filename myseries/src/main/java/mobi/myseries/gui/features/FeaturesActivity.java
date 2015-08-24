package mobi.myseries.gui.features;

import mobi.myseries.R;
import mobi.myseries.gui.activity.base.BaseActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class FeaturesActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, FeaturesActivity.class);
    }

    @Override
    protected void init(Bundle savedInstanceState) { }

    @Override
    protected CharSequence title() {
        return getString(R.string.ab_title_features);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return getString(R.string.nav_features);
    }

    @Override
    protected int layoutResource() {
        return R.layout.features;
    }

    @Override
    protected boolean isTopLevel() {
        return true;
    }
}