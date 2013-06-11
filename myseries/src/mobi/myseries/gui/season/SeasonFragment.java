package mobi.myseries.gui.season;

import mobi.myseries.R;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.gui.episode.EpisodeActivity;
import mobi.myseries.gui.shared.Extra;
import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

public class SeasonFragment extends ListFragment {

    public static SeasonFragment newInstance(int seriesId, int seasonNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);

        SeasonFragment instance = new SeasonFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private int seriesId;
    private int seasonNumber;
    private SeasonAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);
        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
        this.seasonNumber = this.getArguments().getInt(Extra.SEASON_NUMBER);

        if (this.adapter == null) {
            this.adapter = new SeasonAdapter(this.seriesId, this.seasonNumber);
            this.setListAdapter(this.adapter);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();
        this.setUpItemClickListener();
    }

    private void setUpEmptyText() {
        this.setEmptyText(this.getString(R.string.no_episodes_to_see));
    }

    private void setUpItemClickListener() {
        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Episode e = (Episode) parent.getItemAtPosition(position);

                Intent intent = EpisodeActivity.newIntent(
                        view.getContext(), e.seriesId(), e.seasonNumber(), e.number());

                SeasonFragment.this.startActivity(intent);
            }
        });
    }
}
