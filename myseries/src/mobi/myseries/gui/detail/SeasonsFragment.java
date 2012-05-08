package mobi.myseries.gui.detail;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.SeriesProvider;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.actionbarsherlock.app.SherlockFragment;

public class SeasonsFragment extends SherlockFragment {
    private static final String CURRENT_GROUP = "currentGroup";
    private static final String CURRENT_CHILD = "currentChild";
    private static final String SERIES_ID = "seriesId";
    private static final int INVALID_CHILD = -1;
    private static final SeriesProvider SERIES_PROVIDER = App.environment().seriesProvider();

    private int seriesId;
    private ExpandableListView list;
    private SeasonsExpandableAdapter adapter;
    private boolean dualPane;
    private int currentGroup;
    private int currentChild;

    public static SeasonsFragment newInstance(int seriesId) {
        SeasonsFragment seasonsFragment = new SeasonsFragment();

        Bundle arguments = new Bundle();
        arguments.putInt(SERIES_ID, seriesId);
        seasonsFragment.setArguments(arguments);

        return seasonsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.currentGroup = this.getArguments().getInt(CURRENT_GROUP);
            this.currentChild = this.getArguments().getInt(CURRENT_CHILD);
        } else {
            this.currentChild = INVALID_CHILD;
        }

        this.seriesId = this.getArguments().getInt(SERIES_ID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(SERIES_ID, this.seriesId);
        outState.putInt(CURRENT_GROUP, this.currentGroup);
        outState.putInt(CURRENT_CHILD, this.currentChild);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        return inflater.inflate(R.layout.seasons_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FragmentTransaction ft = this.getFragmentManager().beginTransaction();

        Fragment f = this.getFragmentManager().findFragmentById(R.id.overview_details);

        if (f != null) {
            ft.remove(f);
        }

        ft.commit();

        Series series = SERIES_PROVIDER.getSeries(this.seriesId);

        this.adapter = new SeasonsExpandableAdapter(this.getActivity(), series);
        this.list = (ExpandableListView) this.getActivity().findViewById(R.id.seasons);
        this.list.setAdapter(this.adapter);
        this.list.setGroupIndicator(null);
        this.list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                SeasonsFragment.this.currentGroup = groupPosition;
                SeasonsFragment.this.currentChild = childPosition;
                SeasonsFragment.this.showDetails();
                return true;
            }
        });

        View detailsFrame = this.getActivity().findViewById(R.id.overview_details);
        this.dualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

        if (!this.dualPane) {
            return;
        }

        if (this.currentChild != INVALID_CHILD) {
            this.showDetails();
            return;
        }

        Episode nextToSee = series.nextEpisodeToSee(true);
        if (nextToSee != null) {
            this.currentGroup = this.adapter.groupPosition(nextToSee);
            this.currentChild = this.adapter.childPosition(nextToSee);
            this.showDetails();
            return;
        }

        Episode lastEpisode = series.lastEpisode();
        this.currentGroup = this.adapter.groupPosition(lastEpisode);
        this.currentChild = this.adapter.childPosition(lastEpisode);
        this.showDetails();
    }

    private void showDetails() {
        int seasonNumber = this.adapter.seasonNumber(this.currentGroup);
        int episodeNumber = this.adapter.episodeNumber(this.currentChild);

        if (!this.dualPane) {
            Intent intent = EpisodeDetailsActivity.newIntent(this.getActivity(), this.seriesId, seasonNumber, episodeNumber);
            this.startActivity(intent);
            return;
        }

        EpisodeFragment details = (EpisodeFragment) this.getFragmentManager().findFragmentById(R.id.overview_details);

        details = EpisodeFragment.newInstance(this.seriesId, seasonNumber, episodeNumber);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.overview_details, details);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }
}
