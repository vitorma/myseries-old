package mobi.myseries.gui.season;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.activity.base.BaseActivity;
import mobi.myseries.gui.shared.Extra;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SeasonActivity extends BaseActivity {

    public static Intent newIntent(Context context, int seriesId, int seasonNumber) {
        Intent intent = new Intent(context, SeasonActivity.class);

        intent.putExtra(Extra.SERIES_ID, seriesId);
        intent.putExtra(Extra.SEASON_NUMBER, seasonNumber);

        return intent;
    }

    private int seriesId;
    private int seasonNumber;
    private String title;

    @Override
    protected void init() {
        Bundle extras = this.getIntent().getExtras();

        this.seriesId = extras.getInt(Extra.SERIES_ID);
        this.seasonNumber = extras.getInt(Extra.SEASON_NUMBER);

        Series series = App.seriesProvider().getSeries(this.seriesId);

        if (series == null) {
            this.finish();
            return;
        }

        this.title = series.name();

        SeasonFragment fragment = SeasonFragment.newInstance(this.seriesId, this.seasonNumber);
        FragmentTransaction ft = this.getFragmentManager().beginTransaction();
        ft.add(R.id.container, fragment, "seasonFragment").commit();
    }

    @Override
    protected CharSequence title() {
        return this.title;
    }

    @Override
    protected int layoutResource() {
        return R.layout.season;
    }

    @Override
    protected boolean isTopLevel() {
        return false;
    }
}
