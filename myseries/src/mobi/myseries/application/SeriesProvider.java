/*
 *   SeriesProvider.java
 *
 *   Copyright 2012 MySeries Team.
 *
 *   This file is part of MySeries.
 *
 *   MySeries is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   MySeries is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MySeries.  If not, see <http://www.gnu.org/licenses/>.
 */

package mobi.myseries.application;

import java.util.Collection;
import java.util.LinkedList;

import mobi.myseries.application.broadcast.BroadcastService;
import mobi.myseries.domain.model.Episode;
import mobi.myseries.domain.model.Season;
import mobi.myseries.domain.model.Series;
import mobi.myseries.domain.repository.series.SeriesRepository;
import mobi.myseries.shared.Validate;

public class SeriesProvider {
    private final SeriesRepository seriesRepository;
    private BroadcastService broadcastService;

    // TODO (Cleber) Remove this constructor or create a SeenMarkService
    // receiving a BroadcastService
    public SeriesProvider(SeriesRepository seriesRepository) {
        Validate.isNonNull(seriesRepository, "seriesRepository");

        this.seriesRepository = seriesRepository;
    }

    public SeriesProvider(SeriesRepository seriesRepository, BroadcastService broadcastService) {
        Validate.isNonNull(seriesRepository, "seriesRepository");
        Validate.isNonNull(broadcastService, "broadcastService");

        this.seriesRepository = seriesRepository;
        this.broadcastService = broadcastService;
    }

    public Collection<Series> followedSeries() {
        return this.seriesRepository.getAll();
    }

    public Series getSeries(int seriesId) {
        return this.seriesRepository.get(seriesId);
    }

    public Collection<Series> getAllSeries(int[] seriesIds) {
        Collection<Series> allSeries = new LinkedList<Series>();

        for (int i : seriesIds) {
            allSeries.add(this.getSeries(i));
        }

        return allSeries;
    }

    // SeenMark----------------------------------------------------------------------------------------------------------

    public void markSeasonAsSeen(Season season) {
        season.markAsSeen();
        this.seriesRepository.updateAllEpisodes(season.episodes());
        this.broadcastService.broadcastSeenMarkup();
    }

    public void markSeasonAsNotSeen(Season season) {
        season.markAsNotSeen();
        this.seriesRepository.updateAllEpisodes(season.episodes());
        this.broadcastService.broadcastSeenMarkup();
    }

    public void markEpisodeAsSeen(Episode episode) {
        episode.markAsWatched();
        this.seriesRepository.update(episode);
        this.broadcastService.broadcastSeenMarkup();
    }

    public void markEpisodeAsNotSeen(Episode episode) {
        episode.markAsUnwatched();
        this.seriesRepository.update(episode);
        this.broadcastService.broadcastSeenMarkup();
    }

    public void markSeriesAsSeen(Series series) {
        series.markAsSeen();
        this.seriesRepository.update(series);
        this.broadcastService.broadcastSeenMarkup();
    }

    public void markSeriesAsNotSeen(Series series) {
        series.markAsNotSeen();
        this.seriesRepository.update(series);
        this.broadcastService.broadcastSeenMarkup();
    }
}
