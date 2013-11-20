package mobi.myseries.gui.features;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import mobi.myseries.R;
import mobi.myseries.gui.activity.base.BaseActivity;

public class FeaturesActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, FeaturesActivity.class);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected CharSequence title() {
        return this.getString(R.string.ab_title_features);
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
