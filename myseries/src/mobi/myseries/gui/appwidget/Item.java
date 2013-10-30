package mobi.myseries.gui.appwidget;

import mobi.myseries.R;
import mobi.myseries.application.App;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Series;
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
import android.view.View;
import android.widget.RemoteViews;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

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

    public RemoteViews createFor(Episode episode) {
        Series series = App.seriesFollowingService().getFollowedSeries(episode.seriesId());

        RemoteViews item = new RemoteViews(context.getPackageName(), R.layout.schedulewidget_item);

        setUpSeriesPoster(item, series);
        setUpEpisodeAirdate(item, episode);
        setUpAirtimeAndNetwork(item, series);
        setUpSeriesName(item, series);
        setUpEpisodeName(item, episode);
        setUpOnClickIntent(item, episode);

        return item;
    }

    private void setUpSeriesPoster(final RemoteViews item, Series series) {
        String seriesPoster = App.imageService().getPosterOf(series);
       UniversalImageLoader.loader().loadImage(seriesPoster, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    item.setImageViewBitmap(R.id.seriesPoster, loadedImage);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view,
                FailReason failReason) {
                    item.setImageViewResource(R.id.seriesPoster, R.drawable.generic_poster);
                }
        });
        
    }

    private void setUpEpisodeAirdate(RemoteViews item, Episode episode) {
        RelativeDay relativeAirDay = DatesAndTimes.parse(episode.airDate(), null);
        String airDate = DatesAndTimes.toString(episode.airDate(), DateFormat.getDateFormat(context), "");

        item.setTextViewText(R.id.episodeAirDate, LocalText.of(relativeAirDay, airDate));
    }

    private void setUpAirtimeAndNetwork(RemoteViews item, Series series) {
        String airtime = DatesAndTimes.toString(series.airtime(), DateFormat.getTimeFormat(context), "");
        String network = series.network();

        item.setTextViewText(R.id.airtimeAndNetwork, Strings.concat(airtime, network, " - "));
    }

    private void setUpSeriesName(RemoteViews item, Series series) {
        item.setTextViewText(R.id.seriesName, series.name());
    }

    private void setUpEpisodeName(RemoteViews item, Episode episode) {
        String format = context.getString(R.string.episode_number_format);
        String episodeNumber = String.format(format, episode.seasonNumber(), episode.number());

        item.setTextViewText(R.id.episodeNumber, episodeNumber + " " + episode.title());
    }

    private void setUpOnClickIntent(RemoteViews item, Episode episode) {
        setupOnClickFillInIntent(item, episode);
    }

    private void setupOnClickFillInIntent(RemoteViews item, Episode episode) {
        Bundle extras = new Bundle();

        extras.putInt(Extra.SERIES_ID, episode.seriesId());
        extras.putInt(Extra.SEASON_NUMBER, episode.seasonNumber());
        extras.putInt(Extra.EPISODE_NUMBER, episode.number());

        Intent intent = new Intent().putExtras(extras);

        item.setOnClickFillInIntent(R.id.itemPanel, intent);
    }
}
