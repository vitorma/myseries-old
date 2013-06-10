package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.gui.activity.base.TabActivity;
import mobi.myseries.gui.activity.base.TabDefinition;
import mobi.myseries.gui.shared.Extra;
import android.content.Context;
import android.content.Intent;

public class SeriesActivity extends TabActivity {
    private static final int TAB_SEASONS = 1;

    private int seriesId;

    public static Intent newIntent(Context context, int seriesId) {
        Intent intent = new Intent(context, SeriesActivity.class);
        intent.putExtra(Extra.SERIES_ID, seriesId);
        return intent;
    }

    @Override
    protected void init() {
        this.seriesId = this.getIntent().getExtras().getInt(Extra.SERIES_ID);
    }

    @Override
    protected CharSequence title() {
        return App.seriesProvider().getSeries(this.seriesId).name();
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }

    @Override
    protected TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
            new TabDefinition(R.string.details, DetailsFragment.newInstance(this.seriesId)),
            new TabDefinition(R.string.seasons, SeasonsFragment.newInstance(this.seriesId))
        };
    }

    @Override
    protected int defaultSelectedTab() {
        return TAB_SEASONS;
    }
}
