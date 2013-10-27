package mobi.myseries.gui.myschedule;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.CheckableFrameLayout;
import mobi.myseries.gui.shared.CheckableFrameLayout.OnCheckedListener;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.RelativeDay;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

public class ScheduleListAdapter extends BaseAdapter {
    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED = 1;
    private static final int STATE_REGULAR = 2;

    private ScheduleMode mItems;
    private int[] mViewStates;

    private DisplayImageOptions mDisplayImageOptions;

    public ScheduleListAdapter(ScheduleMode items) {
        updateData(items);
        mDisplayImageOptions = imageLoaderOptions(); 
    }

    public void updateData(ScheduleMode items) {
        mItems = items;
        resetViewStates();
    }

    public void resetViewStates() {
        mViewStates = new int[mItems.numberOfEpisodes()];
    }

    /* BaseAdapter */

    @Override
    public int getCount() {
        return mItems.numberOfEpisodes();
    }

    @Override
    public Object getItem(int position) {
        return mItems.episodeAt(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = View.inflate(App.context(), R.layout.myschedule_item_list, null);
            viewHolder = new ViewHolder(view);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Episode episode = mItems.episodeAt(position);
        Series series = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

        setUpViewSection(position, viewHolder, episode);
        setUpViewBody(viewHolder, series, episode);

        return view;
    }

    /* Auxiliary */

    private void setUpViewSection(int position, ViewHolder viewHolder, Episode episode) {
        updateViewStates(position);

        if (isViewSectioned(position)) {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(App.context());
            String unavailable = LocalText.get(R.string.unavailable_date);
            String formattedDate = DatesAndTimes.toString(episode.airDate(), dateFormat, unavailable);
            viewHolder.mDate.setText(formattedDate);

            String formattedWeekDay = DatesAndTimes.toString(
                    episode.airDate(), DateFormats.forShortWeekDay(Locale.getDefault()), "");
            viewHolder.mWeekDay.setText(formattedWeekDay.toUpperCase(Locale.getDefault()));

            RelativeDay relativeDay = DatesAndTimes.parse(episode.airDate(), null);
            viewHolder.mRelativeDay.setText(LocalText.of(relativeDay, ""));

            viewHolder.mSection.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mSection.setVisibility(View.GONE);
        }
    }

    private void setUpViewBody(ViewHolder viewHolder, Series series, Episode episode) {
        ImageLoader.getInstance().displayImage(App.imageService().getPosterOf(series), viewHolder.mPoster, mDisplayImageOptions);

        viewHolder.mSeriesName.setText(series.name());

        viewHolder.mEpisodeNumber.setText(App.resources().getString(
                R.string.episode_number_format, episode.seasonNumber(), episode.number()));
        viewHolder.mEpisodeName.setText(episode.title());

        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(App.context());

        //TODO(Reul): could be episode.airDay, but it looks incorrect and there are time inconsistencies between episodes of the same show
        String airtime = DatesAndTimes.toString(episode.airTime(), airtimeFormat, "");
        viewHolder.mAirTime.setText(airtime);

        viewHolder.mNetwork.setText(series.network());

        viewHolder.mWatchMark.setChecked(episode.watched());
        viewHolder.mWatchMark.setOnClickListener(viewHolder.seenMarkCheckBoxListener(episode));
    }

    private void updateViewStates(int position) {
        if (mViewStates[position] == STATE_UNKNOWN) {
            mViewStates[position] = calculateViewState(position);
        }
    }

    private int calculateViewState(int position) {
        return shouldViewBeSectioned(position) ? STATE_SECTIONED : STATE_REGULAR;
    }

    private boolean shouldViewBeSectioned(int position) {
        if (position == 0) { return true; }

        Date current = DatesAndTimes.midnightDateFrom(mItems.episodeAt(position).airDate());
        Date previous = DatesAndTimes.midnightDateFrom(mItems.episodeAt(position - 1).airDate());

        return Objects.areDifferent(current, previous);
    }

    private boolean isViewSectioned(int position) {
        return mViewStates[position] == STATE_SECTIONED;
    }

    /* ViewHolder */

    private static class ViewHolder {
        private final View mSection;
        private final TextView mDate;
        private final TextView mWeekDay;
        private final TextView mRelativeDay;
        private final ImageView mPoster;
        private final SeenMark mWatchMark;
        private final CheckedTextView mSeriesName;
        private final CheckedTextView mEpisodeNumber;
        private final CheckedTextView mEpisodeName;
        private final CheckedTextView mAirTime;
        private final CheckedTextView mNetwork;
        private CheckableFrameLayout mCheckableBody;

        private ViewHolder(View view) {
            mSection = view.findViewById(R.id.section);
            mDate = (TextView) view.findViewById(R.id.date);
            mWeekDay = (TextView) view.findViewById(R.id.airDay);
            mRelativeDay = (TextView) view.findViewById(R.id.relativeDay);
            mPoster = (ImageView) view.findViewById(R.id.poster);
            mWatchMark = (SeenMark) view.findViewById(R.id.seenMark);
            mSeriesName = (CheckedTextView) view.findViewById(R.id.seriesName);
            mEpisodeNumber = (CheckedTextView) view.findViewById(R.id.episodeNumber);
            mEpisodeName = (CheckedTextView) view.findViewById(R.id.episodeTitle);
            mAirTime = (CheckedTextView) view.findViewById(R.id.airTime);
            mNetwork = (CheckedTextView) view.findViewById(R.id.network);

            if (App.resources().getBoolean(R.bool.isTablet)) {
                mCheckableBody = (CheckableFrameLayout) view.findViewById(R.id.checkableBody);
                mCheckableBody.changeBackgroundWhenChecked(true);

                ((CheckableFrameLayout) view).setOnCheckedListener(checkableFrameLayoutListener());
            }

            view.setTag(this);
        }

        private OnClickListener seenMarkCheckBoxListener(final Episode episode) {
            return new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mWatchMark.isChecked()) {
                        App.markingService().markAsWatched(episode);
                    } else {
                        App.markingService().markAsUnwatched(episode);
                    }
                }
            };
        }

        private OnCheckedListener checkableFrameLayoutListener() {
            return new OnCheckedListener() {
                @Override
                public void onChecked(boolean checked) {
                    mCheckableBody.setChecked(checked);
                }
            };
        }
    }

    private DisplayImageOptions imageLoaderOptions() {
        return new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisc(true)
        .resetViewBeforeLoading(true)
        .showImageOnFail(R.drawable.generic_poster)
        .build();
    }
}