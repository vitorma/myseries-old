package mobi.myseries.gui.episodes;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Season;
import mobi.myseries.gui.shared.Extra;
import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

public class EpisodeListFragment extends ListFragment {

    public static interface OnSelectItemListener {
        public void onSelectItem(int position);
        public boolean shouldHighlightSelectedItem();
    }

    public static EpisodeListFragment newInstance(int seriesId, int seasonNumber, int episodeNumber) {
        Bundle arguments = new Bundle();
        arguments.putInt(Extra.SERIES_ID, seriesId);
        arguments.putInt(Extra.SEASON_NUMBER, seasonNumber);
        arguments.putInt(Extra.EPISODE_NUMBER, episodeNumber);

        EpisodeListFragment instance = new EpisodeListFragment();
        instance.setArguments(arguments);

        return instance;
    }

    private Season season;
    private int episodeNumber;
    private EpisodeListAdapter adapter;
    private OnSelectItemListener listener;
    private int mLastPosition = -1;
    private int mLastTop = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            this.listener = (OnSelectItemListener) activity;
        } catch (ClassCastException e) {
            e.printStackTrace();
            throw new ClassCastException(activity.toString() + " must implement EpisodeListFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRetainInstance(true);

        int seriesId = this.getArguments().getInt(Extra.SERIES_ID);
        int seasonNumber = this.getArguments().getInt(Extra.SEASON_NUMBER);

        this.season = App.seriesFollowingService().getFollowedSeries(seriesId).season(seasonNumber);
        this.episodeNumber = this.getArguments().getInt(Extra.EPISODE_NUMBER);

        this.adapter = new EpisodeListAdapter(this.season);
        this.setListAdapter(this.adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setUpEmptyText();

        if (this.listener.shouldHighlightSelectedItem()) {
            this.getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            this.checkItem(this.adapter.positionOf(this.season.episode(this.episodeNumber)));
        }

        boolean isDualPane = App.resources().getBoolean(R.bool.isTablet);

        if (isDualPane) {
            int paddingLeft = App.resources().getDimensionPixelSize(R.dimen.episode_list_side_gap);

            this.getListView().setPadding(paddingLeft, 0, 0, 0);
            this.getListView().setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
            this.getListView().setDivider(null);
            this.getView().setBackgroundResource(R.drawable.list_background_holo);
        } else {
            int backgroundColor = App.resources().getColor(R.color.bg_light);

            this.getView().setBackgroundColor(backgroundColor);

            this.getListView().setSelectionFromTop(mLastPosition, mLastTop);
        }
    }

    private void setUpEmptyText() {
        this.setEmptyText("");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        this.selectItem(position);
    }

    public void selectItem(int position) {
        this.episodeNumber = this.adapter.episodeAt(position).number();

        this.checkItem(position);
        this.listener.onSelectItem(position);
    }

    private void checkItem(int position) {
        this.getListView().setItemChecked(position, true);

        if (this.isNotVisible(position)) {
            this.getListView().smoothScrollToPosition(position);
        }
    }

    private boolean isNotVisible(int position) {
        return position <= this.getListView().getFirstVisiblePosition() ||
               position >= this.getListView().getLastVisiblePosition();
    }

    public void update(Season season, int episodeNumber) {
        this.season = season;
        this.episodeNumber = episodeNumber;

        this.adapter = new EpisodeListAdapter(this.season);
        this.setListAdapter(this.adapter);
        this.checkItem(this.adapter.positionOf(this.season.episode(this.episodeNumber)));
    }

    public void update(Season season) {
        this.update(season, this.episodeNumber);
    }

    public void setScrollPosition(int position, int top) {
        mLastPosition = position;
        mLastTop = top;
    }
}
