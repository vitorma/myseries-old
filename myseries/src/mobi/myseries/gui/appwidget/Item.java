package mobi.myseries.gui.appwidget;

import java.util.Locale;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
import mobi.myseries.gui.shared.DateFormats;
import mobi.myseries.gui.shared.Extra;
import mobi.myseries.gui.shared.LocalText;
import mobi.myseries.gui.shared.UniversalImageLoader;
import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.RelativeDay;
import mobi.myseries.shared.Strings;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.RemoteViews;

public class Item {
    private final Context context;

    private Item(Context context) {
        this.context = context;
    }

    public static Item from(Context context) {
        return new Item(context);
    }

    public RemoteViews loading() {
        return new RemoteViews(context.getPackageName(), R.layout.schedulewidget_loading_view);
    }

    public RemoteViews createFor(int scheduleMode, int position, Episode episode) {
        Series series = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.schedulewidget_item);

        setUpSeriesPoster(item, series);
        setUpEpisodeAirdate(item, episode);
        setUpAirtimeAndNetwork(item, series);
        setUpSeriesName(item, series);
        setUpEpisodeNumber(item, episode);
        setUpEpisodeTitle(item, episode);
        setUpOnClickFillInIntent(item, scheduleMode, position);

        return item;
    }

    private void setUpSeriesPoster(final RemoteViews item, Series series) {
        String seriesPosterPath = App.imageService().getPosterPath(series);

        if (seriesPosterPath == null) {
            setUpGenericSeriesPoster(item);
            return;
        }

        String seriesPosterUri = UniversalImageLoader.fileURI(seriesPosterPath);
        Bitmap loadedImage = UniversalImageLoader.loader().loadImageSync(seriesPosterUri);

        if (loadedImage != null) {
            item.setImageViewBitmap(R.id.seriesPoster, loadedImage);
        } else {
            setUpGenericSeriesPoster(item);
        }
    }

    private void setUpGenericSeriesPoster(RemoteViews item) {
        item.setImageViewResource(R.id.seriesPoster, R.drawable.generic_poster_thumbnail);
    }

    private void setUpEpisodeAirdate(RemoteViews item, Episode episode) {
        RelativeDay relativeDay = DatesAndTimes.parse(episode.airDate(), null);
        String airDateString = DatesAndTimes.toString(episode.airDate(), DateFormat.getDateFormat(context), "");
        String relativeDayString = LocalText.of(relativeDay, airDateString);

        if (relativeDay != null && shouldShowWeekday(relativeDay)) {
            String weekday = DatesAndTimes.toString(episode.airDate(), DateFormats.forShortWeekDay(Locale.getDefault()), "");

            item.setTextViewText(R.id.episodeAirDate, weekday + ", " + relativeDayString.toLowerCase());
        } else {
            item.setTextViewText(R.id.episodeAirDate, relativeDayString);
        }
    }

    private boolean shouldShowWeekday(RelativeDay relativeDay) {
        return (relativeDay.isInLessThanAWeek() && !relativeDay.isTomorrow()) ||
                (relativeDay.wasLessThanAWeekAgo() && !relativeDay.isYesterday());
    }

    private void setUpAirtimeAndNetwork(RemoteViews item, Series series) {
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(context), "");
        String network = series.network();

        item.setTextViewText(R.id.airtimeAndNetwork, Strings.concat(airtime, network, " "));
    }

    private void setUpSeriesName(RemoteViews item, Series series) {
        item.setTextViewText(R.id.seriesName, series.name());
    }

    private void setUpEpisodeNumber(RemoteViews item, Episode episode) {
        item.setTextViewText(
                R.id.episodeNumber,
                context.getString(R.string.episode_number_format, episode.seasonNumber(), episode.number()));
    }

    private void setUpEpisodeTitle(RemoteViews item, Episode episode) {
        item.setTextViewText(R.id.episodeTitle, episode.title());
    }

    private void setUpOnClickFillInIntent(RemoteViews item, int scheduleMode, int position) {
        Bundle extras = new Bundle();

        extras.putInt(Extra.SCHEDULE_MODE, scheduleMode);
        extras.putInt(Extra.POSITION, position);

        Intent intent = new Intent().putExtras(extras);

        item.setOnClickFillInIntent(R.id.itemPanel, intent);
    }
}
