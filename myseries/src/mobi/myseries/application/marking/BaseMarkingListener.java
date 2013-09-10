package mobi.myseries.application.marking;

import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;

public class BaseMarkingListener implements MarkingListener {

    @Override
    public void onMarked(Series markedSeries) { }

    @Override
    public void onMarked(Season markedSeason) { }

    @Override
    public void onMarked(Episode markedEpisode) { }
}
