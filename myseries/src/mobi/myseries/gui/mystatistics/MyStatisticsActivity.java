package mobi.myseries.gui.mystatistics;

import mobi.myseries.R;
import mobi.myseries.gui.activity.base.BaseActivity;
import android.content.Context;
import android.content.Intent;

public class MyStatisticsActivity extends BaseActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, MyStatisticsActivity.class);
    }

    @Override
    protected void init() { }

    @Override
    protected boolean isTopLevel() {
        return true;
    }

    @Override
    protected int layoutResource() {
        return R.layout.mystatistics;
    }

    @Override
    protected CharSequence title() {
        return this.getString(R.string.my_statistics);
    }

    @Override
    protected CharSequence titleForSideMenu() {
        return this.getString(R.string.nav_statistics);
    }
}
