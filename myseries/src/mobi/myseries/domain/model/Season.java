package mobi.myseries.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import mobi.myseries.shared.Specification;
import mobi.myseries.shared.Validate;

public class Season {
    public static int SPECIAL_SEASON_NUMBER = 0;

    private final int seriesId;
    private final int number;
    private final TreeMap<Integer, Episode> episodes;

    public Season(int seriesId, int number) {
        Validate.isTrue(number >= 0, "number should be non-negative");

        this.seriesId = seriesId;
        this.number = number;

        this.episodes = new TreeMap<Integer, Episode>();
    }

    public int seriesId() {
        return this.seriesId;
    }

    public int number() {
        return this.number;
    }

    public boolean isSpecial() {
        return this.number == SPECIAL_SEASON_NUMBER;
    }

    public int numberOfEpisodes() {
        return this.episodes.size();
    }

    public int numberOfEpisodes(Specification<Episode> specification) {
        return this.episodesBy(specification).size();
    }

    public boolean includes(Episode episode) {
        return episode != null && this.episodes.containsKey(episode.number());
    }

    public int positionOf(Episode episode) {
        Validate.isNonNull(episode, "episode");

        return this.positionOf(episode.number());
    }

    public int positionOf(int episodeNumber) {
        int i = 0;

        for (Entry<Integer, Episode> entry : this.episodes.entrySet()) {
            if (entry.getKey() == episodeNumber) {
                return i;
            }

            i++;
        }

        return -1;
    }

    public Episode episode(int number) {
        return this.episodes.get(number);
    }

    public Episode episodeAt(int position) {
        int i = 0;

        for (Entry<Integer, Episode> entry : this.episodes.entrySet()) {
            if (i == position) {
                return entry.getValue();
            }

            i++;
        }

        throw new IndexOutOfBoundsException(
            "position " + position + " is not in the range [0, " + this.numberOfEpisodes() + ")");
    }

    public Episode nextEpisodeToWatch() {
        for (Entry<Integer, Episode> entry: this.episodes.entrySet()) {
            if (entry.getValue().unwatched()) {
                return entry.getValue();
            }
        }

        return null;
    }

    public List<Episode> episodes() {
        return new ArrayList<Episode>(this.episodes.values());
    }

    public List<Episode> episodesBy(Specification<Episode> specification) {
        Validate.isNonNull(specification, "specification");

        List<Episode> result = new ArrayList<Episode>();

        for (Entry<Integer, Episode> entry: this.episodes.entrySet()) {
            if (specification.isSatisfiedBy(entry.getValue())) {
                result.add(entry.getValue());
            }
        }

        return result;
    }

    public Season markAsWatched() {
        for (Entry<Integer, Episode> entry: this.episodes.entrySet()) {
            entry.getValue().markAsWatched();
        }

        return this;
    }

    public Season markAsUnwatched() {
        for (Entry<Integer, Episode> entry: this.episodes.entrySet()) {
            entry.getValue().markAsUnwatched();
        }

        return this;
    }

    public Season include(Episode e) {
        if (canInclude(e)) {
            this.episodes.put(e.number(), e);
        }

        return this;
    }

    private boolean canInclude(Episode e) {
        return e != null &&
                e.seriesId() == seriesId &&
                e.seasonNumber() == number &&
                !includes(e);
    }

    public Season mergeWith(Season other) {
        Validate.isTrue(this.equals(other), "other should be equals to this");

        this.mergeExistingEpisodesThatStillExistIn(other);
        this.insertNewEpisodesFrom(other);
        this.removeEpisodesThatNoLongerExistIn(other);

        return this;
    }

    private void mergeExistingEpisodesThatStillExistIn(Season other) {
        for (Episode e : this.episodes.values()) {
            if (other.includes(e)) {
                e.mergeWith(other.episode(e.number()));
            }
        }
    }

    private void insertNewEpisodesFrom(Season other) {
        for (Episode e : other.episodes.values()) {
            if (!this.includes(e)) {
                this.episodes.put(e.number(), e);
            }
        }
    }

    private void removeEpisodesThatNoLongerExistIn(Season other) {
        List<Episode> myEpisodes = this.episodes();

        for (Episode e : myEpisodes) {
            if (!other.includes(e)) {
                this.episodes.remove(e.number());
            }
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        return (prime * (prime + this.seriesId)) + this.number;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Season)) {
            return false;
        }

        Season other = (Season) obj;

        return other.seriesId == this.seriesId && other.number == this.number;
    }
}
