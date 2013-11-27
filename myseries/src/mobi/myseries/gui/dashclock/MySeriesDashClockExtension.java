package mobi.myseries.gui.dashclock;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.application.schedule.ScheduleMode;
import mobi.myseries.application.schedule.ScheduleSpecification;
import mobi.myseries.application.schedule.Unaired;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.schedule.dualpane.ScheduleDualPaneActivity;
import mobi.myseries.gui.schedule.singlepane.ScheduleSinglePaneActivity;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.RelativeDay;
import android.content.Intent;
import android.text.format.DateFormat;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

public class MySeriesDashClockExtension extends DashClockExtension {

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);

        setUpdateWhenScreenOn(true);
    }

    @Override
    protected void onUpdateData(int reason) {
        ScheduleSpecification specification = App.preferences().forSchedule().fullSpecification();
        Unaired unairedEpisodes = App.schedule().unaired(specification);

        if (unairedEpisodes.numberOfEpisodes() == 0) {
            publishUpdate(null);
            return;
        }

        List<Episode> nextUnairedEpisodes = unairedEpisodes.nextEpisodes();
        Date airDate = nextUnairedEpisodes.get(0).airDate();

        publishUpdate(new ExtensionData()
            .visible(true)
            .icon(R.drawable.ic_notification)
            .status(status(airDate))
            .expandedTitle(expandedTitle(airDate))
            .expandedBody(expandedBody(nextUnairedEpisodes))
            .clickIntent(clickIntent()));
    }

    private String status(Date airDate) {
        return day(airDate).toUpperCase() + "\n" + time(airDate);
    }

    private String expandedTitle(Date airDate) {
        return day(airDate) + " " + time(airDate);
    }

    private String expandedBody(List<Episode> episodes) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        for (Episode e : episodes) {
            Series s = App.seriesFollowingService().getFollowedSeries(e.seriesId());

            builder
                .append(App.resources().getString(R.string.episode_number_format, e.seasonNumber(), e.number()))
                .append(" ")
                .append(s.name())
                .append(", ")
                .append(s.network())
                .append(i < episodes.size() - 1 ? "\n" : "");

            i++;
        }

        return builder.toString();
    }

    private Intent clickIntent() {
        Intent intent = App.resources().getBoolean(R.bool.isTablet) ?
                ScheduleDualPaneActivity.newIntent(App.context(), ScheduleMode.UNAIRED) :
                ScheduleSinglePaneActivity.newIntent(App.context(), ScheduleMode.UNAIRED);

        return intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private String day(Date airDate) {
        RelativeDay relativeDay = RelativeDay.valueOf(airDate);

        if (relativeDay.isToday() || relativeDay.isTomorrow()) {
            return LocalText.of(relativeDay, "");
        }

        String weekDay = DatesAndTimes.toString(airDate, DateFormats.forShortWeekDay(Locale.getDefault()), "");

        return relativeDay.isInLessThanAWeek() ?
                weekDay :
                weekDay + ", " + DatesAndTimes.toString(airDate, DateFormat.getDateFormat(App.context()), "");
    }

    private String time(Date airDate) {
        return DatesAndTimes.toString(
                airDate,
                android.text.format.DateFormat.getTimeFormat(App.context()),
                "");
    }
}
