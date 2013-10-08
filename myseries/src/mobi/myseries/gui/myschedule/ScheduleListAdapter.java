package mobi.myseries.gui.myschedule;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.AsyncImageLoader;
import mobi.myseries.gui.shared.CheckableFrameLayout;
import mobi.myseries.gui.shared.CheckableFrameLayout.OnCheckedListener;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Images;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.PosterFetchingMethod;
import mobi.myseries.gui.shared.SeenMark;
import mobi.myseries.gui.shared.SmallPosterFetchingMethod;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Objects;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Strings;
import mobi.myseries.shared.WeekDay;
import android.graphics.Bitmap;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ScheduleListAdapter extends BaseAdapter {
    private static final int STATE_UNKNOWN = 0;
    private static final int STATE_SECTIONED = 1;
    private static final int STATE_REGULAR = 2;

    private static final Bitmap GENERIC_POSTER = Images.genericSeriesPosterThumbnailFrom(App.resources());

    private ScheduleMode mItems;
    private int[] mViewStates;

    public ScheduleListAdapter(ScheduleMode items) {
        updateData(items);
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

            WeekDay weekDay = App.seriesFollowingService().getFollowedSeries(episode.seriesId()).airDay();
            String formattedWeekDay = DatesAndTimes.toString(weekDay, DateFormats.forShortWeekDay(Locale.getDefault()), "").toUpperCase();
            viewHolder.mWeekDay.setText(formattedWeekDay);

            RelativeDay relativeDay = DatesAndTimes.parse(episode.airDate(), null);
            viewHolder.mRelativeDay.setText(LocalText.of(relativeDay, ""));

            viewHolder.mSection.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mSection.setVisibility(View.GONE);
        }
    }

    private void setUpViewBody(ViewHolder viewHolder, Series series, Episode episode) {
        AsyncImageLoader.loadBitmapOn(
                new SmallPosterFetchingMethod(series, App.imageService()),
                GENERIC_POSTER,
                viewHolder.mPoster,
                viewHolder.mPosterLoadingProgress);

        viewHolder.mSeriesName.setText(series.name());

        String numberFormat = App.context().getString(R.string.episode_number_format);
        String episodeNumber = String.format(numberFormat, episode.seasonNumber(), episode.number());
        viewHolder.mEpisodeName.setText(episodeNumber + " " + episode.title());

        DateFormat airtimeFormat = android.text.format.DateFormat.getTimeFormat(App.context());
        String airtime = DatesAndTimes.toString(episode.airTime(), airtimeFormat, "");
        String network = series.network();
        viewHolder.mAirInfo.setText(Strings.concat(airtime, network, " - "));

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

        Date current = mItems.episodeAt(position).airDate();
        Date previous = mItems.episodeAt(position - 1).airDate();

        return Objects.areDifferent(current, previous);
    }

    private boolean isViewSectioned(int position) {
        return mViewStates[position] == STATE_SECTIONED;
    }

    /* ViewHolder */

    private static class ViewHolder {
        private View mSection;
        private TextView mDate;
        private TextView mWeekDay;
        private TextView mRelativeDay;
        private ImageView mPoster;
        private SeenMark mWatchMark;
        private CheckedTextView mSeriesName;
        private CheckedTextView mEpisodeName;
        private CheckedTextView mAirInfo;
        private ProgressBar mPosterLoadingProgress;
        private CheckableFrameLayout mCheckableBody;

        private ViewHolder(View view) {
            mSection = view.findViewById(R.id.section);
            mDate = (TextView) view.findViewById(R.id.date);
            mWeekDay = (TextView) view.findViewById(R.id.weekDay);
            mRelativeDay = (TextView) view.findViewById(R.id.relativeDay);
            mPoster = (ImageView) view.findViewById(R.id.poster);
            mWatchMark = (SeenMark) view.findViewById(R.id.seenMark);
            mSeriesName = (CheckedTextView) view.findViewById(R.id.seriesName);
            mEpisodeName = (CheckedTextView) view.findViewById(R.id.episodeName);
            mAirInfo = (CheckedTextView) view.findViewById(R.id.airInfo);
            mPosterLoadingProgress = (ProgressBar) view.findViewById(R.id.loadProgress);

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

                    mSeriesName.setChecked(checked);
                    mEpisodeName.setChecked(checked);
                    mAirInfo.setChecked(checked);

                    mWatchMark.setImageDrawable(
                            checked ?
                            App.resources().getDrawable(R.drawable.watchmark_dark) :
                            App.resources().getDrawable(R.drawable.watchmark_light));
                }
            };
        }
    }
}