package mobi.myseries.gui.myschedule;

import mobi.myseries.R;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.gui.shared.Extra;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScheduleMasterFragment extends Fragment implements ActionBarTabAdapter.OnTabSelectedListener {
    private static final String SELECTED_TAB = "selectedTab";
    private static final int TAB_NONE = -1;

    private ViewPager mViewPager;
    private ActionBarTabAdapter mTabAdapter;
    private int mSelectedTab = TAB_NONE;
    private OnSelectTabListener mListener;

    public static interface OnSelectTabListener {
        public void onSelectTab(int position);
    }

    public static ScheduleMasterFragment newInstance(int scheduleMode) {
        Bundle arguments = new Bundle();

        arguments.putInt(Extra.SCHEDULE_MODE, scheduleMode);

        ScheduleMasterFragment instance = new ScheduleMasterFragment();
        instance.setArguments(arguments);

        return instance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (OnSelectTabListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement EpisodePagerFragmentListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if (savedInstanceState == null) {
          mSelectedTab = getArguments().getInt(Extra.SCHEDULE_MODE);
      } else {
          mSelectedTab = savedInstanceState.getInt(SELECTED_TAB);
      }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.myschedule_master_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewPager = (ViewPager) this.getView().findViewById(R.id.viewPager);
        mTabAdapter = new ActionBarTabAdapter(
                getFragmentManager(),
                getActivity().getActionBar(),
                mViewPager,
                tabDefinitions(),
                mSelectedTab);
        mTabAdapter.register(this);
    }

    private TabDefinition[] tabDefinitions() {
        return new TabDefinition[] {
                new TabDefinition(R.string.schedule_to_watch, ScheduleFragment.newInstance(ScheduleMode.TO_WATCH)),
                new TabDefinition(R.string.schedule_aired, ScheduleFragment.newInstance(ScheduleMode.AIRED)),
                new TabDefinition(R.string.schedule_unaired, ScheduleFragment.newInstance(ScheduleMode.UNAIRED))
        };
    }

    /* ActionBarTabAdapter.Listener */

    @Override
    public void onTabSelected(int position) {
        mSelectedTab = position;

        mListener.onSelectTab(position);
    }
}
