package mobi.myseries.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

import mobi.myseries.shared.DatesAndTimes;
import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class SeasonSet {
    private final int seriesId;
    private final ConcurrentSkipListMap<Integer, Season> seasons;

    public SeasonSet(int seriesId) {
        Validate.isTrue(seriesId >= 0, "seriesId should be non-negative");

        this.seriesId = seriesId;
        this.seasons = new ConcurrentSkipListMap<>();
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int numberOfSeasons() {
        return this.seasons.size();
    }

    public int numberOfEpisodes() {
        int numberOfEpisodes = 0;

        for (Entry<Integer, Season> entry : this.seasons.entrySet()) {
            numberOfEpisodes += entry.getValue().numberOfEpisodes();
        }

        return numberOfEpisodes;
    }

    public int numberOfEpisodes(Specification<Episode> specification) {
        return this.episodesBy(specification).size();
    }

    public boolean hasSpecialEpisodes() {
        return this.seasons.containsKey(Season.SPECIAL_SEASON_NUMBER);
    }

    public boolean includes(Season season) {
        return season != null && this.seasons.containsKey(season.number());
    }

    public int positionOf(Season season) {
        Validate.isNonNull(season, "season");

        return this.positionOf(season.number());
    }

    public int positionOf(int seasonNumber) {
        int i = 0;

        for (Entry<Integer, Season> entry : this.seasons.entrySet()) {
            if (entry.getKey() == seasonNumber) {
                return i;
            }

            i++;
        }

        return -1;
    }

    public Season season(int number) {
        return this.seasons.get(number);
    }

    public Season seasonAt(int position) {
        int i = 0;

        for (Entry<Integer,Season> entry : this.seasons.entrySet()) {
            if (i == position) {
                return entry.getValue();
            }

            i++;
        }

        throw new IndexOutOfBoundsException(
            "position " + position + " is not in the range [0, " + this.numberOfSeasons() + ")");
    }

    private Season ensuredSeason(int number) {
        if (!this.seasons.containsKey(number)) {
            Season season = new Season(this.seriesId, number);

            this.seasons.put(season.number(), season);
        }

        return this.season(number);
    }

    public Episode nextEpisodeToWatch(boolean includingSpecialEpisodes) {
        if (!includingSpecialEpisodes) {
            return this.nextNonSpecialEpisodeToWatch();
        }

        Episode special = this.nextNonSpecialEpisodeToWatch();
        Episode nonSpecial = this.nextSpecialEpisodeToWatch();

        if (special == null) { return nonSpecial; }
        if (nonSpecial == null) { return special; }

        boolean specialBefore = DatesAndTimes.compareByNullLast(special.airDate(), nonSpecial.airDate()) < 0;

        return specialBefore ? special : nonSpecial;
    }

    private Episode nextSpecialEpisodeToWatch() {
        Season specialEpisodes = this.season(Season.SPECIAL_SEASON_NUMBER);

        if (specialEpisodes == null) { return null;}

        return specialEpisodes.nextEpisodeToWatch();
    }

    private Episode nextNonSpecialEpisodeToWatch() {
        for (Entry<Integer, Season> entry : this.seasons.entrySet()) {
            Season s = entry.getValue();

            if (s.isSpecial()) { continue; }

            Episode e = s.nextEpisodeToWatch();

            if (e != null) { return e; }
        }

        return null;
    }

    public List<Season> seasons() {
        return new ArrayList<Season>(this.seasons.values());
    }

    public List<Episode> episodes() {
        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.episodes());
        }

        return episodes;
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        Validate.isNonNull(specification, "specification");

        List<Episode> episodes = new ArrayList<Episode>();

        for (Season s : this.seasons.values()) {
            episodes.addAll(s.episodesBy(specification));
        }

        return episodes;
    }

    public SeasonSet markAsWatched() {
        for (Entry<Integer, Season> entry: this.seasons.entrySet()) {
            entry.getValue().markAsWatched();
        }

        return this;
    }

    public SeasonSet markAsUnwatched() {
        for (Entry<Integer, Season> entry: this.seasons.entrySet()) {
            entry.getValue().markAsUnwatched();
        }

        return this;
    }

    public SeasonSet include(Episode episode) {
        Validate.isNonNull(episode, "episode");

        this.ensuredSeason(episode.seasonNumber()).include(episode);

        return this;
    }

    public SeasonSet includeAll(Collection<Episode> episodes) {
        Validate.isNonNull(episodes, "items");

        for (Episode e : episodes) {
            this.include(e);
        }

        return this;
    }

    public SeasonSet mergeWith(SeasonSet other) {
        Validate.isNonNull(other, "other");
        Validate.isTrue(this.seriesId == other.seriesId, "other should have same seriesId as this");

        this.mergeExistingSeasonsThatStillExistIn(other);
        this.insertNewSeasonsFrom(other);
        this.removeSeasonsThatNoLongerExistIn(other);

        return this;
    }

    private void mergeExistingSeasonsThatStillExistIn(SeasonSet other) {
        for (Season s : this.seasons.values()) {
            if (other.includes(s)) {
                s.mergeWith(other.season(s.number()));
            }
        }
    }

    private void insertNewSeasonsFrom(SeasonSet other) {
        for (Season s : other.seasons.values()) {
            if (!this.includes(s)) {
                this.seasons.put(s.number(), s);
            }
        }
    }

    private void removeSeasonsThatNoLongerExistIn(SeasonSet other) {
        List<Season> mySeasons = this.seasons();

        for (Season s : mySeasons) {
            if (!other.includes(s)) {
                this.seasons.remove(s.number());
            }
        }
    }
}
