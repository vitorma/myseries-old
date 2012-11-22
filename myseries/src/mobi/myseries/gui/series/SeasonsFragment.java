package mobi.myseries.gui.series;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.episodes.EpisodesActivity;
import mobi.myseries.gui.shared.Extra;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;

public class SeasonsFragment extends SherlockFragment {
    private static final SeriesProvider SERIES_PROVIDER = App.seriesProvider();

    private int seriesId;
    private ExpandableListView list;
    private SeasonsExpandableAdapter adapter;

    public static SeasonsFragment newInstance(int seriesId) {
        SeasonsFragment seasonsFragment = new SeasonsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        seasonsFragment.setArguments(arguments);

        return seasonsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.seriesId = this.getArguments().getInt(Extra.SERIES_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Extra.SERIES_ID, this.seriesId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.series_seasons, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        this.adapter = new SeasonsExpandableAdapter(this.getActivity(), series);

        this.list = (ExpandableListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);

        this.list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SeasonsFragment.this.showDetailsOf(groupPosition, childPosition);
                return true;
            }
        });

        this.list.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int numberOfGroups = SeasonsFragment.this.adapter.getGroupCount();

                for (int i = 0; i < numberOfGroups; i++) {
                    if (i != groupPosition) {
                        SeasonsFragment.this.list.collapseGroup(i);
                    }
                }
            }
        });
    }

    private void showDetailsOf(int groupPosition, int childPosition) {
        int seasonNumber = this.adapter.season(groupPosition).number();
        int episodeNumber = this.adapter.episode(groupPosition, childPosition).number();

        Intent intent = EpisodesActivity.newIntent(this.getActivity(), this.seriesId, seasonNumber, episodeNumber);
        this.startActivity(intent);
    }
}
