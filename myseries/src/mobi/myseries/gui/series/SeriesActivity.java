package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.Extra;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;

public class SeriesActivity extends TabActivity {
    private static final int TAB_SEASONS = 1;

    private static final String OVERVIEW_FRAGMENT = OverviewFragment.class.getName();
    private static final String SEASONS_FRAGMENT = SeasonsFragment.class.getName();

    private final boolean isTablet = App.resources().getBoolean(R.bool.isTablet);
    private int seriesId;

    public static Intent newIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra(Extra.SERIES_ID, seriesId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this.isTablet) {
            this.setUpFragments(savedInstanceState);
        }
    }

    private void setUpFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            FragmentTransaction ft = this.getFragmentManager().beginTransaction();

            ft.add(R.id.overview_container, OverviewFragment.newInstance(this.seriesId), OVERVIEW_FRAGMENT);
            ft.add(R.id.seasons_container, SeasonsFragment.newInstance(this.seriesId), SEASONS_FRAGMENT);

            ft.commit();
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        this.seriesId = this.getIntent().getExtras().getInt(Extra.SERIES_ID);
    }

    @Override
    protected CharSequence title() {
        return App.seriesProvider().getSeries(this.seriesId).name();
    }

    @Override
    protected int layoutResource() {
        if (this.isTablet) {
            return R.layout.series_activity;
        }

        return super.layoutResource();
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected Intent navigateUpIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        if (this.isTablet) {
            return null;
        }

        return new TabDefinition[] {
            new TabDefinition(R.string.tab_overview, OverviewFragment.newInstance(this.seriesId)),
            new TabDefinition(R.string.tab_seasons, SeasonsFragment.newInstance(this.seriesId))
        };
    }

    @Override
    protected int defaultSelectedTab() {
        if (this.isTablet) {
            return TAB_NONE;
        }

        return TAB_SEASONS;
    }
}
